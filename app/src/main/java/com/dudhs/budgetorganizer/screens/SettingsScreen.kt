package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.budgetComponents.*
import com.dudhs.budgetorganizer.dataClasses.BudgetStateDataClass
import com.dudhs.budgetorganizer.dataClasses.ChartStyle
import com.dudhs.budgetorganizer.dataClasses.SettingsActions
import com.dudhs.budgetorganizer.helpers.formatMoney
import com.dudhs.budgetorganizer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: BudgetStateDataClass,
    currentAppTheme: AppTheme,
    actions: SettingsActions
) {
    var activeDialog by remember { mutableStateOf<SettingsDialog>(SettingsDialog.None) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Budget Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        SettingsSection("Appearance") {
            SettingsSegmentedRow(
                options = ChartStyle.values().toList(),
                selected = uiState.chartStyle,
                label = { it.label },
                onSelect = actions.onChartStyleChange
            )
            SettingsSegmentedRow(
                options = AppTheme.values().toList(),
                selected = currentAppTheme,
                label = { it.label },
                onSelect = actions.onThemeChange
            )
        }

        SettingsSection("Money") {
            VariableItem(
                icon = Icons.Default.AccountBox,
                title = "Monthly Income",
                subtitle = formatMoney(uiState.baseSalary),
                onClick = { activeDialog = SettingsDialog.Salary }
            )
            VariableItem(
                icon = Icons.Default.CheckCircle,
                title = "One-time Income",
                subtitle = "Current total: ${formatMoney(uiState.suddenIncome)}",
                onClick = { activeDialog = SettingsDialog.AddIncome }
            )
            VariableItem(
                icon = Icons.Default.Clear,
                title = "One-time Expense",
                subtitle = "Current total: ${formatMoney(uiState.suddenExpense)}",
                onClick = { activeDialog = SettingsDialog.AddExpense }
            )
            VariableItem(
                icon = Icons.Default.DateRange,
                title = "Spending Limit",
                subtitle = formatMoney(uiState.monthlyTarget),
                onClick = { activeDialog = SettingsDialog.SpendingLimit }
            )
            VariableItem(
                icon = Icons.Default.Edit,
                title = "Set Bank Balance",
                subtitle = "Override to: ${formatMoney(uiState.estimatedBankBalance)}",
                onClick = { activeDialog = SettingsDialog.BankBalance }
            )
        }

        SettingsSection("Month Cycle") {
            SettingsToggleCard(
                title = "Manual Refresh Mode",
                subtitle = "Disable auto-reset on 1st of month",
                checked = uiState.isManualRefresh,
                onCheckedChange = actions.onToggleManualRefresh
            )
            if (uiState.isManualRefresh) {
                VariableItem(
                    icon = Icons.Default.Refresh,
                    title = "Reset Month Now",
                    subtitle = "Clear inputs and start a new cycle",
                    onClick = { activeDialog = SettingsDialog.ConfirmReset }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    SettingsDialogHost(
        dialog = activeDialog,
        actions = actions,
        onDismiss = { activeDialog = SettingsDialog.None }
    )
}