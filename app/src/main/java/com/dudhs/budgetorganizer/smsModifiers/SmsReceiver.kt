package com.dudhs.budgetorganizer.smsModifiers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.dudhs.budgetorganizer.PreferencesManager
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.helpers.normalizeAmount

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper(context).showTestNotification()
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val prefs = PreferencesManager(context)
            val notificationHelper = NotificationHelper(context)

            for (sms in messages) {
                val sender = sms.displayOriginatingAddress
                val bankNames = prefs.getBankNames()

                if (sender != null && bankNames.any { sender.contains(it, ignoreCase = true) }) {
                    val body = sms.displayMessageBody ?: ""
                    var expenseAmount = 0f

                    val amountRegex = Regex("([\\d.,]+)\\s*[EΕ]UR|[EΕ]UR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE)
                    val amountMatch = amountRegex.find(body)

                    if (amountMatch != null && !body.contains("ΠIΣTΩΣH", ignoreCase = true)) {
                        val rawAmount = amountMatch.groupValues[1].ifEmpty { amountMatch.groupValues[2] }
                        expenseAmount = normalizeAmount(rawAmount).toFloatOrNull() ?: 0f
                    }

                    if (body.contains("AMEΣH METAΦOPA", ignoreCase = true)) {
                        val transferMatch = Regex("€\\s*([\\d.,]+)").find(body)
                        if (transferMatch != null) {
                            expenseAmount = normalizeAmount(transferMatch.groupValues[1]).toFloatOrNull() ?: 0f
                        }
                    }

                    if (expenseAmount > 0f) {
                        prefs.addTrackedExpense(expenseAmount)
                        val currentSpent = prefs.getTrackedSpentThisMonth()
                        val target = prefs.getMonthlyTarget()

                        notificationHelper.showTestNotification()

                        if (target > 0) {
                            val percent = ((currentSpent / target) * 100).toInt()
                            val currentTier = (percent / 10) * 10
                            val lastNotifiedTier = prefs.getLastNotifiedTier()

                            if (currentTier >= 10 && currentTier > lastNotifiedTier) {
                                notificationHelper.showProgressWarning(currentSpent, target, currentTier)
                                prefs.setLastNotifiedTier(currentTier)
                            }
                        }
                    }
                }
            }
        }
    }
}