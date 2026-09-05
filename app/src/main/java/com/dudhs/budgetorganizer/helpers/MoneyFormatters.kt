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

fun normalizeAmount(raw: String): String {
    val cleaned = raw.trim().replace(" ", "")

    val lastDot = cleaned.lastIndexOf('.')
    val lastComma = cleaned.lastIndexOf(',')

    return when {
        lastDot != -1 && lastComma != -1 -> {
            if (lastComma > lastDot) {
                cleaned.replace(".", "").replace(",", ".")
            } else {
                cleaned.replace(",", "")
            }
        }
        lastComma != -1 -> {
            val decimals = cleaned.length - lastComma - 1
            if (decimals == 2 || decimals == 1) {
                cleaned.replace(",", ".")
            } else {
                cleaned.replace(",", "")
            }
        }
        lastDot != -1 -> {
            val decimals = cleaned.length - lastDot - 1
            if (decimals == 2 || decimals == 1) {
                cleaned
            } else {
                cleaned.replace(".", "")
            }
        }
        else -> cleaned
    }
}
