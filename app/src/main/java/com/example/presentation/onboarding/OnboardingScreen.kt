package com.example.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.calculator.CalculatorViewModel
import com.example.presentation.components.PrimaryButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: CalculatorViewModel,
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val onboardingItems = listOf(
        OnboardingItem(
            title = "Advanced Calc Engine",
            description = "Compute amortized EMIs instantly using highly optimized formulas with real-time field validation.",
            icon = Icons.Default.Calculate,
            color = MaterialTheme.colorScheme.primaryContainer,
            tagText = "CALC ENGINE"
        ),
        OnboardingItem(
            title = "Visual Tech Analytics",
            description = "Visualize complex interest trajectories and dynamic principal portions with sleek interactive charts.",
            icon = Icons.Default.ShowChart,
            color = MaterialTheme.colorScheme.secondaryContainer,
            tagText = "ANALYTICS"
        ),
        OnboardingItem(
            title = "Secure Loan Sandbox",
            description = "Compare multiple financial scenarios side-by-side. Securely saved in your local sandboxed database.",
            icon = Icons.Default.CompareArrows,
            color = MaterialTheme.colorScheme.primaryContainer,
            tagText = "SECURE SANDBOX"
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (selected) 24.dp else 10.dp, 10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (pagerState.currentPage < 2) {
                        TextButton(
                            onClick = {
                                viewModel.completeOnboarding()
                                onFinishOnboarding()
                            },
                            modifier = Modifier.testTag("skip_button")
                        ) {
                            Text("Skip")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier.testTag("next_button")
                        ) {
                            Text("Next")
                        }
                    } else {
                        PrimaryButton(
                            text = "Get Started",
                            onClick = {
                                viewModel.completeOnboarding()
                                onFinishOnboarding()
                            },
                            modifier = Modifier.testTag("get_started_button")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            val item = onboardingItems[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Premium 7D Layered Multi-dimensional Isometric Deck Container
                Layered7DCardDeck(
                    icon = item.icon,
                    accentColor = item.color,
                    tagText = item.tagText
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Onboarding Metadata Text
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun Layered7DCardDeck(
    icon: ImageVector,
    accentColor: Color,
    tagText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Core deep ambient glowing aura behind the stack
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // 2. Extra Deep Isometric Offset Layer (Aesthetic physical projection)
        Box(
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer {
                    rotationZ = -14f
                    translationX = -24.dp.toPx()
                    translationY = 16.dp.toPx()
                }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(32.dp)
                )
        )

        // 3. Middle Structural Outline Frame (Dynamic translucent glass)
        Box(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    rotationZ = 10f
                    translationX = 18.dp.toPx()
                    translationY = -12.dp.toPx()
                }
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(36.dp)
                )
                .border(
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(36.dp)
                )
        )

        // 4. Front Crystal Core Card (7D holographic-like glass controller)
        Box(
            modifier = Modifier
                .size(165.dp)
                .graphicsLayer {
                    cameraDistance = 16f * density
                    rotationX = 12f
                    rotationY = -14f
                    rotationZ = -3f
                }
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .border(
                    BorderStroke(
                        width = 1.8.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Internal radial focus container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        // 5. High-Impact Floating Glassy Action Badge (7D active label)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = (-2).dp)
                .graphicsLayer {
                    rotationZ = 6f
                }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Active Indicator",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = tagText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

data class OnboardingItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val tagText: String
)
