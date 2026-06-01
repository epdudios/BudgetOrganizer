package com.dudhs.budgetorganizer.budgetComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.helpers.formatMoney


@Composable
fun MainBudgetScreenOverviewCard(
    estimatedBankBalance: Float,
    totalBudget: Float,
    spent: Float,
    remaining: Float,
    spentColor: Color,
    remainingColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Budget Remaining", style = MaterialTheme.typography.labelLarge, color = remainingColor)
            Text(formatMoney(remaining), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = remainingColor)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BudgetMetric("Monthly Budget", formatMoney(totalBudget), colorScheme.onSurface)
                BudgetMetric("Spent", formatMoney(spent), spentColor)
                BudgetMetric("Bank balance", formatMoney(estimatedBankBalance), colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun MonthlyTargetCard(
    monthlyTarget: Float,
    spent: Float,
    remaining: Float,
    spentColor: Color,
    remainingColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Spending limit", style = MaterialTheme.typography.labelLarge, color = colorScheme.onSurfaceVariant)
            Text(formatMoney(monthlyTarget), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BudgetMetric("Spent", formatMoney(spent), spentColor)
                BudgetMetric("Remaining", formatMoney(remaining), remainingColor)
            }
        }
    }
}

@Composable
fun BudgetMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}