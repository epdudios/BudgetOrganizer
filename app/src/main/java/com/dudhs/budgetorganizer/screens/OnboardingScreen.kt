package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class OnboardingResult(
    val monthlyIncome: Float,
    val monthlyTarget: Float,
    val bankName: String
)

@Composable
fun OnboardingScreen(onFinished: (OnboardingResult) -> Unit) {
    var incomeText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var bankText by remember { mutableStateOf("") }

    val income = incomeText.replace(',', '.').toFloatOrNull()
    val target = targetText.replace(',', '.').toFloatOrNull()
    val isValid = income != null && income > 0f &&
            target != null && target >= 0f &&
            bankText.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Let's set up your budget. You can change all of this later in Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = incomeText,
            onValueChange = { v -> incomeText = v.filter { it.isDigit() || it == '.' || it == ',' } },
            label = { Text("Monthly income (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = targetText,
            onValueChange = { v -> targetText = v.filter { it.isDigit() || it == '.' || it == ',' } },
            label = { Text("Monthly spending target (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bankText,
            onValueChange = { bankText = it },
            label = { Text("Bank SMS sender name (e.g. ALPHA)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onFinished(OnboardingResult(income!!, target!!, bankText.trim()))
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Get Started")
        }
    }
}