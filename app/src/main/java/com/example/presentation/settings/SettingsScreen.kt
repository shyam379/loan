package com.example.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.calculator.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalculatorViewModel,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentCurrency by viewModel.currencySymbol.collectAsState()
    val currentTheme by viewModel.appTheme.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings",
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
                .padding(20.dp)
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )

            // Preferences Card Group
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column {
                    // Currency Selection Action Row
                    SettingsRow(
                        title = "Currency Symbol",
                        subtitle = "Selected: $currentCurrency",
                        onClick = { showCurrencyDialog = true },
                        tag = "setting_row_currency"
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))

                    // Theme Selector Action Row
                    SettingsRow(
                        title = "App theme",
                        subtitle = "Selected: ${currentTheme.replaceFirstChar { it.uppercase() }}",
                        onClick = { showThemeDialog = true },
                        tag = "setting_row_theme"
                    )
                }
            }

            Text(
                text = "Application Support",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Support/About Card Group
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column {
                    // Reset Onboarding Row
                    SettingsRow(
                        title = "Reset Onboarding Screen",
                        subtitle = "Review the introductory onboarding slide deck again",
                        onClick = {
                            viewModel.resetOnboarding()
                            Toast.makeText(context, "Onboarding preferences successfully reset. Re-init app to view.", Toast.LENGTH_SHORT).show()
                        },
                        tag = "setting_row_reset_onboarding"
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))

                    // About Screen Navigation Row
                    SettingsRow(
                        title = "About Loan Calculator",
                        subtitle = "App versions, legal information, and bank disclaimer",
                        onClick = onNavigateToAbout,
                        tag = "setting_row_about"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer Version Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Loan EMI Calculator",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "v1.0.0 (Production Channel)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    // Currency Chooser Dialog
    if (showCurrencyDialog) {
        val currencies = listOf("₹", "$", "€", "£")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Choose Currency Symbol", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    currencies.forEach { symbol ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setCurrency(symbol)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currentCurrency == symbol),
                                onClick = {
                                    viewModel.setCurrency(symbol)
                                    showCurrencyDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = when (symbol) {
                                    "₹" -> "₹ - Indian Rupee (INR)"
                                    "$" -> "$ - United States Dollar (USD)"
                                    "€" -> "€ - Euro (EUR)"
                                    "£" -> "£ - British Pound (GBP)"
                                    else -> symbol
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Theme Selector Dialog
    if (showThemeDialog) {
        val themes = listOf("system", "light", "dark")
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Choose App Theme", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    themes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTheme(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currentTheme == mode),
                                onClick = {
                                    viewModel.setTheme(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = when (mode) {
                                    "system" -> "Follow System Default"
                                    "light" -> "Force Light Theme Mode"
                                    "dark" -> "Force Dark Theme Mode"
                                    else -> mode
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = "Arrow Right Symbol",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
