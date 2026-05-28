package com.example.presentation.comparison

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.calculator.CalculatorViewModel
import com.example.presentation.components.AppTextField
import com.example.presentation.components.PrimaryButton
import com.example.presentation.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    viewModel: CalculatorViewModel
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Loan A state
    val amountA by viewModel.compareAmountA.collectAsState()
    val interestA by viewModel.compareInterestA.collectAsState()
    val tenureA by viewModel.compareTenureA.collectAsState()
    val tenureTypeA by viewModel.compareTenureTypeA.collectAsState()

    // Loan B state
    val amountB by viewModel.compareAmountB.collectAsState()
    val interestB by viewModel.compareInterestB.collectAsState()
    val tenureB by viewModel.compareTenureB.collectAsState()
    val tenureTypeB by viewModel.compareTenureTypeB.collectAsState()

    // Output Result State
    val compareResultState by viewModel.compareResult.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "Compare Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compare Loans",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .clickable { focusManager.clearFocus() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("comparison_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Loan Comparison Tool",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Loan A Box
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "LOAN OPTIONS A",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    AppTextField(
                        value = amountA,
                        onValueChange = {
                            viewModel.compareAmountA.value = it
                            viewModel.calculateComparison()
                        },
                        label = "Loan Amount",
                        placeholder = "Amount A"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            value = interestA,
                            onValueChange = {
                                viewModel.compareInterestA.value = it
                                viewModel.calculateComparison()
                            },
                            label = "Interest Rate (%)",
                            placeholder = "e.g., 8.0",
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = tenureA,
                            onValueChange = {
                                viewModel.compareTenureA.value = it
                                viewModel.calculateComparison()
                            },
                            label = "Tenure",
                            placeholder = "Tenure",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Loan B Box
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "LOAN OPTIONS B",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    AppTextField(
                        value = amountB,
                        onValueChange = {
                            viewModel.compareAmountB.value = it
                            viewModel.calculateComparison()
                        },
                        label = "Loan Amount",
                        placeholder = "Amount B"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            value = interestB,
                            onValueChange = {
                                viewModel.compareInterestB.value = it
                                viewModel.calculateComparison()
                            },
                            label = "Interest Rate (%)",
                            placeholder = "e.g., 8.5",
                            modifier = Modifier.weight(1f)
                        )
                        AppTextField(
                            value = tenureB,
                            onValueChange = {
                                viewModel.compareTenureB.value = it
                                viewModel.calculateComparison()
                            },
                            label = "Tenure",
                            placeholder = "Tenure",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Side by side Comparison Metrics
            when (val res = compareResultState) {
                is CalculatorViewModel.CompareResultState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Please enter valid quantities for both Loan options to show the comparative study.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                is CalculatorViewModel.CompareResultState.Calculated -> {
                    // Recommendation Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Insight",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = res.comparisonText,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // Side-by-side Table Metrics
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Comparative Analytics",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Main Comparison Rows
                            RowComparisonItem("Metric", "Loan A", "Loan B", isHeader = true)
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            
                            RowComparisonItem(
                                metric = "Monthly EMI",
                                valA = formatCurrency(res.emiA, currencySymbol),
                                valB = formatCurrency(res.emiB, currencySymbol),
                                isHighlight = true
                            )
                            RowComparisonItem(
                                metric = "Total Interest",
                                valA = formatCurrency(res.interestA, currencySymbol),
                                valB = formatCurrency(res.interestB, currencySymbol)
                            )
                            RowComparisonItem(
                                metric = "Total Outflow",
                                valA = formatCurrency(res.totalPayableA, currencySymbol),
                                valB = formatCurrency(res.totalPayableB, currencySymbol),
                                isHighlight = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowComparisonItem(
    metric: String,
    valA: String,
    valB: String,
    isHeader: Boolean = false,
    isHighlight: Boolean = false
) {
    val style = if (isHeader) {
        MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
    } else if (isHighlight) {
        MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
    } else {
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = metric,
            style = style,
            color = if (isHeader) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valA,
            style = style,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = valB,
            style = style,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
    }
}
