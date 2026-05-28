package com.example.presentation.result

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.calculator.CalculatorViewModel
import com.example.presentation.components.AppTopBar
import com.example.presentation.components.BreakdownChart
import com.example.presentation.components.ResultCard
import com.example.presentation.components.SummaryCard
import com.example.presentation.components.formatCurrency
import com.example.presentation.components.AiAssistantCard
import com.example.presentation.components.AiChatBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAmortization: () -> Unit,
    onNavigateToCompare: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    val emiResultState by viewModel.emiResult.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    // Dialog State
    var showSaveDialog by remember { mutableStateOf(false) }
    var calculationTitle by remember { mutableStateOf("") }
    var showChatSheet by remember { mutableStateOf(false) }

    val emiResult = emiResultState

    Scaffold(
        topBar = {
            AppTopBar(
                title = "EMI Calculation",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            if (emiResult != null) {
                                calculationTitle = "Loan EMI (${formatCurrency(emiResult.principalAmount, currencySymbol)})"
                                showSaveDialog = true
                            }
                        },
                        modifier = Modifier.testTag("action_save_calc")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Save Calculation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (emiResult == null) {
            // Safe fallback state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active Calculation found. Please calculate from the form.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Key result visual
                ResultCard(
                    monthlyEmi = emiResult.monthlyEmi,
                    totalInterest = emiResult.totalInterest,
                    totalPayment = emiResult.totalPayment,
                    currencySymbol = currencySymbol
                )

                // Extra details
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Repayment Overview",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        RowValue(
                            label = "Principal Amount",
                            value = formatCurrency(emiResult.principalAmount, currencySymbol)
                        )
                        RowValue(
                            label = "Interest Payable",
                            value = formatCurrency(emiResult.totalInterest, currencySymbol),
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                        RowValue(
                            label = "Processing Fee",
                            value = formatCurrency(emiResult.processingFee, currencySymbol)
                        )
                        RowValue(
                            label = "Prepayment Amount",
                            value = formatCurrency(emiResult.prepaymentAmount, currencySymbol)
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        RowValue(
                            label = "Total Loan Cost",
                            value = formatCurrency(emiResult.totalPayment, currencySymbol),
                            fontWeight = FontWeight.ExtraBold,
                            valueColor = MaterialTheme.colorScheme.primary
                        )
                        RowValue(
                            label = "Estimated End Date",
                            value = emiResult.formattedEndDate,
                            icon = Icons.Default.CalendarMonth
                        )
                    }
                }

                // Visual Chart
                BreakdownChart(
                    principalPercentage = emiResult.principalPercentage,
                    interestPercentage = emiResult.interestPercentage,
                    principalAmount = emiResult.principalAmount,
                    interestAmount = emiResult.totalInterest,
                    currencySymbol = currencySymbol
                )

                // Summary Text Indicator
                SummaryCard(
                    extraInterest = emiResult.totalInterest,
                    months = emiResult.tenureMonths,
                    currencySymbol = currencySymbol
                )

                // AI Intelligent Insights Card
                val aiState by viewModel.aiAnalysisState.collectAsState()
                AiAssistantCard(
                    aiState = aiState,
                    onAnalyzeClick = { viewModel.analyzeLoanWithAi() },
                    onConsultClick = { showChatSheet = true }
                )

                // Actions Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. View Schedule button
                    Button(
                        onClick = onNavigateToAmortization,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("button_view_schedule")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = "Schedule",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Amortization Schedule",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // 2. Compare option button
                    OutlinedButton(
                        onClick = onNavigateToCompare,
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("button_goto_compare")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "Compare",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compare with Alternative Loan",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    // Save Loan Details Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Save EMI Calculation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Specify a custom tag or description to remember this loan calculation:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = calculationTitle,
                        onValueChange = { calculationTitle = it },
                        label = { Text("Calculation Label") },
                        placeholder = { Text("e.g., Appt 4B Mortgage") },
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
                            .testTag("dialog_save_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentCalculation(calculationTitle)
                        showSaveDialog = false
                        Toast.makeText(context, "Calculation Saved Successfully", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.testTag("dialog_save_confirm")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false },
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChatSheet) {
        AiChatBottomSheet(
            viewModel = viewModel,
            onDismissRequest = { showChatSheet = false }
        )
    }
}

// Subordinate composable for details representation row
@Composable
fun RowValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Medium,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = fontWeight,
                color = valueColor
            )
        )
    }
}
