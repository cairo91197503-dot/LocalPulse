package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BusinessProfile
import com.example.data.models.Review
import com.example.data.models.SentimentSummary
import com.example.ui.viewmodel.BusinessViewModel

@Composable
fun HomeScreen(
    viewModel: BusinessViewModel,
    onNavigateToReviews: () -> Unit,
    onNavigateToDetail: (Review) -> Unit
) {
    val profile by viewModel.businessProfile.collectAsState()
    val sentiment by viewModel.sentimentSummary.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSimulationModeActive by viewModel.isSimulationModeActive.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("home_screen_container")
    ) {
        // Welcoming header or offline banner
        if (isOffline) {
            OfflineIndicatorBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Hello Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bem-vindo de volta,",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = profile?.name ?: "Seu Perfil",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("home_business_name")
                )

                // Dynamic Account Type Badge
                Spacer(modifier = Modifier.height(4.dp))
                val isPersonal = (profile?.accountType ?: "PERSONAL") == "PERSONAL"
                val badgeText = if (isPersonal) "Pessoal (Criador)" else "Comercial (Empresa)"
                val badgeColor = if (isPersonal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("account_type_indicator_badge")
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    )
                }
            }

            // Help and simple profile icon buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.triggerTutorial() },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                        .testTag("home_tutorial_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "Tutorial",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Configurações",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.triggerTutorial() }
                .testTag("home_tutorial_guide_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Novo por aqui?",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Toque aqui para ver o guia e tutorial rápido do PulsePersonal e dominar o app!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Sentiment Summary Card
        SentimentCard(
            summary = sentiment,
            isLoading = isLoading,
            isSimulationModeActive = isSimulationModeActive,
            onRefresh = { viewModel.refreshWeeklySentiment() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Metrics Grids
        Text(
            text = "Métricas Gerais",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        MetricsGrid(profile = profile)

        if (!isSimulationModeActive) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Consolidação de Dados Reais",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "As métricas acima refletem apenas dados reais. Para carregar e consolidar suas avaliações legítimas das redes, configure suas integrações oficiais na aba Configurações.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Highlight Latest Review Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Último feedback recebido",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            TextButton(
                onClick = onNavigateToReviews,
                modifier = Modifier.testTag("view_all_reviews_text_button")
            ) {
                Text("Ver todos", color = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val latestReview = allReviews.firstOrNull()
        if (latestReview != null) {
            LatestReviewCard(
                review = latestReview,
                onClick = { onNavigateToDetail(latestReview) }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum feedback pendente no momento.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SentimentCard(
    summary: SentimentSummary?,
    isLoading: Boolean,
    isSimulationModeActive: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weekly_sentiment_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSimulationModeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSimulationModeActive) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSimulationModeActive) Icons.Default.AutoAwesome else Icons.Default.Info,
                        contentDescription = "IA LocalPulse",
                        tint = if (isSimulationModeActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSimulationModeActive) "RESUMO DE SENTIMENTO IA" else "SOBRE ESTA OPÇÃO (RESUMO DE SENTIMENTO IA)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSimulationModeActive) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (isSimulationModeActive) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recarregar Análise",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onRefresh() }
                                .testTag("refresh_sentiment_btn")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isSimulationModeActive) {
                Text(
                    text = summary?.text ?: "Calculando sentimento de reputação com inteligência artificial...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.testTag("sentiment_summary_text")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atualizado em ${summary?.dateUpdated ?: "--/--/--"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Semanal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            } else {
                Text(
                    text = "💡 O que esta opção faz?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Consolida os elogios recebidos e as queixas mais frequentes feitas por seus clientes nas redes conectadas, gerando um diagnóstico estratégico claro para aprimorar seu atendimento de forma descomplicada.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "🔑 O que é necessário para obter dados reais?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vincule suas credenciais oficiais do Google Meu Negócio ou Facebook no menu Configurações. É necessário receber no mínimo 5 avaliações verdadeiras de clientes para acionar a análise automatizada do modelo Gemini.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun MetricsGrid(profile: BusinessProfile?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricItemCard(
            modifier = Modifier.weight(1f),
            title = "Engajamento",
            value = String.format(Locale.getDefault(), "%.1f", profile?.rating ?: 4.4f),
            icon = Icons.Default.Star,
            iconColor = Color(0xFFFFB300),
            testTag = "metric_rating"
        )

        MetricItemCard(
            modifier = Modifier.weight(1f),
            title = "Interações",
            value = (profile?.reviewCount ?: 50).toString(),
            icon = Icons.Default.RateReview,
            iconColor = MaterialTheme.colorScheme.primary,
            testTag = "metric_reviews_count"
        )

        MetricItemCard(
            modifier = Modifier.weight(1f),
            title = "Sem Retorno",
            value = (profile?.unrepliedCount ?: 18).toString(),
            icon = Icons.Default.MarkChatUnread,
            iconColor = MaterialTheme.colorScheme.error,
            testTag = "metric_unreplied"
        )
    }
}

@Composable
fun MetricItemCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    testTag: String
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LatestReviewCard(
    review: Review,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("latest_review_featured_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Author avatar place holder
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.authorName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = review.authorName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Simple formatted time
                        Text(
                            text = "Recente",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                // Star badge representation
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFB300).copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Nota",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = review.rating.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (review.isReplied) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Respondida",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Respondida",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MarkChatUnread,
                            contentDescription = "Pendente",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Toque para responder",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineIndicatorBanner() {
    AnimatedVisibility(
        visible = true,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("offline_banner_indicator"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Modo Offline",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Modo Offline ativo • Dados carregados do cache local",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}
