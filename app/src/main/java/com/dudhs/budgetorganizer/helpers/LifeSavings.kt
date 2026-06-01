package com.dudhs.budgetorganizer.helpers

import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.iterator

data class MonthlySavings(
    val monthName: String,
    val savedAmount: Float
)

class LifeSavingsLogic(private val preferencesManager: PreferencesManager) {

    /**
     * Calculates how much money was saved each individual month.
     * Example: March 2026 -> 400€, April 2026 -> 600€
     */
    fun calculateMonthByMonthSavings(transactions: List<TransactionDataClass>): List<MonthlySavings> {
        val baseSalary = preferencesManager.getSalary()
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val groupedTransactions = transactions.groupBy { transaction ->
            val cal = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
            format.format(cal.time)
        }

        val monthlySavingsList = mutableListOf<MonthlySavings>()

        for ((monthString, monthTransactions) in groupedTransactions) {
            val spentThisMonth = monthTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat()
            val saved = baseSalary - spentThisMonth
            monthlySavingsList.add(MonthlySavings(monthString, saved))
        }
        return monthlySavingsList.reversed()
    }

    /**
     * Calculates an estimated bank balance based on monthly income minus parsed expenses.
     * It counts every calendar month from the first parsed transaction until the current month,
     * so the value naturally changes when a new month begins even before many new transactions exist.
     */
    fun calculateTotalLifeSavings(transactions: List<TransactionDataClass>): Float {
        val baseSalary = preferencesManager.getSalary()
        val globalSuddenIncome = preferencesManager.getSuddenIncome()
        val globalSuddenExpense = preferencesManager.getSuddenExpense()

        val monthsCount = calculateInclusiveMonthsFromFirstTransaction(transactions)
        val totalExpectedIncome = (baseSalary * monthsCount) + globalSuddenIncome
        val totalSmsSpent = transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat()
        val totalSpent = totalSmsSpent + globalSuddenExpense

        return (totalExpectedIncome - totalSpent).coerceAtLeast(0f)
    }

    private fun calculateInclusiveMonthsFromFirstTransaction(transactions: List<TransactionDataClass>): Int {
        if (transactions.isEmpty()) return 1

        val firstTransaction = transactions.minByOrNull { it.timestamp } ?: return 1
        val first = Calendar.getInstance().apply { timeInMillis = firstTransaction.timestamp }
        val now = Calendar.getInstance()

        val yearDiff = now.get(Calendar.YEAR) - first.get(Calendar.YEAR)
        val monthDiff = now.get(Calendar.MONTH) - first.get(Calendar.MONTH)
        return (yearDiff * 12 + monthDiff + 1).coerceAtLeast(1)
    }
}
