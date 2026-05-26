package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.helpers.Transaction
import com.dudhs.budgetorganizer.helpers.CalendarDates
import com.dudhs.budgetorganizer.helpers.formatMoney
import com.jaikeerthick.composable_graphs.composables.pie.PieChart
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTargetScreen(
    allTransactions: List<Transaction>,
    monthlyTarget: Float,
    notificationHelper: NotificationHelper,
    preferencesManager: PreferencesManager,
    chartStyle: ChartStyle
) {
    var showDetails by remember { mutableStateOf(false) }

    if (showDetails) {
        TransactionListScreen(
            transactions = allTransactions,
            onBack = { showDetails = false }
        )
    } else {
        val colorScheme = MaterialTheme.colorScheme
        val thisMonthTransactions = allTransactions.filter {
            it.timestamp >= CalendarDates.getStartOfCurrentMonthMillis()
        }

        val spent = thisMonthTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat().coerceAtLeast(0f)
        val remaining = (monthlyTarget - spent).coerceAtLeast(0f)
        val progress = if (monthlyTarget > 0f) (spent / monthlyTarget).coerceIn(0f, 1f) else 0f
        val isOverTarget = monthlyTarget > 0f && spent > monthlyTarget
        val spentColor = if (isOverTarget) colorScheme.error else colorScheme.primary
        val remainingColor = Color(0xFF2E7D32)


        LaunchedEffect(spent, monthlyTarget) {
            preferencesManager.setTrackedSpentThisMonth(spent)

            if (monthlyTarget > 0 && spent >= (monthlyTarget / 2)) {
                if (!preferencesManager.isHalfwayWarningSent()) {
                    notificationHelper.showHalfwayWarning(spent, monthlyTarget)
                    preferencesManager.setHalfwayWarningSent(true)
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Monthly Goal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Spending limit", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)
                    Text(text = formatMoney(monthlyTarget), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GoalMetric("Spent", formatMoney(spent), spentColor)
                        GoalMetric("Remaining", formatMoney(remaining), remainingColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))



            Spacer(modifier = Modifier.height(24.dp))

            if (monthlyTarget == 0f) {
                Text("Set a spending limit in Settings.", color = colorScheme.onSurfaceVariant)
            } else if (thisMonthTransactions.isEmpty()) {
                Text("No transactions this month.", color = colorScheme.onSurfaceVariant)
            } else if (chartStyle == ChartStyle.PIE) {
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
