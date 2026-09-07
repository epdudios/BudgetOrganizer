package com.dudhs.budgetorganizer.helpers

import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import java.util.Calendar

data class Membership(
    val merchant: String,
    val lastAmount: Float,
    val isManual: Boolean
)

object MembershipLogic {

    private fun monthKeyOf(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
    }

    fun detectMemberships(
        transactions: List<TransactionDataClass>,
        preferencesManager: PreferencesManager
    ): List<Membership> {
        val now = Calendar.getInstance()
        val currentKey = "${now.get(Calendar.YEAR)}-${now.get(Calendar.MONTH)}"
        val prev = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val previousKey = "${prev.get(Calendar.YEAR)}-${prev.get(Calendar.MONTH)}"

        val expenses = transactions.filter { (it.amount.toDoubleOrNull() ?: 0.0) > 0.0 }

        val byMerchant = expenses.groupBy { it.merchant.trim().uppercase() }

        val detected = byMerchant.mapNotNull { (_, group) ->
            val months = group.map { monthKeyOf(it.timestamp) }.toSet()
            if (currentKey in months && previousKey in months) {
                val latest = group.maxByOrNull { it.timestamp }!!
                Membership(
                    merchant = latest.merchant.trim(),
                    lastAmount = (latest.amount.toDoubleOrNull() ?: 0.0).toFloat(),
                    isManual = false
                )
            } else null
        }

        val ignored = preferencesManager.getIgnoredMemberships()
        val manual = preferencesManager.getManualMemberships()
            .filter { name -> detected.none { it.merchant.equals(name, ignoreCase = true) } }
            .map { Membership(merchant = it, lastAmount = 0f, isManual = true) }

        return (detected.filter { it.merchant.uppercase() !in ignored } + manual)
            .sortedBy { it.merchant }
    }
}