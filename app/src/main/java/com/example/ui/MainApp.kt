package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainApp(
    viewModel: BusinessViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val hasSelectedAccountType by viewModel.hasSelectedAccountType.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedReview by viewModel.selectedReview.collectAsState()
    val showInactivityDialog by viewModel.showInactivityDialog.collectAsState()

    // Multiplatform state flows
    val notificationGmb by viewModel.notificationGmb.collectAsState()
    val notificationFacebook by viewModel.notificationFacebook.collectAsState()
    val notificationInstagram by viewModel.notificationInstagram.collectAsState()
    val notificationWhatsApp by viewModel.notificationWhatsApp.collectAsState()
    val notificationTikTok by viewModel.notificationTikTok.collectAsState()

    val isFacebookConnected by viewModel.isFacebookConnected.collectAsState()
    val isInstagramConnected by viewModel.isInstagramConnected.collectAsState()
    val isWhatsAppConnected by viewModel.isWhatsAppConnected.collectAsState()
    val isTikTokConnected by viewModel.isTikTokConnected.collectAsState()

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        // Show Onboarding screen if not signed in / verified
        OnboardingScreen(
            viewModel = viewModel,
            onShowPrivacyPolicy = { showPrivacyPolicyDialog = true }
        )
    } else if (!hasSelectedAccountType) {
        // Show Account Type selection upon first login
        AccountTypeSelectionScreen(
            viewModel = viewModel
        )
    } else {
        // App Main Shell Scaffold
        val userPlan by viewModel.userPlan.collectAsState()
        val showPremiumUpgradeDialog by viewModel.showPremiumUpgradeDialog.collectAsState()
        val showCheckoutDialog by viewModel.showCheckoutDialog.collectAsState()
        val checkoutPlan by viewModel.checkoutPlan.collectAsState()

        if (showPremiumUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissPremiumUpgradeDialog() },
                title = {
                    Text(
                        text = "Opções do PulsePersonal",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Por favor, ative uma das versões ampliadas abaixo, que agora estão inteiramente gratuitas e acessíveis:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // PRO PLAN SELECT CARD
                        Card(
                            onClick = {
                                viewModel.triggerCheckout("PRO")
                                viewModel.dismissPremiumUpgradeDialog()
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Plano PRO", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Grátis", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Sincronize as contas disponíveis e remova propagandas.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // EXPERT+ PLAN SELECT CARD
                        Card(
                            onClick = {
                                viewModel.triggerCheckout("EXPERT_PLUS")
                                viewModel.dismissPremiumUpgradeDialog()
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Plano EXPERT+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                    Text("Grátis", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Agendador inteligente de posts, piloto automático total e sem anúncios.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.dismissPremiumUpgradeDialog() },
                        modifier = Modifier.testTag("dismiss_premium_modal")
                    ) {
                        Text("Depois")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (showCheckoutDialog) {
            com.example.ui.screens.PaymentCheckoutDialog(
                plan = checkoutPlan,
                onDismiss = { viewModel.dismissCheckoutDialog() },
                viewModel = viewModel
            )
        }

        AppTutorialDialog(viewModel = viewModel)

        val postNotificationAlert by viewModel.postNotificationAlert.collectAsState()
        val pendingNotificationPost by viewModel.pendingNotificationPost.collectAsState()
        val mediaRequestPostPending by viewModel.mediaRequestPostPending.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    // Main Bottom Navigation Bar
                    NavigationBar(
                        modifier = Modifier.testTag("main_bottom_nav"),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == "home",
                            onClick = {
                                viewModel.selectReview(null) // Reset nested states
                                viewModel.changeTab("home")
                            },
                            icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Início") },
                            label = { Text("Início", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            modifier = Modifier.testTag("nav_tab_home")
                        )

                        NavigationBarItem(
                            selected = currentTab == "reviews",
                            onClick = {
                                viewModel.selectReview(null)
                                viewModel.changeTab("reviews")
                            },
                            icon = { Icon(imageVector = Icons.Default.RateReview, contentDescription = "Avaliações") },
                            label = { Text("Avaliações", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            modifier = Modifier.testTag("nav_tab_reviews")
                        )

                        NavigationBarItem(
                            selected = currentTab == "posts",
                            onClick = {
                                viewModel.selectReview(null)
                                viewModel.changeTab("posts")
                            },
                            icon = { Icon(imageVector = Icons.Default.PostAdd, contentDescription = "Publicações") },
                            label = { Text("Publicações", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            modifier = Modifier.testTag("nav_tab_posts")
                        )

                        NavigationBarItem(
                            selected = currentTab == "settings",
                            onClick = {
                                viewModel.selectReview(null)
                                viewModel.changeTab("settings")
                            },
                            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Configurações") },
                            label = { Text("Configurações", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            modifier = Modifier.testTag("nav_tab_settings")
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // If nested in review detail, display detail screen regardless of tab (Reviews list master-detail sub-navigation pattern)
                        val activeReview = selectedReview
                        if (activeReview != null) {
                            ReviewDetailScreen(
                                review = activeReview,
                                viewModel = viewModel,
                                onBack = { viewModel.selectReview(null) }
                            )
                        } else {
                            // Switch top level content sheets
                            when (currentTab) {
                                "home" -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToReviews = { viewModel.changeTab("reviews") },
                                    onNavigateToDetail = { viewModel.selectReview(it) }
                                )
                                "reviews" -> ReviewsScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { viewModel.selectReview(it) }
                                )
                                "posts" -> PostsScreen(
                                    viewModel = viewModel
                                )
                                "settings" -> SettingsScreen(
                                    viewModel = viewModel,
                                    onShowPrivacyPolicy = { showPrivacyPolicyDialog = true }
                                )
                            }
                        }
                    }

                    if (userPlan == "FREE") {
                        AdBanner()
                    }
                }
            }

            // Dynamic Alert / Push simulated banner overlay at the top of the interface
            postNotificationAlert?.let { alertMsg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                        .testTag("in_app_notification_banner"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notificação",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Notificação do PulsePersonal",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alertMsg,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                                )
                            }
                            IconButton(onClick = { viewModel.dismissPostNotification() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // If there is a manual post pending attached, show immediate action
                        pendingNotificationPost?.let { pendingPost ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.completeManualPost(pendingPost)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp).testTag("notification_publish_action_btn")
                            ) {
                                Text("Publicar Agora (Ação Requerida)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Expert Plan Media Confirmation Request overlay modal
            mediaRequestPostPending?.let { pedpost ->
                AlertDialog(
                    onDismissRequest = { viewModel.dismissMediaRequest() },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Expert AI",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🤖 Solicitação de Mídia (Expert)", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = "O Planejador Inteligente (Autopilot) planejou a postagem: \n\n\"${pedpost.title}\"\n\nComo as diretrizes de imagem/vídeo exigem sua revisão e anexo, por favor, autorize e confirme a inclusão da mídia correspondente para publicar de forma 100% automática.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.completeExpertPostWithMedia(pedpost)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("expert_media_authorize_btn")
                        ) {
                            Text("Autorizar e Postar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.dismissMediaRequest() },
                            modifier = Modifier.testTag("expert_media_reject_btn")
                        ) {
                            Text("Verificar Mais Tarde")
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }

    // Weekly/5-day inactivity alert dialog indicator
    if (showInactivityDialog && isLoggedIn) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissInactivityDialog() },
            confirmButton = {
                Button(
                    onClick = {
                        val idea = viewModel.inactivityPostIdea
                        viewModel.submitPost(idea.title, idea.content)
                        viewModel.dismissInactivityDialog()
                    },
                    modifier = Modifier.testTag("inactivity_confirm_btn")
                ) {
                    Text("Publicar agora")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissInactivityDialog() },
                    modifier = Modifier.testTag("inactivity_dismiss_btn")
                ) {
                    Text("Lembrar mais tarde")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerta",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Alerta de Inatividade!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Que tal publicar no seu perfil hoje?",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Manter suas mídias sociais integradas atualizadas aumenta o seu engajamento pessoal e a conexão com seus seguidores! Confira abaixo quais canais de postagem estão gerando alerta e veja a sugestão de publicação gerada por IA:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Canais de Conteúdo Integrados:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ReminderStatusRow(
                            name = "Google/YouTube",
                            isConnected = true,
                            isNotificationEnabled = notificationGmb,
                            brandColor = MaterialTheme.colorScheme.primary
                        )
                        ReminderStatusRow(
                            name = "Facebook (Perfil)",
                            isConnected = isFacebookConnected,
                            isNotificationEnabled = notificationFacebook,
                            brandColor = Color(0xFF1877F2)
                        )
                        ReminderStatusRow(
                            name = "Instagram (Pessoal)",
                            isConnected = isInstagramConnected,
                            isNotificationEnabled = notificationInstagram,
                            brandColor = Color(0xFFE1306C)
                        )
                        ReminderStatusRow(
                            name = "WhatsApp (Pessoal)",
                            isConnected = isWhatsAppConnected,
                            isNotificationEnabled = notificationWhatsApp,
                            brandColor = Color(0xFF25D366)
                        )
                        ReminderStatusRow(
                            name = "TikTok (Pessoal)",
                            isConnected = isTikTokConnected,
                            isNotificationEnabled = notificationTikTok,
                            brandColor = Color(0xFF010101)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sugestão Inteligente do Gemini IA:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = viewModel.inactivityPostIdea.title,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = viewModel.inactivityPostIdea.content,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Completely self-standing Brazilian Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    modifier = Modifier.testTag("privacy_policy_confirm")
                ) {
                    Text("Entendi e Aceito")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = "Privacidade",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Política de Privacidade", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 280.dp)) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "A sua privacidade é de extrema importância para nós do PulsePersonal. Esta política esclarece como coletamos, guardamos e interagimos com as APIs integradas de terceiros:\n\n" +
                                    "1. Autenticação e Credenciais:\n" +
                                    "O PulsePersonal utiliza provedores de autenticação padrão para login seguro. O login é direto e seguro nos servidores parceiros, não armazenando senhas ou dados sensíveis de forma insegura.\n\n" +
                                    "2. Integração e Interação:\n" +
                                    "Ao conceder acesso aos perfis sociais (TikTok, Instagram, etc), o aplicativo realiza chamadas estritamente em seu nome para permitir a listagem de interações de seguidores, permitir resposta inteligente ou sugerir ideias criativas de postagens para engajar seu público.\n\n" +
                                    "3. Armazenamento Offline (Cache):\n" +
                                    "Para garantir um desempenho ágil em locais de baixa conexão de dados celulares, cacheamos os feedbacks de redes sociais de forma criptografada localmente via banco de dados local (Room Database). Você poderá limpá-los a qualquer momento apagando os dados de armazenamento do app nas configurações padrão do sistema operacional Android.\n\n" +
                                    "4. Uso de Inteligência Artificial (Gemini API):\n" +
                                    "Ao utilizar recursos generativos, trechos anônimos dos feedbacks recebidos são processados com segurança através da API do Gemini para gerar sugestões contextuais. Absolutamente nenhum dado sensível do perfil é indexado por modelos generativos externos.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ReminderStatusRow(
    name: String,
    isConnected: Boolean,
    isNotificationEnabled: Boolean,
    brandColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(brandColor.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (isNotificationEnabled) brandColor else Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Text(
            text = when {
                !isNotificationEnabled -> "Silenciado"
                isConnected -> "Ativo e Integrado"
                else -> "Ativo (Lembrete sem login)"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNotificationEnabled) brandColor else Color.Gray
        )
    }
}

@Composable
fun AdBanner() {
    var adIndex by remember { mutableStateOf(0) }
    val ads = listOf(
        "💡 Remova as propagandas e sincronize contas sem limites no plano PRO por apenas R$ 9,90!",
        "🚀 Mude para o plano EXPERT+ por R$ 19,90/mês para agendar posts e usar o piloto automático inteligente!",
        "⚡ PulsePersonal Premium: Desbloqueie agendamento, automação completa e retire todos os anúncios!",
        "✨ Dica: Use a Inteligência Artificial do app para otimizar suas ideias de mídias sociais!"
    )
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(6000)
            adIndex = (adIndex + 1) % ads.size
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("simulated_banner_ad"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ANÚNCIO",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = ads[adIndex],
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
