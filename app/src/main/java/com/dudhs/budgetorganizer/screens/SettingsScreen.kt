package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.dataClasses.BudgetStateDataClass
import com.dudhs.budgetorganizer.helpers.formatMoney

enum class ChartStyle(val label: String) {
    PIE("Pie"), PROGRESS("Progress")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: BudgetStateDataClass,
    onSalaryChange: (Float) -> Unit,
    onAddIncome: (Float) -> Unit,
    onAddExpense: (Float) -> Unit,
    onMonthlyTarget: (Float) -> Unit,
    onChartStyleChange: (ChartStyle) -> Unit,
    onToggleManualRefresh: (Boolean) -> Unit,
    onManualReset: () -> Unit
) {
    var activeDialog by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Budget Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,

        )

        Spacer(modifier = Modifier.height(12.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.widthIn(max = 340.dp).padding(vertical = 8.dp)
        ) {
            ChartStyle.values().forEachIndexed { index, style ->
                SegmentedButton(
                    selected = uiState.chartStyle == style,
                    onClick = { onChartStyleChange(style) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ChartStyle.values().size)
                ) {
                    Text(style.label)
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Manual Refresh Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Disable auto-reset on 1st of month", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = uiState.isManualRefresh, onCheckedChange = onToggleManualRefresh)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        VariableItem(
            icon = Icons.Default.AccountBox,
            title = "Monthly Income",
            subtitle = formatMoney(uiState.baseSalary),
            onClick = { activeDialog = 1 }
        )
        VariableItem(
            icon = Icons.Default.CheckCircle,
            title = "One-time Income",
            subtitle = "Current total: ${formatMoney(uiState.suddenIncome)}",
            onClick = { activeDialog = 2 }
        )
        VariableItem(
            icon = Icons.Default.Clear,
            title = "One-time Expense",
            subtitle = "Current total: ${formatMoney(uiState.suddenExpense)}",
            onClick = { activeDialog = 3 }
        )
        VariableItem(
            icon = Icons.Default.DateRange,
            title = "Spending Limit",
            subtitle = formatMoney(uiState.monthlyTarget),
            onClick = { activeDialog = 4 }
        )
        if (uiState.isManualRefresh) {
            VariableItem(
                icon = Icons.Default.Refresh,
                title = "Reset Month Now",
                subtitle = "Clear inputs and start a new cycle",
                onClick = { activeDialog = 5 }
            )
        }

    }

    when (activeDialog) {
        1 -> NumberInputDialog(
            title = "Set Monthly Income",
            onDismiss = { activeDialog = 0 },
            onConfirm = { onSalaryChange(it); activeDialog = 0 }
        )
        2 -> NumberInputDialog(
            title = "Add One-time Income",
            onDismiss = { activeDialog = 0 },
            onConfirm = { onAddIncome(it); activeDialog = 0 }
        )
        3 -> NumberInputDialog(
            title = "Add One-time Expense",
            onDismiss = { activeDialog = 0 },
            onConfirm = { onAddExpense(it); activeDialog = 0 }
        )
        4 -> NumberInputDialog(
            title = "Set Spending Limit",
            onDismiss = { activeDialog = 0 },
            onConfirm = { onMonthlyTarget(it); activeDialog = 0 }
        )
        5 -> AlertDialog(
            onDismissRequest = { activeDialog = 0 },
            title = { Text("Start New Month") },
            text = { Text("This will clear sudden incomes/expenses and start a new transaction cycle. Continue?") },
            confirmButton = {
                TextButton(onClick = { onManualReset(); activeDialog = 0 }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialog = 0 }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun VariableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NumberInputDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val parsedAmount = text.replace(',', '.').toFloatOrNull()
    val isValid = parsedAmount != null && parsedAmount >= 0f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue.filter { it.isDigit() || it == '.' || it == ',' }
                },
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = text.isNotBlank() && !isValid
            )
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = { parsedAmount?.let { onConfirm(it) } }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
