package com.dudhs.budgetorganizer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.screens.BudgetAppScreen
import com.dudhs.budgetorganizer.smsModifiers.SmsParser
import com.dudhs.budgetorganizer.ui.theme.BudgetOrganizerTheme

class MainActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 101

    private lateinit var smsParser: SmsParser
    //private lateinit var csvExporter: CsvExporter
    private lateinit var prefsManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper

    private var transactionsList: List<TransactionDataClass> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        smsParser = SmsParser(this)
       // csvExporter = CsvExporter(this)
        prefsManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)

        if (checkSmsPermission()) {
            transactionsList = smsParser.readAndParseAlphaBankSms()
            renderComposeUI()
        } else {
            requestSmsPermission()
        }
    }

    private fun renderComposeUI() {
        setContent {
            BudgetOrganizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BudgetAppScreen(
                        transactions = transactionsList,
                        preferencesManager = prefsManager,
                        notificationHelper = notificationHelper ,
                        onTransactionClick = {TODO()}
                        // onExportClick = { processAndExportData() }
                    )
                }
            }
        }
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), SMS_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            transactionsList = smsParser.readAndParseAlphaBankSms()
            renderComposeUI()
        } else {
            Toast.makeText(this, "SMS Permission is required to show the budget.", Toast.LENGTH_LONG).show()
        }
    }

//    private fun processAndExportData() {
//        if (transactionsList.isEmpty()) {
//            Toast.makeText(this, "No transactions found to export.", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val myFile = csvExporter.createBudgetSpreadsheet(transactionsList)
//
//        try {
//            val intent = csvExporter.createExcelIntent(myFile)
//            startActivity(intent)
//            Toast.makeText(this, "Exported ${transactionsList.size} transactions!", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Could not open Excel.", Toast.LENGTH_SHORT).show()
//        }
//    }
}
