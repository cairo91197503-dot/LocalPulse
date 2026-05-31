package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.BusinessViewModel

data class TutorialStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val accentColor: Color,
    val testTag: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppTutorialDialog(
    viewModel: BusinessViewModel
) {
    val showTutorial by viewModel.showTutorialDialog.collectAsState()
    
    if (!showTutorial) return

    val steps = listOf(
        TutorialStep(
            title = "Bem-vindo ao PulsePersonal",
            description = "O seu assistente inteligente definitivo para gerenciar sua presença local, engajamento e reputação em múltiplas mídias sociais simultaneamente de forma rápida.",
            icon = Icons.Default.AutoAwesome,
            iconColor = MaterialTheme.colorScheme.primary,
            accentColor = MaterialTheme.colorScheme.primaryContainer,
            testTag = "tutorial_step_welcome"
        ),
        TutorialStep(
            title = "Análise de Sentimentos por IA",
            description = "Monitore as avaliações recebidas e use nossa IA integrada em Português para ler, filtrar e gerar respostas personalizadas e insights detalhados de satisfação semanal.",
            icon = Icons.Default.RateReview,
            iconColor = MaterialTheme.colorScheme.secondary,
            accentColor = MaterialTheme.colorScheme.secondaryContainer,
            testTag = "tutorial_step_sentiment"
        ),
        TutorialStep(
            title = "Piloto Automático (Autopilot)",
            description = "Ative o Piloto Automático na aba de Publicações para receber ideias inteligentes prontas e planejar seus posts. No plano Expert+ (agora gratuito), o app cuida de praticamente tudo!",
            icon = Icons.Default.SmartButton,
            iconColor = MaterialTheme.colorScheme.tertiary,
            accentColor = MaterialTheme.colorScheme.tertiaryContainer,
            testTag = "tutorial_step_autopilot"
        ),
        TutorialStep(
            title = "Integração Multiplataforma",
            description = "Conecte perfis do Google, Facebook, Instagram, WhatsApp e TikTok. Sincronize dados e gerencie todos os feeds em um único painel coeso e sem propagandas chatas.",
            icon = Icons.Default.Share,
            iconColor = MaterialTheme.colorScheme.primary,
            accentColor = MaterialTheme.colorScheme.primaryContainer,
            testTag = "tutorial_step_integration"
        ),
        TutorialStep(
            title = "Pronto para Decolar!",
            description = "Análises, agendamentos automáticos e controle total sobre como as pessoas veem seu negócio local. Comece sua nova experiência digital agora mesmo!",
            icon = Icons.Default.RocketLaunch,
            iconColor = Color(0xFF4CAF50),
            accentColor = Color(0xFFE8F5E9),
            testTag = "tutorial_step_ready"
        )
    )

    var currentStepIdx by remember { mutableStateOf(0) }
    val currentStep = steps[currentStepIdx]

    Dialog(
        onDismissRequest = { viewModel.dismissTutorial() },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .testTag("app_tutorial_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Progress text and Skip button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Passo ${currentStepIdx + 1} de ${steps.size}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("tutorial_step_counter")
                    )

                    TextButton(
                        onClick = { viewModel.dismissTutorial() },
                        modifier = Modifier.testTag("tutorial_skip_button")
                    ) {
                        Text(
                            text = "Pular",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sliding Animated Content for the steps
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState.title != steps[0].title) {
                            slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    },
                    modifier = Modifier.weight(weight = 1f, fill = false)
                ) { step ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .testTag(step.testTag)
                    ) {
                        // Visual Icon Container with Accent background
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(step.accentColor.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = step.iconColor,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Text content
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            ),
                            fontSize = 22.sp,
                            modifier = Modifier.testTag("tutorial_step_title")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp
                            ),
                            modifier = Modifier.testTag("tutorial_step_description")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Progress Step dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    steps.forEachIndexed { idx, _ ->
                        val isSelected = currentStepIdx == idx
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSelected) 18.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action controls layout (Previous and Next / Finish buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStepIdx > 0) {
                        OutlinedButton(
                            onClick = { currentStepIdx-- },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("tutorial_prev_button")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Voltar")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    val isLastStep = currentStepIdx == steps.size - 1
                    Button(
                        onClick = {
                            if (isLastStep) {
                                viewModel.dismissTutorial()
                            } else {
                                currentStepIdx++
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastStep) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag(if (isLastStep) "tutorial_finish_button" else "tutorial_next_button")
                    ) {
                        Text(
                            text = if (isLastStep) "Começar" else "Avançar",
                            fontWeight = FontWeight.Bold
                        )
                        if (!isLastStep) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
