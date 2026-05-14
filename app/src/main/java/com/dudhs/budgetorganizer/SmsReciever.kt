package com.dudhs.budgetorganizer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.dudhs.budgetorganizer.helpers.NotificationHelper

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            val prefs = PreferencesManager(context)
            val notificationHelper = NotificationHelper(context)

            for (sms in messages) {
                val sender = sms.displayOriginatingAddress
                if (sender?.contains("ALPHA", ignoreCase = true) == true) {
                    val body = sms.displayMessageBody ?: ""

                    var expenseAmount = 0f

                    // 1. Check if it's a Standard Expense (and NOT an income/credit)
                    val amountRegex = Regex("([\\d.,]+)\\s*[EΕ]UR|[EΕ]UR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE)
                    val amountMatch = amountRegex.find(body)

                    if (amountMatch != null && !body.contains("ΠIΣTΩΣH", ignoreCase = true)) {
                        val rawAmount = amountMatch.groupValues[1].ifEmpty { amountMatch.groupValues[2] }
                        expenseAmount = rawAmount.replace(".", "").replace(",", ".").toFloatOrNull() ?: 0f
                    }

                    // 2. Check if it's a Direct Transfer Expense
                    if (body.contains("AMEΣH METAΦOPA", ignoreCase = true)) {
                        val transferMatch = Regex("€\\s*([\\d.,]+)").find(body)
                        if (transferMatch != null) {
                            expenseAmount = transferMatch.groupValues[1].replace(".", "").replace(",", ".").toFloatOrNull() ?: 0f
                        }
                    }

                    // 3. If we spent money, add it to our background tracker and check target
                    if (expenseAmount > 0f) {
                        prefs.addTrackedExpense(expenseAmount)

                        val currentSpent = prefs.getTrackedSpentThisMonth()
                        val target = prefs.getMonthlyTarget()

                        // If we crossed the 50% line and haven't been warned yet this month
                        if (target > 0 && currentSpent >= (target / 2)) {
                            if (!prefs.isHalfwayWarningSent()) {
                                notificationHelper.showHalfwayWarning(currentSpent, target)
                                prefs.setHalfwayWarningSent(true)
                            }
                        }
                    }
                }
            }
        }
    }
}