package com.example.presentation.amortization

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.presentation.calculator.CalculatorViewModel
import com.example.presentation.components.AppTopBar
import com.example.presentation.components.formatCurrency

@Composable
fun AmortizationScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val schedule by viewModel.amortizationSchedule.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    // Screen States
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Monthly, 1 = Yearly Summary

    // Filter schedule based on search query
    val filteredSchedule = remember(schedule, searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            schedule
        } else {
            val queryInt = searchQuery.trim().toIntOrNull()
            if (queryInt != null) {
                schedule.filter { it.monthNumber == queryInt }
            } else {
                schedule
            }
        }
    }

    // Chunk schedule into yearly summaries
    val yearlySummaries = remember(schedule) {
        schedule.chunked(12).mapIndexed { index, chunk ->
            val yearNumber = index + 1
            val totalPrincipal = chunk.sumOf { it.principalPaid }
            val totalInterest = chunk.sumOf { it.interestPaid }
            val totalPayments = chunk.sumOf { it.emi }
            val endingBalance = chunk.last().remainingBalance
            YearlySummary(
                yearNumber = yearNumber,
                principalPaid = totalPrincipal,
                interestPaid = totalInterest,
                totalPaid = totalPayments,
                remainingBalance = endingBalance
            )
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Payment Schedule",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Exporting Schedule as CSV placeholder (Feature Coming Soon!)", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.testTag("action_export_schedule")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export Amortization Data",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("amortization_screen")
        ) {
            // View Selection Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Monthly Table", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Yearly Summary", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Monthly detailed view
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by Month Number") },
                    placeholder = { Text("e.g., 5") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("search_month_field")
                )

                // Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeaderCell("Mo.", weight = 1.0f)
                    TableHeaderCell("EMI Paid", weight = 1.8f)
                    TableHeaderCell("Principal", weight = 1.8f)
                    TableHeaderCell("Interest", weight = 1.8f)
                    TableHeaderCell("Balance", weight = 2.0f)
                }

                if (filteredSchedule.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching installment month found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredSchedule) { item ->
                            val isEven = item.monthNumber % 2 == 0
                            val rowBg = if (isEven) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.background
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(rowBg)
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableValueCell(item.monthNumber.toString(), weight = 1.0f, isBold = true)
                                TableValueCell(formatCurrency(item.emi, currencySymbol), weight = 1.8f)
                                TableValueCell(formatCurrency(item.principalPaid, currencySymbol), weight = 1.8f)
                                TableValueCell(formatCurrency(item.interestPaid, currencySymbol), weight = 1.8f, textColor = MaterialTheme.colorScheme.secondary)
                                TableValueCell(formatCurrency(item.remainingBalance, currencySymbol), weight = 2.0f)
                            }
                        }
                    }
                }
            } else {
                // Yearly Summary View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(yearlySummaries) { summary ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "YEAR ${summary.yearNumber}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(8.dp))

                                RowSummaryLine("Total Principal Paid", formatCurrency(summary.principalPaid, currencySymbol))
                                RowSummaryLine("Total Interest Paid", formatCurrency(summary.interestPaid, currencySymbol), MaterialTheme.colorScheme.secondary)
                                RowSummaryLine("Total Payments Paid", formatCurrency(summary.totalPaid, currencySymbol))
                                RowSummaryLine("Remaining Balance", formatCurrency(summary.remainingBalance, currencySymbol), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.weight(weight),
        textAlign = TextAlign.Center
    )
}

@Composable
fun RowScope.TableValueCell(
    text: String,
    weight: Float,
    isBold: Boolean = false,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        ),
        modifier = Modifier.weight(weight),
        textAlign = TextAlign.Center
    )
}

@Composable
fun RowSummaryLine(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = fontWeight, color = valueColor))
    }
}

data class YearlySummary(
    val yearNumber: Int,
    val principalPaid: Double,
    val interestPaid: Double,
    val totalPaid: Double,
    val remainingBalance: Double
)
