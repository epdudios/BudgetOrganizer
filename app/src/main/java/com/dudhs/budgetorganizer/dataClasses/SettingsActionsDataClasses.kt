package com.dudhs.budgetorganizer.dataClasses


import com.dudhs.budgetorganizer.ui.theme.AppTheme

data class SettingsActions(
    val onSalaryChange: (Float) -> Unit,
    val onAddIncome: (Float) -> Unit,
    val onAddExpense: (Float) -> Unit,
    val onMonthlyTarget: (Float) -> Unit,
    val onSetBalance: (Float) -> Unit,
    val onChartStyleChange: (ChartStyle) -> Unit,
    val onThemeChange: (AppTheme) -> Unit,
    val onToggleManualRefresh: (Boolean) -> Unit,
    val onManualReset: () -> Unit,
    val onAddBank: (String) -> Unit
)