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
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedReview by viewModel.selectedReview.collectAsState()
    val showInactivityDialog by viewModel.showInactivityDialog.collectAsState()

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        // Show Onboarding screen if not signed in / verified
        OnboardingScreen(
            viewModel = viewModel,
            onShowPrivacyPolicy = { showPrivacyPolicyDialog = true }
        )
    } else {
        // App Main Shell Scaffold
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
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
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
                        icon = { Icon(imageVector = Icons.Default.PostAdd, contentDescription = "Posts") },
                        label = { Text("Posts", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
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
                        text = "Que tal postar uma foto do seu negócio hoje?",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Nenhuma atualização foi feita no seu Google Meu Negócio nos últimos 5 dias. Postagens frequentes aumentam as visualizações em até 40%!\n\nSugestão rápida de IA para postar hoje:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
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
                            text = "A sua privacidade é de extrema importância para nós do LocalPulse. Esta política esclarece como coletamos, guardamos e interagimos com as APIs integradas de terceiros:\n\n" +
                                    "1. Autenticação e Credenciais:\n" +
                                    "O LocalPulse utiliza o Firebase Auth para autenticação via provedor Google. O login é seguro e direto nos servidores do ecossistema Google, não armazenando senhas ou dados sensíveis em servidores próprios do aplicativo.\n\n" +
                                    "2. Google Business Profile API:\n" +
                                    "Ao conceder acesso ao escopo 'business.manage', o aplicativo realiza chamadas em seu nome estritamente para listar avaliações recebidas por clientes locais do seu estabelecimento comercial, permitir a publicação direta de respostas às mesmas ou a criação e agendamento de postagens promocionais.\n\n" +
                                    "3. Armazenamento Offline (Cache):\n" +
                                    "Para garantir um desempenho ágil em locais de baixa conexão de dados celulares, cacheamos as revisões e resumos de forma criptografada localmente via banco de dados (Room Room Database). Você poderá desativá-lo a qualquer momento limpando os dados do aplicativo nas preferências do sistema Android.\n\n" +
                                    "4. Uso de Inteligência Artificial (Gemini API):\n" +
                                    "Ao utilizar recursos como 'Resumos semanais' ou 'Responder com IA', trechos anônimos dos feedbacks (texto escrito pelo cliente, nota e nome do autor) são passados à API do Gemini do Google AI Studio para geração de sugestões contextuais. Nenhum dado financeiro ou de identificação privada é compartilhado com os modelos generativos.",
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
