package com.dudhs.budgetorganizer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.dudhs.budgetorganizer.helpers.Transaction
import java.io.File

class CsvExporter(private val context: Context) {

    fun createBudgetSpreadsheet(transactions: List<Transaction>): File {
        val fileName = "MyBudget.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        val csvData = StringBuilder()

        csvData.append("Monthly budget, 1500\n\n")
        csvData.append("Expenses, Amount (€), Date, Time\n")

        for (t in transactions) {
            val safeMerchant = t.merchant.replace(",", " ").replace("\n", " ").trim()
            csvData.append("${safeMerchant}, ${t.amount}, ${t.date}, ${t.time}\n")
        }

        file.writeText(csvData.toString())
        return file
    }

    fun createExcelIntent(file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}