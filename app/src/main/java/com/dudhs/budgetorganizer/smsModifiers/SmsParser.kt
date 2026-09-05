package com.dudhs.budgetorganizer.smsModifiers

import android.content.Context
import android.net.Uri
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.helpers.normalizeAmount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsParser(private val context: Context) {

    var debugRawMessage: String = ""
        private set

    fun readAndParseAlphaBankSms(): List<TransactionDataClass> {
        val transactions = mutableListOf<TransactionDataClass>()
        debugRawMessage = ""

        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("address", "body", "date")

        val cursor = context.contentResolver.query(uri, projection, null, null, "date DESC")

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


                    if (body.contains("AMEΣH METAΦOPA", ignoreCase = true)) {
                        val amountMatch = Regex("€\\s*([\\d.,]+)").find(body)
                        val merchantMatch = Regex("ΔIKAIOYXO (.*)").find(body)

                        if (amountMatch != null && merchantMatch != null) {
                            val amount = normalizeAmount(amountMatch.groupValues[1])
                            val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')
                            val date = dateFormat.format(Date(timestampMillis))
                            val time = timeFormat.format(Date(timestampMillis))

                            transactions.add(
                                TransactionDataClass(
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

                    if (body.contains("ΠIΣTΩΣH", ignoreCase = true)) {
                        val amountMatch = Regex("EUR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE).find(body)
                        val dateTimeMatch = Regex("(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}:\\d{2})").find(body)
                        val merchantMatch = Regex("@\\s*(.*?)(?:ΔΙΑΘΕΣΙΜΟ|\$)", RegexOption.IGNORE_CASE).find(body)

                        if (amountMatch != null && dateTimeMatch != null && merchantMatch != null) {
                            val amountRaw = normalizeAmount(amountMatch.groupValues[1])

                            val amount = "-$amountRaw"
                            val date = dateTimeMatch.groupValues[1]
                            val time = dateTimeMatch.groupValues[2]
                            val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')

                            transactions.add(
                                TransactionDataClass(
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

                    val amountRegex = Regex("([\\d.,]+)\\s*[EΕ]UR|[EΕ]UR\\s*([\\d.,]+)", RegexOption.IGNORE_CASE)
                    val dateTimeRegex = Regex("(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}:\\d{2})")
                    val merchantRegex = Regex("@\\s*(.*?)(?:ΔΙΑΘΕΣΙΜΟ|\$)", RegexOption.IGNORE_CASE)

                    val amountMatch = amountRegex.find(body)
                    val dateTimeMatch = dateTimeRegex.find(body)
                    val merchantMatch = merchantRegex.find(body)

                    if (amountMatch != null && dateTimeMatch != null && merchantMatch != null) {
                        val rawAmount = amountMatch.groupValues[1].ifEmpty { amountMatch.groupValues[2] }
                        val amount = normalizeAmount(rawAmount)
                        val date = dateTimeMatch.groupValues[1]
                        val time = dateTimeMatch.groupValues[2]
                        val merchant = merchantMatch.groupValues[1].trimEnd('.', ' ', '\n', '\r')

                        transactions.add(
                            TransactionDataClass(
                                date,
                                time,
                                amount,
                                merchant,
                                timestampMillis
                            )
                        )
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