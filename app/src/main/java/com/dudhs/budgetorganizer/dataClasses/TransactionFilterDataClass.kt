package com.dudhs.budgetorganizer.dataClasses

import com.dudhs.budgetorganizer.helpers.CalendarDates

enum class DateFilter(val label: String) {
    ALL("All time"),
    LAST_7("Last 7 days"),
    LAST_30("Last 30 days"),
    THIS_MONTH("This month")
}

enum class TypeFilter(val label: String) {
    ALL("All"),
    EXPENSES("Expenses only"),
    INCOME("Income only")
}

data class TransactionFilters(
    val dateFilter: DateFilter = DateFilter.ALL,
    val typeFilter: TypeFilter = TypeFilter.ALL,
    val minAmount: Float? = null,
    val maxAmount: Float? = null
)

fun applyFilters(
    transactions: List<TransactionDataClass>,
    filters: TransactionFilters
): List<TransactionDataClass> {
    val now = System.currentTimeMillis()
    val day = 24L * 60 * 60 * 1000

    val dateCutoff: Long = when (filters.dateFilter) {
        DateFilter.ALL -> 0L
        DateFilter.LAST_7 -> now - 7 * day
        DateFilter.LAST_30 -> now - 30 * day
        DateFilter.THIS_MONTH -> CalendarDates.getStartOfCurrentMonthMillis()
    }

    return transactions.filter { t ->
        val value = t.amount.toDoubleOrNull() ?: 0.0
        // In your data, expense = positive, income = negative
        val isIncome = value < 0.0
        val absValue = kotlin.math.abs(value).toFloat()

        val passesDate = t.timestamp >= dateCutoff
        val passesType = when (filters.typeFilter) {
            TypeFilter.ALL -> true
            TypeFilter.EXPENSES -> !isIncome
            TypeFilter.INCOME -> isIncome
        }
        val passesMin = filters.minAmount?.let { absValue >= it } ?: true
        val passesMax = filters.maxAmount?.let { absValue <= it } ?: true

        passesDate && passesType && passesMin && passesMax
    }
}