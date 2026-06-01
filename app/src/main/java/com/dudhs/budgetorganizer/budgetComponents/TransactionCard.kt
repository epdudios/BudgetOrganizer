package com.dudhs.budgetorganizer.budgetComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.helpers.formatExpense
import com.dudhs.budgetorganizer.helpers.formatIncome

@Composable
fun TransactionCardItem (transaction: TransactionDataClass, onClick: () -> Unit){
    val amountValue = transaction.amount.toDoubleOrNull() ?: 0.0
    val isIncome = amountValue < 0.0
    val displayAmount = if (isIncome) formatIncome(amountValue) else formatExpense(amountValue)
    val amountColor = if (isIncome) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = amountColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${transaction.date} at ${transaction.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = displayAmount,
                modifier = Modifier.widthIn(min = 92.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }
    }
}