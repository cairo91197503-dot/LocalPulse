package com.localpulse.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.localpulse.app.navigation.Routes
import com.localpulse.app.presentation.home.HomeScreen
import com.localpulse.app.presentation.settings.SettingsScreen
import com.localpulse.app.presentation.home.HomeUiState
import com.localpulse.app.presentation.home.HomeViewModel
import com.localpulse.app.ui.theme.LocalPulseTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.localpulse.app.presentation.auth.LoginScreen
import com.localpulse.app.presentation.auth.LoginUiState
import com.localpulse.app.presentation.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loginViewModel.onGoogleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val webClientId = getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            LocalPulseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val uiState by loginViewModel.uiState.collectAsState()

                    LaunchedEffect(uiState) {
                        if (uiState is LoginUiState.Success) {
                            if ((uiState as LoginUiState.Success).user.hasBusinessProfile) {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Routes.ONBOARDING_BUSINESS) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Routes.LOGIN
                    ) {
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                uiState = uiState,
                                onSignInClick = {
                                    val signInIntent = googleSignInClient.signInIntent
                                    signInLauncher.launch(signInIntent)
                                }
                            )
                        }
                        composable(Routes.ONBOARDING_BUSINESS) {
                            com.localpulse.app.presentation.onboarding.OnboardingBusinessScreen(
                                onHasBusiness = {
                                    loginViewModel.markBusinessProfileCompleted()
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.ONBOARDING_BUSINESS) { inclusive = true }
                                    }
                                },
                                onNoBusiness = {
                                    // Abrir browser tratado na tela
                                },
                                onSkip = {
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.ONBOARDING_BUSINESS) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.HOME) {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            val homeUiState by homeViewModel.uiState.collectAsState()

                            LaunchedEffect(homeUiState) {
                                if (homeUiState is HomeUiState.LoggedOut) {
                                    googleSignInClient.signOut().addOnCompleteListener {
                                        navController.navigate(Routes.LOGIN) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            }

                            HomeScreen(
                                viewModel = homeViewModel,
                                onSignOutClick = { homeViewModel.signOut() },
                                onNavigateToSettings = {
                                    navController.navigate(Routes.SETTINGS)
                                }
                            )
                        }
                        composable(Routes.SETTINGS) {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            val homeUiState by homeViewModel.uiState.collectAsState()

                            LaunchedEffect(homeUiState) {
                                if (homeUiState is HomeUiState.LoggedOut) {
                                    googleSignInClient.signOut()
                                        .addOnCompleteListener {
                                        navController.navigate(Routes.LOGIN) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            }

                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onSignOut = { homeViewModel.signOut() }
                            )
                        }
                    }
                }
            }
        }
    }
}
