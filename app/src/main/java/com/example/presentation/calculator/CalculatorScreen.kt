package com.example.presentation.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.AppTextField
import androidx.compose.foundation.BorderStroke
import com.example.presentation.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToResult: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Observe text inputs
    val amount by viewModel.amountInput.collectAsState()
    val interest by viewModel.interestInput.collectAsState()
    val tenure by viewModel.tenureInput.collectAsState()
    val tenureType by viewModel.tenureTypeInput.collectAsState()
    val fee by viewModel.processingFeeInput.collectAsState()
    val prepayment by viewModel.prepaymentInput.collectAsState()
    val startDate by viewModel.startDateInput.collectAsState()

    // Observe error state flows
    val amountError by viewModel.amountError.collectAsState()
    val interestError by viewModel.interestError.collectAsState()
    val tenureError by viewModel.tenureError.collectAsState()

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
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculator Symbol",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Loan EMI Calculator",
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("loan_calculator_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header text
            Column {
                Text(
                    text = "Plan Your Repayment",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Quick, precise calculations for home, car, or personal loans.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Segment 1: Amount & Interest
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Loan Amount field
                    AppTextField(
                        value = amount,
                        onValueChange = { viewModel.amountInput.value = it },
                        label = "Loan Amount",
                        placeholder = "e.g., 500000",
                        errorText = amountError,
                        leadingIcon = {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    )

                    // Interest rate field
                    AppTextField(
                        value = interest,
                        onValueChange = { viewModel.interestInput.value = it },
                        label = "Annual Interest Rate (%)",
                        placeholder = "e.g., 7.9",
                        errorText = interestError,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Interest Rate Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }

            // Segment 2: Tenure & Options
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Loan Tenure Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppTextField(
                            value = tenure,
                            onValueChange = { viewModel.tenureInput.value = it },
                            label = "Loan Tenure",
                            placeholder = "e.g., 15",
                            errorText = tenureError,
                            modifier = Modifier.weight(1f)
                        )

                        // Tenure type selector (Months / Years)
                        Column {
                            Text(
                                text = "Tenure Type",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                                    .padding(4.dp)
                            ) {
                                val options = listOf("Years", "Months")
                                options.forEach { option ->
                                    val isSelected = tenureType == option
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent
                                            )
                                            .clickable { viewModel.tenureTypeInput.value = option }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Optional Segment: Fees, Prepayments, and Dates Accordion
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Optional Parameters",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Processing Fee field
                    AppTextField(
                        value = fee,
                        onValueChange = { viewModel.processingFeeInput.value = it },
                        label = "Processing Fee",
                        placeholder = "0",
                        leadingIcon = {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    )

                    // Prepayment field
                    AppTextField(
                        value = prepayment,
                        onValueChange = { viewModel.prepaymentInput.value = it },
                        label = "Prepayment Amount (Partial Pay)",
                        placeholder = "0",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = "Prepayment Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    // Start Date field
                    AppTextField(
                        value = startDate,
                        onValueChange = { viewModel.startDateInput.value = it },
                        label = "Start Date (DD/MM/YYYY)",
                        placeholder = "e.g., 28/05/2026",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = { viewModel.resetForm() },
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("button_reset")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Reset",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Calculate Button
                PrimaryButton(
                    text = "Calculate EMI",
                    onClick = {
                        val success = viewModel.calculateEmi()
                        if (success) {
                            onNavigateToResult()
                        }
                    },
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}
