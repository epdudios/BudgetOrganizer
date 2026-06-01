package com.dudhs.budgetorganizer.dataClasses

data class TransactionDataClass(
    val date: String,
    val time: String,
    val amount: String,
    val merchant: String,
    val timestamp: Long

)