package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: BusinessViewModel
) {
    val navController = rememberNavController()
    val isTutorialVisible by viewModel.isTutorialVisible.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var activeTab by remember { mutableStateOf("home") }

    // Floating onboarding Tutorial dialog check
    if (isTutorialVisible) {
        AppTutorialDialog(
            onDismiss = { viewModel.dismissTutorial() }
        )
    }

    if (!isLoggedIn) {
        // Display a simplified login dashboard card that can be completed with a business title
        var businessText by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .testTag("login_screen_container"),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("login_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Pulse Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Entrar no PulsePersonal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Escolha um nome para seu perfil de marketing para começar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = businessText,
                        onValueChange = { businessText = it },
                        label = { Text("Nome da Marca ou Canal") },
                        placeholder = { Text("Ex: Pulse Personal Creator") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_brand_input_field")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (businessText.trim().isNotBlank()) {
                                viewModel.handleLogin(businessText.trim())
                            }
                        },
                        enabled = businessText.trim().isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_btn")
                    ) {
                        Text("Iniciar Gerenciador", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("global_bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = {
                            activeTab = "home"
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Painel") },
                        label = { Text("Painel") },
                        modifier = Modifier.testTag("nav_tab_home")
                    )
                    NavigationBarItem(
                        selected = activeTab == "posts",
                        onClick = {
                            activeTab = "posts"
                            navController.navigate("posts") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = "Cronograma") },
                        label = { Text("Posts") },
                        modifier = Modifier.testTag("nav_tab_posts")
                    )
                    NavigationBarItem(
                        selected = activeTab == "settings",
                        onClick = {
                            activeTab = "settings"
                            navController.navigate("settings") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
                        label = { Text("Ajustes") },
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            },
            modifier = Modifier.testTag("main_scaffold")
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToPosts = {
                            activeTab = "posts"
                            navController.navigate("posts")
                        },
                        onNavigateToSettings = {
                            activeTab = "settings"
                            navController.navigate("settings")
                        }
                    )
                }
                composable("posts") {
                    PostsScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            activeTab = "home"
                            navController.navigate("home")
                        }
                    )
                }
                composable("settings") {
                    var showPrivacyPolicy by remember { mutableStateOf(false) }

                    if (showPrivacyPolicy) {
                        AlertDialog(
                            onDismissRequest = { showPrivacyPolicy = false },
                            confirmButton = {
                                Button(
                                    onClick = { showPrivacyPolicy = false },
                                    modifier = Modifier.testTag("privacy_policy_confirm_button")
                                ) {
                                    Text("Entendido")
                                }
                            },
                            title = { Text("Política de Privacidade") },
                            text = {
                                Text(
                                    "O aplicativo PulsePersonal protege rigorosamente todas as credenciais de segurança e APIs do usuário. Nenhum dado de conexão de redes sociais (Facebook, Instagram, YouTube e TikTok) é coletado ou transmitido a servidores de terceiros. A sua privacidade é nossa maior prioridade."
                                )
                            },
                            modifier = Modifier.testTag("privacy_policy_dialog")
                        )
                    }

                    SettingsScreen(
                        viewModel = viewModel,
                        onShowPrivacyPolicy = { showPrivacyPolicy = true }
                    )
                }
            }
        }
    }
}
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
