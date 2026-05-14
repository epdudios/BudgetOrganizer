package com.dudhs.budgetorganizer.helpers

data class Transaction(
    val date: String,
    val time: String,
    val amount: String,
    val merchant: String,
    val timestamp: Long

)