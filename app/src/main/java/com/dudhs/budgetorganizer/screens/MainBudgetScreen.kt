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
import com.dudhs.budgetorganizer.budgetComponents.MainBudgetScreenOverviewCard
import com.dudhs.budgetorganizer.dataClasses.ChartStyle
import com.dudhs.budgetorganizer.helpers.TimePeriod
import com.jaikeerthick.composable_graphs.composables.pie.PieChart
import com.jaikeerthick.composable_graphs.composables.pie.model.PieData
import java.util.Calendar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBudgetScreen(
    uiState: BudgetStateDataClass)
{
    var selectedPeriod by remember { mutableStateOf(TimePeriod.THIS_MONTH) }
   // var selectedChartStyle by remember { mutableStateOf(BudgetChartStyle.PIE) }
    val periods = TimePeriod.values()
   // val chartStyles = BudgetChartStyle.values()
    val colorScheme = MaterialTheme.colorScheme

    val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000
    val cutoff30Days = System.currentTimeMillis() - thirtyDaysInMillis

    val displayTransactions = when (selectedPeriod) {
        TimePeriod.ALL_TIME -> uiState.transactions
        TimePeriod.LAST_30_DAYS -> uiState.transactions.filter { it.timestamp >= cutoff30Days }
        TimePeriod.THIS_MONTH -> uiState.transactions.filter { it.timestamp >= uiState.effectiveStartMillis }
    }

    val uniqueMonthsCount = if (selectedPeriod == TimePeriod.ALL_TIME && displayTransactions.isNotEmpty()) {
        displayTransactions.map {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
        }.toSet().size
    } else {
        1
    }

    val rawBudget = (uiState.baseSalary * uniqueMonthsCount) + uiState.suddenIncome
    val totalBudget = minOf(rawBudget, uiState.estimatedBankBalance)

    val parsedSpent = displayTransactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toFloat()
    val spent = (parsedSpent + uiState.suddenExpense).coerceAtLeast(0f)
    val remaining = (totalBudget - spent).coerceAtLeast(0f)
    val progress = if (totalBudget > 0f) (spent / totalBudget).coerceIn(0f, 1f) else 0f
    val isOverBudget = totalBudget > 0f && spent > totalBudget

    val spentColor = if (isOverBudget) colorScheme.error else colorScheme.primary
    val remainingColor = Color(0xFF2E7D32)

    val pieChartData = listOf(
        PieData(value = spent.coerceAtLeast(0.01f), label = "Spent", color = spentColor),
        PieData(value = remaining.coerceAtLeast(0.01f), label = "Remaining", color = remainingColor)
    )
    var showDetails by remember { mutableStateOf(false) }

    if (showDetails) {
        TransactionListScreen(
            transactions = uiState.transactions,
            onBack = { showDetails = false },
            onTransactionClick = {}
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            periods.forEachIndexed { index, period ->
                SegmentedButton(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size)
                ) {
                    Text(text = period.label)
                }
            }
        }

        MainBudgetScreenOverviewCard(
            estimatedBankBalance = uiState.estimatedBankBalance,
            totalBudget = totalBudget,
            spent = spent,
            remaining = remaining,
            spentColor = spentColor,
            remainingColor = remainingColor,
            onClick = { showDetails = true }

        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isOverBudget) {
            Text(
                text = "Over budget",
                color = colorScheme.error,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (displayTransactions.isEmpty()) {
            Text(
                text = "No transactions found for this period.",
                color = colorScheme.onSurfaceVariant
            )
        } else if (uiState.chartStyle == ChartStyle.PIE){
            PieChart(
                modifier = Modifier.size(250.dp),
                data = pieChartData
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
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
    }
}

@Composable
private fun BudgetMetric(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
