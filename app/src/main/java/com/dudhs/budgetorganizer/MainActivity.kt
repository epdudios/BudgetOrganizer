package com.dudhs.budgetorganizer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dudhs.budgetorganizer.helpers.NotificationHelper
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.screens.BudgetAppScreen
import com.dudhs.budgetorganizer.smsModifiers.SmsParser
import com.dudhs.budgetorganizer.ui.theme.AppTheme
import com.dudhs.budgetorganizer.ui.theme.BudgetOrganizerTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 101
    private val NOTIFICATION_PERMISSION_CODE = 102


    private lateinit var smsParser: SmsParser
    private lateinit var prefsManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper

    private var transactionsList: List<TransactionDataClass> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        smsParser = SmsParser(this)
        prefsManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)

        if (checkSmsPermission()) {
            transactionsList = smsParser.readAndParseAlphaBankSms()
            renderComposeUI()
            requestNotificationPermission()
        } else {
            requestSmsPermission()
        }
    }

    private fun renderComposeUI() {
        setContent {
            var appTheme by remember { mutableStateOf(AppTheme.valueOf(prefsManager.getAppTheme())) }

            BudgetOrganizerTheme(appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BudgetAppScreen(
                        transactions = transactionsList,
                        preferencesManager = prefsManager,
                        notificationHelper = notificationHelper,
                        onTransactionClick = {},
                        onThemeChange = { newTheme -> appTheme = newTheme
                            prefsManager.setAppTheme(newTheme.name)
                        }
                    )
                }
            }
        }
    }

    private fun checkSmsPermission(): Boolean {
        val readSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        val receiveSms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        return readSms && receiveSms
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
            SMS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                transactionsList = smsParser.readAndParseAlphaBankSms()
                renderComposeUI()
            } else {Toast.makeText(this, "SMS Permission is required to show the budget.", Toast.LENGTH_LONG).show() }
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }
}
