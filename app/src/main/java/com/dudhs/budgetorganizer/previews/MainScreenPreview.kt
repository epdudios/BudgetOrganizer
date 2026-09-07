//package com.dudhs.budgetorganizer.previews
//
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.dudhs.budgetorganizer.dataClasses.BudgetStateDataClass
//import com.dudhs.budgetorganizer.dataClasses.ChartStyle
//import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
//import com.dudhs.budgetorganizer.screens.MainBudgetScreen
//
//@Preview(showBackground = true, name = "Main Budget Screen - Light Mode")
//@Composable
//fun MainBudgetScreenPreview() {
//    MaterialTheme {
//        val mockUiState = BudgetStateDataClass(
//            transactions = listOf(
//                TransactionDataClass("31/05/2026", "20:15", "46.90", "H&B KOLONAKIOU, CYPRUS", System.currentTimeMillis()),
//                TransactionDataClass("30/05/2026", "12:45", "20.00", "ESSO AYIAS FYLAXEOS, CYPRUS", System.currentTimeMillis() - 86400000)
//            ),
//            baseSalary = 1400f,
//            suddenIncome = 0f,
//            suddenExpense = 0f,
//            estimatedBankBalance = 936.92f,
//            monthlyTarget = 1000f,
//            chartStyle = ChartStyle.PROGRESS,
//            effectiveStartMillis = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000),
//            isManualRefresh = false
//        )
//
//        MainBudgetScreen(uiState = mockUiState)
//    }
//}