package com.dudhs.budgetorganizer.helpers

import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass


fun filterTransactionsByName(
    transactions: List<TransactionDataClass>,
    query: String
): List<TransactionDataClass> {
    if (query.isBlank()) return transactions

    // Filters the list, ignoring uppercase/lowercase differences
    return transactions.filter { transaction ->
        transaction.merchant.contains(query, ignoreCase = true)
    }
}