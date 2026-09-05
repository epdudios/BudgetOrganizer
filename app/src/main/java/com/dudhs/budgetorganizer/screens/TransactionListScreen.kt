package com.dudhs.budgetorganizer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dudhs.budgetorganizer.budgetComponents.TransactionCardItem
import com.dudhs.budgetorganizer.dataClasses.DateFilter
import com.dudhs.budgetorganizer.dataClasses.TransactionDataClass
import com.dudhs.budgetorganizer.dataClasses.TransactionFilters
import com.dudhs.budgetorganizer.dataClasses.TypeFilter
import com.dudhs.budgetorganizer.dataClasses.applyFilters
import com.dudhs.budgetorganizer.helpers.filterTransactionsByName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactions: List<TransactionDataClass>,
    onBack: () -> Unit,
    onTransactionClick: (TransactionDataClass) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(TransactionFilters()) }
    val focusRequester = remember { FocusRequester() }

    val displayTransactions = remember(searchQuery, transactions, filters) {
        val byName = filterTransactionsByName(transactions, searchQuery)
        applyFilters(byName, filters).sortedByDescending { it.timestamp }
    }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) focusRequester.requestFocus()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                        )
                    } else {
                        Text("Transactions")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearchActive) {
                            isSearchActive = false
                            searchQuery = ""
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    } else {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )

            if (displayTransactions.isEmpty()) {
                EmptyState(message = "No transactions found.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(displayTransactions) { transaction ->
                        TransactionCardItem(transaction = transaction, onClick = { onTransactionClick(transaction) })
                    }
                }
            }
        }

    if (showFilterSheet) {
        FilterBottomSheet(
            current = filters,
            onApply = { filters = it; showFilterSheet = false },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    current: TransactionFilters,
    onApply: (TransactionFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var dateFilter by remember { mutableStateOf(current.dateFilter) }
    var typeFilter by remember { mutableStateOf(current.typeFilter) }
    var minText by remember { mutableStateOf(current.minAmount?.toString() ?: "") }
    var maxText by remember { mutableStateOf(current.maxAmount?.toString() ?: "") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filter transactions", style = MaterialTheme.typography.titleLarge)

            Text("Date range", style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DateFilter.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dateFilter = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = dateFilter == option, onClick = { dateFilter = option })
                        Text(option.label)
                    }
                }
            }

            Text("Type", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TypeFilter.values().forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = typeFilter == option,
                        onClick = { typeFilter = option },
                        shape = SegmentedButtonDefaults.itemShape(index, TypeFilter.values().size)
                    ) { Text(option.label) }
                }
            }

            Text("Amount (€)", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minText,
                    onValueChange = { minText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Min") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = maxText,
                    onValueChange = { maxText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Max") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onApply(TransactionFilters()) }, modifier = Modifier.weight(1f)) {
                    Text("Clear")
                }
                Button(
                    onClick = {
                        onApply(
                            TransactionFilters(
                                dateFilter = dateFilter,
                                typeFilter = typeFilter,
                                minAmount = minText.toFloatOrNull(),
                                maxAmount = maxText.toFloatOrNull()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Apply") }
            }
        }
    }
}