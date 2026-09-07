package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.helpers.ReportLogic
import com.dudhs.budgetorganizer.helpers.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportScreen(
    transactions: List<TransactionDataClass>,
    onBack: () -> Unit
) {
    val reports = remember(transactions) { ReportLogic.buildMonthlyReports(transactions) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Monthly Reports") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (reports.isEmpty()) {
            EmptyState(message = "No expenses to report yet.")
            return
        }

        val report = reports[selectedIndex.coerceIn(reports.indices)]

        // Month selector
        ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 16.dp) {
            reports.forEachIndexed { index, r ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { Text(r.monthLabel) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total spent in ${report.monthLabel}", style = MaterialTheme.typography.labelLarge)
                        Text(
                            formatMoney(report.totalSpent),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Text("By category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(report.categoryTotals) { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(cat.category.label)
                    Text(formatMoney(cat.total), fontWeight = FontWeight.SemiBold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("By vendor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(report.merchantTotals) { m ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(m.merchant, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${m.category.label} · ${m.count}x",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(formatMoney(m.total), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}