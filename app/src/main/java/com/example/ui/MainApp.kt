package com.example.ui

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.viewmodel.BusinessViewModel
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.theme.LocalPulseTheme

@Composable
fun MainApp(
    userEmail: String = "cairo91197503@gmail.com"
) {
    val businessViewModel: BusinessViewModel = viewModel()
    val academyViewModel: AcademyViewModel = viewModel()

    var isLoggedIn by remember { mutableStateOf(false) }
    var isProPlan by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf("onboarding") } // "onboarding", "home", "dashboard", "academy", "settings"

    val profiles by businessViewModel.profiles.collectAsState()
    val selectedProfile by businessViewModel.selectedProfile.collectAsState()

    LocalPulseTheme {
        Crossfade(targetState = currentScreen, label = "screen_root_transitions") { screen ->
            when (screen) {
                "onboarding" -> {
                    OnboardingScreen(
                        onLoginSuccess = { isProSelected ->
                            isProPlan = isProSelected
                            isLoggedIn = true
                            currentScreen = "home"
                        }
                    )
                }
                "home" -> {
                    HomeScreen(
                        profiles = profiles,
                        onSelectProfile = { id ->
                            businessViewModel.selectProfile(id)
                            currentScreen = "dashboard"
                        },
                        onAddProfile = { name, category, addr, phone, web, hrs, dsc ->
                            businessViewModel.addCustomProfile(name, category, addr, phone, web, hrs, dsc)
                            currentScreen = "dashboard"
                        },
                        onNavigateToAcademy = {
                            currentScreen = "academy"
                        },
                        onNavigateToSettings = {
                            currentScreen = "settings"
                        }
                    )
                }
                "dashboard" -> {
                    selectedProfile?.let { profile ->
                        DashboardScreen(
                            viewModel = businessViewModel,
                            profile = profile,
                            onBack = {
                                businessViewModel.selectProfile(null)
                                currentScreen = "home"
                            }
                        )
                    } ?: run {
                        currentScreen = "home"
                    }
                }
                "academy" -> {
                    AcademyScreen(
                        viewModel = academyViewModel,
                        onBack = {
                            currentScreen = "home"
                        }
                    )
                }
                "settings" -> {
                    SettingsScreen(
                        userEmail = userEmail,
                        isPro = isProPlan,
                        onToggleProMode = { isProPlan = it },
                        onBack = {
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}
