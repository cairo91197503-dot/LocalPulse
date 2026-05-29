package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoogleGreen
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: BusinessViewModel,
    onShowPrivacyPolicy: () -> Unit
) {
    val step by viewModel.onboardingStep.collectAsState()
    val businessNameInput by viewModel.businessNameInput.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: App Widescreen Logo
            Image(
                painter = painterResource(id = com.example.R.drawable.img_app_logo_1780065100739),
                contentDescription = "Logo LocalPulse",
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            // Slide content transition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    label = "OnboardingSlide"
                ) { currentStep ->
                    OnboardingSlideContent(
                        step = currentStep,
                        businessNameInput = businessNameInput,
                        onNameChange = { viewModel.updateBusinessNameInput(it) },
                        onShowPrivacyPolicy = onShowPrivacyPolicy
                    )
                }
            }

            // Bottom Navigation & Actions Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) { index ->
                        val isSelected = index == step
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                if (step < 2) {
                    // Standard Button
                    Button(
                        onClick = { viewModel.nextOnboardingStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Avançar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "Avançar"
                        )
                    }
                } else {
                    // Step 3: Multi-Platform / Social login options
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title/Callout helper
                        Text(
                            text = "Por favor, conecte para continuar:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )

                        // 1. Google connection
                        Button(
                            onClick = { viewModel.loginWithGoogle() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("google_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Selo Google",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Entrar com Google",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        // 2. Facebook connection
                        Button(
                            onClick = { viewModel.loginWithFacebook() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("facebook_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1877F2),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Selo Facebook",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Entrar com Facebook",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        // 3. Instagram connection
                        Button(
                            onClick = { viewModel.loginWithInstagram() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("instagram_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE1306C),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = "Selo Instagram",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Entrar com Instagram",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Back or Privacy button
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (step > 0) {
                        TextButton(
                            onClick = { viewModel.previousOnboardingStep() },
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Text("Voltar", color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Text(
                        text = "Politica de Privacidade",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .clickable { onShowPrivacyPolicy() }
                            .padding(8.dp)
                            .testTag("privacy_policy_onboarding_btn")
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingSlideContent(
    step: Int,
    businessNameInput: String,
    onNameChange: (String) -> Unit,
    onShowPrivacyPolicy: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        when (step) {
            0 -> {
                // Slide 1: Welcome
                LocalStoreShowcaseIllustration()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Bem-vindo ao LocalPulse",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Gerencie a reputação online do seu pequeno comércio. Crie um relacionamento incrível com seus clientes locais com facilidade.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )
            }
            1 -> {
                // Slide 2: Analytics & Sentiment
                WeeklyReportsIllustration()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Entenda seus clientes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lemos de forma integrada as avaliações vindas do Google Meu Negócio e geramos resumos inteligentes semanais de sentimentos automaticamente.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )
            }
            2 -> {
                // Slide 3: Smart Replying and name configuration
                SmartReplyingIllustration()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Responda em 1 toque",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gere respostas otimizadas por IA contextualizadas para cada feedback em segundos e publique diretamente no perfil da sua loja.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Business connecting helper input
                OutlinedTextField(
                    value = businessNameInput,
                    onValueChange = onNameChange,
                    label = { Text("Nome do seu negócio no Google") },
                    singleLine = true,
                    placeholder = { Text("Ex: Sabor da Vila Panificadora") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("business_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

@Composable
fun LocalStoreShowcaseIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            // Draw a simplified beautiful storefront setup representing a local business
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Shop body
            drawRect(
                color = primaryColor.copy(alpha = 0.2f),
                topLeft = Offset(20.dp.toPx(), 45.dp.toPx()),
                size = Size(80.dp.toPx(), 55.dp.toPx())
            )

            // Door
            drawRect(
                color = primaryColor,
                topLeft = Offset(50.dp.toPx(), 65.dp.toPx()),
                size = Size(20.dp.toPx(), 35.dp.toPx())
            )

            // Window
            drawRect(
                color = accentColor.copy(alpha = 0.4f),
                topLeft = Offset(28.dp.toPx(), 55.dp.toPx()),
                size = Size(15.dp.toPx(), 20.dp.toPx())
            )

            // Roof (Awnings)
            drawRect(
                color = primaryColor,
                topLeft = Offset(12.dp.toPx(), 32.dp.toPx()),
                size = Size(96.dp.toPx(), 15.dp.toPx())
            )

            // Ground base
            drawLine(
                color = primaryColor,
                start = Offset(5.dp.toPx(), 100.dp.toPx()),
                end = Offset(115.dp.toPx(), 100.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )

            // Stars around GMB style
            val starColor = Color(0xFFFFB300)
            drawStar(this, Offset(35.dp.toPx(), 15.dp.toPx()), 8.dp.toPx(), starColor)
            drawStar(this, Offset(60.dp.toPx(), 10.dp.toPx()), 10.dp.toPx(), starColor)
            drawStar(this, Offset(85.dp.toPx(), 15.dp.toPx()), 8.dp.toPx(), starColor)
        }
    }
}

@Composable
fun WeeklyReportsIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Draw a stylish metrics card
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(15.dp.toPx(), 15.dp.toPx()),
                size = Size(90.dp.toPx(), 90.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw progress lines / text representations
            drawLine(
                color = primaryColor,
                start = Offset(25.dp.toPx(), 35.dp.toPx()),
                end = Offset(80.dp.toPx(), 35.dp.toPx()),
                strokeWidth = 5.dp.toPx()
            )

            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(25.dp.toPx(), 48.dp.toPx()),
                end = Offset(65.dp.toPx(), 48.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )

            // Success trend arrow
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(25.dp.toPx(), 85.dp.toPx())
                    lineTo(50.dp.toPx(), 65.dp.toPx())
                    lineTo(65.dp.toPx(), 75.dp.toPx())
                    lineTo(90.dp.toPx(), 55.dp.toPx())
                },
                color = accentColor,
                style = Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Circle endpoint indicator
            drawCircle(
                color = accentColor,
                center = Offset(90.dp.toPx(), 55.dp.toPx()),
                radius = 5.dp.toPx()
            )
        }
    }
}

@Composable
fun SmartReplyingIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Draw a phone layout or chat bubles
            // Bubble 1 Left (Incoming rating)
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.15f),
                topLeft = Offset(10.dp.toPx(), 20.dp.toPx()),
                size = Size(80.dp.toPx(), 35.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )
            // Stars inside bubble 1
            val starColor = Color(0xFFFFB300)
            drawStar(this, Offset(20.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)
            drawStar(this, Offset(32.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)
            drawStar(this, Offset(44.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)

            // Bubble 2 Right (AI suggested response)
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(30.dp.toPx(), 65.dp.toPx()),
                size = Size(80.dp.toPx(), 35.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )

            // AI Sparks icon
            drawCircle(
                color = Color.White,
                center = Offset(45.dp.toPx(), 82.dp.toPx()),
                radius = 4.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(55.dp.toPx(), 82.dp.toPx()),
                end = Offset(95.dp.toPx(), 82.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

// Simple Helper to draw a math-oriented Star shape
fun drawStar(
    scope: androidx.compose.ui.graphics.drawscope.DrawScope,
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = androidx.compose.ui.graphics.Path()
    val numberOfPoints = 5
    val halfPi = Math.PI / 2.0
    val doublePi = Math.PI * 2.0
    val innerRadius = radius * 0.47f // ratio for nice stars

    for (i in 0 until numberOfPoints) {
        val outerAngle = halfPi + i * doublePi / numberOfPoints
        val innerAngle = halfPi + (i + 0.5) * doublePi / numberOfPoints

        val outerX = center.x + radius * Math.cos(outerAngle).toFloat()
        val outerY = center.y - radius * Math.sin(outerAngle).toFloat()
        
        val innerX = center.x + innerRadius * Math.cos(innerAngle).toFloat()
        val innerY = center.y - innerRadius * Math.sin(innerAngle).toFloat()

        if (i == 0) {
            path.moveTo(outerX, outerY)
        } else {
            path.lineTo(outerX, outerY)
        }
        path.lineTo(innerX, innerY)
    }
    path.close()
    scope.drawPath(path = path, color = color)
}
