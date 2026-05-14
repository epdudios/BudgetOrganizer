package com.dudhs.budgetorganizer

import android.content.Context
import android.net.Uri
import com.dudhs.budgetorganizer.helpers.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsParser(private val context: Context) {

    var debugRawMessage: String = ""
        private set

    fun readAndParseAlphaBankSms(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        debugRawMessage = ""

        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("address", "body", "date")

        val cursor = context.contentResolver.query(uri, projection, null, null, "date DESC")

        // Used for messages that don't have a date in the body (like transfers)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        cursor?.use {
            val addressIndex = it.getColumnIndexOrThrow("address")
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val dateIndex = it.getColumnIndexOrThrow("date")

            while (it.moveToNext()) {
                val address = it.getString(addressIndex) ?: ""

                if (address.contains("ALPHA", ignoreCase = true)) {
                    val body = it.getString(bodyIndex) ?: ""
                    val timestampMillis = it.getLong(dateIndex)

                    // 1. Check for Type 2: Direct Transfer (Expense)
                    if (body.contains("AMEΣH METAΦOPA", ignoreCase = true)) {
                        val amountMatch = Regex("€\\s*([\\d.,]+)").find(body)
                        val merchantMatch = Regex("ΔIKAIOYXO (.*)").find(body)

                        if (amountMatch != null && merchantMatch != null) {
                            val amount = amountMatch.groupValues[1].replace(".", "").replace(",", ".")
                            val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')
                            // Transfer SMS doesn't have a date string, so we generate it from metadata
                            val date = dateFormat.format(Date(timestampMillis))
                            val time = timeFormat.format(Date(timestampMillis))

                            transactions.add(
                                Transaction(
                                    date,
                                    time,
                                    amount,
                                    merchant,
                                    timestampMillis
                                )
                            )
                            continue // skip the rest and move to next SMS
                        }
                    }

                    // 2. Check for Type 3: Credit / Refund (Sudden Income)
                    if (body.contains("ΠIΣTΩΣH", ignoreCase = true)) {
                        val amountMatch = Regex("EUR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE).find(body)
                        val dateTimeMatch = Regex("(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}:\\d{2})").find(body)
                        val merchantMatch = Regex("@\\s*(.*?)(?:ΔΙΑΘΕΣΙΜΟ|\$)", RegexOption.IGNORE_CASE).find(body)

                        if (amountMatch != null && dateTimeMatch != null && merchantMatch != null) {
                            val amountRaw = amountMatch.groupValues[1].replace(".", "").replace(",", ".")
                            // Prepend a minus sign! This subtracts from "spent", adding to the budget.
                            val amount = "-$amountRaw"
                            val date = dateTimeMatch.groupValues[1]
                            val time = dateTimeMatch.groupValues[2]
                            val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')

                            transactions.add(
                                Transaction(
                                    date,
                                    time,
                                    amount,
                                    merchant,
                                    timestampMillis
                                )
                            )
                            continue
                        }
                    }

                    // 3. Fallback to Type 1: Standard Expense (Original Logic)
                    val amountRegex = Regex("([\\d.,]+)\\s*[EΕ]UR|[EΕ]UR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE)
                    val dateTimeRegex = Regex("(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}:\\d{2})")
                    val merchantRegex = Regex("@\\s*(.*?)(?:ΔΙΑΘΕΣΙΜΟ|\$)", RegexOption.IGNORE_CASE)

                    val amountMatch = amountRegex.find(body)
                    val dateTimeMatch = dateTimeRegex.find(body)
                    val merchantMatch = merchantRegex.find(body)

                    if (amountMatch != null && dateTimeMatch != null && merchantMatch != null) {
                        val rawAmount = amountMatch.groupValues[1].ifEmpty { amountMatch.groupValues[2] }
                        val amount = rawAmount.replace(".", "").replace(",", ".")
                        val date = dateTimeMatch.groupValues[1]
                        val time = dateTimeMatch.groupValues[2]
                        val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')

                        transactions.add(Transaction(date, time, amount, merchant, timestampMillis))
                    } else {
                        if (debugRawMessage.isEmpty()) {
                            debugRawMessage = body
                        }
                    }
                }
            }
        }
        return transactions
    }
}