package com.dudhs.budgetorganizer.helpers

import java.util.Locale
import kotlin.math.abs

fun formatMoney(value: Number): String {
    val amount = value.toDouble()
    val cleaned = if (abs(amount) < 0.005) 0.0 else amount
    return String.format(Locale.getDefault(), "€%,.2f", cleaned)
}

fun formatExpense(value: Number): String = "-${formatMoney(abs(value.toDouble()))}"

fun formatIncome(value: Number): String = "+${formatMoney(abs(value.toDouble()))}"
