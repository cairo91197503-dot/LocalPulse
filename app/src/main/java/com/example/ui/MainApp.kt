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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.BusinessViewModel
import com.example.ui.viewmodel.AcademyViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.School

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
        OnboardingScreen(viewModel = viewModel)
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
                        selected = activeTab == "academy",
                        onClick = {
                            activeTab = "academy"
                            navController.navigate("academy") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        icon = { Icon(Icons.Default.School, contentDescription = "Academy") },
                        label = { Text("Academy") },
                        modifier = Modifier.testTag("nav_tab_academy")
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
                composable("academy") {
                    val academyViewModel: AcademyViewModel = viewModel()
                    AcademyScreen(viewModel = academyViewModel)
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
