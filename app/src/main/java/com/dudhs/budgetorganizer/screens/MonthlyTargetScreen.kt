package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.dataClasses.BudgetStateDataClass
import com.dudhs.budgetorganizer.budgetComponents.MonthlyTargetCard
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.dataClasses.ChartStyle
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.jaikeerthick.composable_graphs.composables.pie.PieChart
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData


//@Preview(showBackground = true, name = "Light Mode")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTargetScreen(
    uiState: BudgetStateDataClass,
    notificationHelper: NotificationHelper,
    preferencesManager: PreferencesManager,
    onTransactionClick: (TransactionDataClass) -> Unit,
) {
    var showDetails by remember { mutableStateOf(false) }

    if (showDetails) {
        TransactionListScreen(
            transactions = uiState.transactions,
            onBack = { showDetails = false },
            onTransactionClick = onTransactionClick
        )
    } else {
        val colorScheme = MaterialTheme.colorScheme
        val thisMonthTransactions = uiState.transactions.filter {
            it.timestamp >= uiState.effectiveStartMillis
        }

        val spent = thisMonthTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat().coerceAtLeast(0f)
        val remaining = (uiState.monthlyTarget - spent).coerceAtLeast(0f)
        val progress = if (uiState.monthlyTarget > 0f) (spent / uiState.monthlyTarget).coerceIn(0f, 1f) else 0f
        val isOverTarget = uiState.monthlyTarget > 0f && spent > uiState.monthlyTarget
        val spentColor = if (isOverTarget) colorScheme.error else colorScheme.primary
        val remainingColor = Color(0xFF2E7D32)


        LaunchedEffect(spent, uiState.monthlyTarget) {
            preferencesManager.setTrackedSpentThisMonth(spent)

            if (uiState.monthlyTarget > 0) {
                val percent = ((spent / uiState.monthlyTarget) * 100).toInt()
                val currentTier = (percent / 10) * 10
                val lastNotifiedTier = preferencesManager.getLastNotifiedTier()

                if (currentTier >= 10 && currentTier > lastNotifiedTier) {
                    notificationHelper.showProgressWarning(spent, uiState.monthlyTarget, currentTier)
                    preferencesManager.setLastNotifiedTier(currentTier)
                }
            }
        }

        val pieChartData = listOf(
            PieData(value = spent.coerceAtLeast(0.01f), label = "Spent", color = spentColor),
            PieData(value = remaining.coerceAtLeast(0.01f), label = "Remaining", color = remainingColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Monthly Goal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

             MonthlyTargetCard(
                monthlyTarget = uiState.monthlyTarget,
                spent = spent,
                remaining = remaining,
                spentColor = spentColor,
                remainingColor = remainingColor
            )

            Spacer(modifier = Modifier.height(20.dp))



            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.monthlyTarget == 0f) {
                Text("Set a spending limit in Settings.", color = colorScheme.onSurfaceVariant)
            } else if (thisMonthTransactions.isEmpty()) {
                Text("No transactions this month.", color = colorScheme.onSurfaceVariant)
            } else if (uiState.chartStyle == ChartStyle.PIE) {
                PieChart(modifier = Modifier.size(250.dp), data = pieChartData)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% used",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = spentColor,
                        trackColor = colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { showDetails = true }) {
                Text("See Transactions")
            }
        }
    }
}

@Composable
private fun GoalMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
