package com.dudhs.budgetorganizer.helpers

import com.dudhs.budgetorganizer.dataClasses.ExpenseCategory
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MerchantTotal(
    val merchant: String,
    val category: ExpenseCategory,
    val total: Float,
    val count: Int
)

data class CategoryTotal(
    val category: ExpenseCategory,
    val total: Float
)

data class MonthlyReport(
    val monthKey: String,        // "2026-06" — for sorting
    val monthLabel: String,      // "June 2026" — for display
    val totalSpent: Float,
    val categoryTotals: List<CategoryTotal>,
    val merchantTotals: List<MerchantTotal>
)

object ReportLogic {

    fun buildMonthlyReports(transactions: List<TransactionDataClass>): List<MonthlyReport> {
        val labelFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        // Expenses only (in your data: expense = positive amount)
        val expenses = transactions.filter { (it.amount.toDoubleOrNull() ?: 0.0) > 0.0 }

        val byMonth = expenses.groupBy { transaction ->
            val cal = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            String.format(Locale.US, "%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        return byMonth.map { (monthKey, monthTransactions) ->
            val label = labelFormat.format(monthTransactions.first().timestamp)

            val merchantTotals = monthTransactions
                .groupBy { it.merchant.trim().uppercase() }
                .map { (_, group) ->
                    val name = group.first().merchant.trim()
                    MerchantTotal(
                        merchant = name,
                        category = ExpenseCategory.fromMerchant(name),
                        total = group.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat(),
                        count = group.size
                    )
                }
                .sortedByDescending { it.total }

            val categoryTotals = merchantTotals
                .groupBy { it.category }
                .map { (category, group) -> CategoryTotal(category, group.map { it.total }.sum()) }
                .sortedByDescending { it.total }

            MonthlyReport(
                monthKey = monthKey,
                monthLabel = label,
                totalSpent = merchantTotals.map { it.total }.sum(),
                categoryTotals = categoryTotals,
                merchantTotals = merchantTotals
            )
        }.sortedByDescending { it.monthKey }
    }
}