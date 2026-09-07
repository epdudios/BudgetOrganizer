package com.dudhs.budgetorganizer.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dudhs.budgetorganizer.budgetComponents.NumberInputDialog
import com.dudhs.budgetorganizer.dataClasses.SettingsActions

sealed class SettingsDialog {
    object None : SettingsDialog()
    object Salary : SettingsDialog()
    object AddIncome : SettingsDialog()
    object AddExpense : SettingsDialog()
    object SpendingLimit : SettingsDialog()
    object BankBalance : SettingsDialog()
    object ConfirmReset : SettingsDialog()
    object AddBank : SettingsDialog()
}

@Composable
fun SettingsDialogHost(
    dialog: SettingsDialog,
    actions: SettingsActions,
    onDismiss: () -> Unit
) {
    when (dialog) {
        SettingsDialog.None -> Unit

        SettingsDialog.Salary -> NumberInputDialog(
            title = "Set Monthly Income",
            onDismiss = onDismiss,
            onConfirm = { actions.onSalaryChange(it); onDismiss() }
        )
        SettingsDialog.AddIncome -> NumberInputDialog(
            title = "Add One-time Income",
            onDismiss = onDismiss,
            onConfirm = { actions.onAddIncome(it); onDismiss() }
        )
        SettingsDialog.AddExpense -> NumberInputDialog(
            title = "Add One-time Expense",
            onDismiss = onDismiss,
            onConfirm = { actions.onAddExpense(it); onDismiss() }
        )
        SettingsDialog.SpendingLimit -> NumberInputDialog(
            title = "Set Spending Limit",
            onDismiss = onDismiss,
            onConfirm = { actions.onMonthlyTarget(it); onDismiss() }
        )
        SettingsDialog.BankBalance -> NumberInputDialog(
            title = "Set Current Bank Balance",
            onDismiss = onDismiss,
            onConfirm = { actions.onSetBalance(it); onDismiss() }
        )
        SettingsDialog.ConfirmReset -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Start New Month") },
            text = { Text("This will clear sudden incomes/expenses and start a new transaction cycle. Continue?") },
            confirmButton = {
                TextButton(onClick = { actions.onManualReset(); onDismiss() }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )
        SettingsDialog.AddBank -> {
            var text by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Add Bank") },
                text = {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Bank SMS sender name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(enabled = text.isNotBlank(), onClick = { actions.onAddBank(text); onDismiss() }) {
                        Text("Add")
                    }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
            )
        }
    }
}