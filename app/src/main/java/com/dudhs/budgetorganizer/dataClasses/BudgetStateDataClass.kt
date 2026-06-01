package com.dudhs.budgetorganizer.dataClasses

import com.dudhs.budgetorganizer.screens.ChartStyle

data class BudgetStateDataClass(
    val transactions: List<TransactionDataClass>,
    val baseSalary: Float,
    val suddenIncome: Float,
    val suddenExpense: Float,
    val estimatedBankBalance: Float,
    val monthlyTarget: Float,
    val chartStyle: ChartStyle,
    val effectiveStartMillis: Long,
    val isManualRefresh: Boolean
)