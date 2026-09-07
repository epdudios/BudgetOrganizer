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
import com.dudhs.budgetorganizer.dataClasses.BudgetStateDataClass
import com.dudhs.budgetorganizer.helpers.LifeSavingsLogic
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import androidx.compose.foundation.Image
import com.dudhs.budgetorganizer.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.dataClasses.ChartStyle
import com.dudhs.budgetorganizer.dataClasses.SettingsActions
import com.dudhs.budgetorganizer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAppScreen(
    transactions: List<TransactionDataClass>,
    preferencesManager: PreferencesManager,
    notificationHelper: NotificationHelper,
    onTransactionClick: (TransactionDataClass) -> Unit,
    onThemeChange: (AppTheme) -> Unit

) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var currentSalary by remember { mutableFloatStateOf(preferencesManager.getSalary()) }
    var currentSuddenIncome by remember { mutableFloatStateOf(preferencesManager.getSuddenIncome()) }
    var currentSuddenExpense by remember { mutableFloatStateOf(preferencesManager.getSuddenExpense()) }
    var currentMonthlyTarget by remember { mutableFloatStateOf(preferencesManager.getMonthlyTarget()) }
    var currentChartStyle by remember { mutableStateOf(ChartStyle.valueOf(preferencesManager.getChartStyle()))}
    val lifeSavingsLogic = remember(preferencesManager) { LifeSavingsLogic(preferencesManager) }
    var balanceAdjustmentTrigger by remember { mutableIntStateOf(0) }
    val estimatedBankBalance by remember(transactions, currentSalary, currentSuddenIncome, currentSuddenExpense, balanceAdjustmentTrigger) {
        derivedStateOf { lifeSavingsLogic.calculateTotalLifeSavings(transactions) } }
    var isManualRefresh by remember { mutableStateOf(preferencesManager.isManualRefreshEnabled()) }
    var effectiveStartMillis by remember { mutableLongStateOf(preferencesManager.getEffectiveStartMillis()) }
    var currentAppTheme by remember { mutableStateOf(AppTheme.valueOf(preferencesManager.getAppTheme())) }

    val uiState = BudgetStateDataClass(
        transactions = transactions,
        baseSalary = currentSalary,
        suddenIncome = currentSuddenIncome,
        suddenExpense = currentSuddenExpense,
        monthlyTarget = currentMonthlyTarget,
        estimatedBankBalance = estimatedBankBalance,
        chartStyle = currentChartStyle,
        effectiveStartMillis = effectiveStartMillis,
        isManualRefresh = isManualRefresh
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier.height(36.dp)
                    )
                }
            )
        },
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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.TopCenter) {
            when (selectedTab) {
                0 -> MainBudgetScreen(uiState = uiState, preferencesManager= preferencesManager)
                1 -> MonthlyTargetScreen(
                    uiState = uiState,
                    notificationHelper = notificationHelper,
                    preferencesManager = preferencesManager,
                    onTransactionClick = onTransactionClick


                )
                2 -> SettingsScreen(
                    uiState = uiState,
                    currentAppTheme = currentAppTheme,
                    actions = SettingsActions(
                        onSalaryChange = { preferencesManager.setSalary(it); currentSalary = it },
                        onAddIncome = { preferencesManager.addSuddenIncome(it); currentSuddenIncome += it },
                        onAddExpense = { preferencesManager.addSuddenExpense(it); currentSuddenExpense += it },
                        onMonthlyTarget = {preferencesManager.setMonthlyTarget(it); currentMonthlyTarget = it },
                        onSetBalance = { target ->preferencesManager.setBalanceToTarget(target,lifeSavingsLogic.calculateTotalLifeSavings(transactions))
                            balanceAdjustmentTrigger++},
                        onChartStyleChange = {currentChartStyle = it; preferencesManager.setChartStyle(it.name) },
                        onThemeChange = { currentAppTheme = it; onThemeChange(it) },
                        onToggleManualRefresh = { enabled ->preferencesManager.setManualRefreshEnabled(enabled);isManualRefresh = enabled;effectiveStartMillis = preferencesManager.getEffectiveStartMillis() },
                        onManualReset = {preferencesManager.performManualReset()
                            currentSuddenIncome = 0f;currentSuddenExpense = 0f;effectiveStartMillis = preferencesManager.getEffectiveStartMillis()},
                        onAddBank = {preferencesManager.addBankName(name = it)}
                    )
                )
                }
            }
        }
    }

