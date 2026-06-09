package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onLoginSuccess: (isProSelected: Boolean) -> Unit
) {
    var isProSelected by remember { mutableStateOf(true) }
    var step by remember { mutableIntStateOf(1) } // 1: Welcome/Value Proposition, 2: Choose Plan

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF0F1528))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo Icon & Title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(listOf(PrimaryPurple, PrimaryCyan)),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = "Pulse Logo",
                    tint = Color.white,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LocalPulse",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Impulsione o seu GMN com Inteligência Artificial",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryCyan,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "onboarding_steps_anim"
            ) { currentStep ->
                if (currentStep == 1) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aumente as vendas, otimize seu perfil local do Google em segundos e responda clientes em tempo real com apoio do Gemini.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { step = 2 },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("continue_to_plans"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Ver Planos e Começar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Próximo")
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Selecione o plano ideal para seu negócio:",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )

                        // Plan Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Free Plan Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (!isProSelected) 2.dp else 1.dp,
                                        color = if (!isProSelected) PrimaryCyan else CardBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(CardBg, RoundedCornerShape(16.dp))
                                    .clickable { isProSelected = false }
                                    .padding(16.dp)
                                    .testTag("free_plan_selector")
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "Gratuito",
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isProSelected) PrimaryCyan else TextSecondary
                                    )
                                    Text("R$ 0", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Uso básico offline, diagnóstico estático.", fontSize = 12.sp, color = TextMuted)
                                    if (!isProSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = PrimaryCyan,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }

                            // Pro Plan Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isProSelected) 2.dp else 1.dp,
                                        color = if (isProSelected) PrimaryPurple else CardBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        if (isProSelected) Color(0xFF1E1F3D) else CardBg,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { isProSelected = true }
                                    .padding(16.dp)
                                    .testTag("pro_plan_selector")
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "Plano Pro",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isProSelected) PrimaryPurple else TextSecondary
                                    )
                                    Text("R$ 39/mês", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Acesso integral às IAs do Gemini, posts infinitos.", fontSize = 11.sp, color = TextMuted)
                                    if (isProSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = PrimaryPurple,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Google Sign-In Button (Material 3 standard)
                        Button(
                            onClick = { onLoginSuccess(isProSelected) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("google_login_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Google Icon",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Entrar com o Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Ao continuar, você concorda com nossos Termos de Serviço de assinatura mensal recorrente de R$ 39.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
