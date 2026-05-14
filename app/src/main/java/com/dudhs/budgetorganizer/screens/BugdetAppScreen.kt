package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dudhs.budgetorganizer.LifeSavingsLogic
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.helpers.Transaction

@Composable
fun BudgetAppScreen(
    transactions: List<Transaction>,
    preferencesManager: PreferencesManager,
    notificationHelper: NotificationHelper

    //onExportClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var currentSalary by remember { mutableFloatStateOf(preferencesManager.getSalary()) }
    var currentSuddenIncome by remember { mutableFloatStateOf(preferencesManager.getSuddenIncome()) }
    var currentSuddenExpense by remember { mutableFloatStateOf(preferencesManager.getSuddenExpense()) }
    var currentMonthlyTarget by remember { mutableFloatStateOf(preferencesManager.getMonthlyTarget()) }
    var currentChartStyle by remember { mutableStateOf(ChartStyle.valueOf(preferencesManager.getChartStyle()))}
    val lifeSavingsLogic = remember(preferencesManager) { LifeSavingsLogic(preferencesManager) }
    val estimatedBankBalance by remember(transactions, currentSalary, currentSuddenIncome, currentSuddenExpense) {
        derivedStateOf { lifeSavingsLogic.calculateTotalLifeSavings(transactions) }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Overview") },
                    label = { Text("Overview") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Goal") },
                    label = { Text("Goal") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> {
                    BudgetPieChart(
                        allTransactions = transactions,
                        baseSalary = currentSalary,
                        suddenIncome = currentSuddenIncome,
                        suddenExpense = currentSuddenExpense,
                        estimatedBankBalance = estimatedBankBalance,
                        chartStyle = currentChartStyle
                    )
                }
                1 -> {
                    MonthlyTargetScreen(
                        transactions,
                        currentMonthlyTarget,
                        notificationHelper,
                        preferencesManager,
                        chartStyle = currentChartStyle
                    )
                    // Button(onClick = onExportClick) { Text("Process & Export Data") }
                }
                2 -> {
                    SettingsScreen(
                        salary = currentSalary,
                        suddenIncome = currentSuddenIncome,
                        suddenExpense = currentSuddenExpense,
                        monthlyTarget = currentMonthlyTarget,
                        chartStyle = currentChartStyle,
                        onSalaryChange = { preferencesManager.setSalary(it); currentSalary = it },
                        onAddIncome = { preferencesManager.addSuddenIncome(it); currentSuddenIncome += it },
                        onAddExpense = { preferencesManager.addSuddenExpense(it); currentSuddenExpense += it },
                        onMonthlyTarget = { preferencesManager.setMonthlyTarget(it); currentMonthlyTarget = it },
                         onChartStyleChange = { newStyle -> currentChartStyle = newStyle; preferencesManager.setChartStyle(newStyle.name) }
                    )
                }
            }
        }
    }
}
