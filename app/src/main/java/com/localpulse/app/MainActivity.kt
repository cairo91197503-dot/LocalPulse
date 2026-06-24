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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
            }
        }

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

                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH
                    ) {
                        composable(Routes.SPLASH) {
                            com.localpulse.app.presentation.splash.SplashScreen(
                                onNavigate = { destination ->
                                    when (destination) {
                                        com.localpulse.app.presentation.splash.SplashDestination.COURSE_MODULE_1 -> {
                                            navController.navigate(Routes.COURSE_MODULE_1) {
                                                popUpTo(Routes.SPLASH) { inclusive = true }
                                            }
                                        }
                                        com.localpulse.app.presentation.splash.SplashDestination.LOGIN -> {
                                            navController.navigate(Routes.LOGIN) {
                                                popUpTo(Routes.SPLASH) { inclusive = true }
                                            }
                                        }
                                        com.localpulse.app.presentation.splash.SplashDestination.HOME -> {
                                            navController.navigate(Routes.HOME) {
                                                popUpTo(Routes.SPLASH) { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        composable(Routes.COURSE_MODULE_1) {
                            com.localpulse.app.presentation.course.CourseModule1Screen(
                                onCourseCompleted = {
                                    if (navController.previousBackStackEntry != null && navController.previousBackStackEntry?.destination?.route != Routes.SPLASH) {
                                        navController.popBackStack()
                                    } else {
                                        navController.navigate(Routes.LOGIN) {
                                            popUpTo(Routes.COURSE_MODULE_1) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable(Routes.LOGIN) {
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
                                },
                                onNavigateToDiagnosis = {
                                    navController.navigate(Routes.BUSINESS_FORM)
                                },
                                onNavigateToQrCode = {
                                    navController.navigate(Routes.QR_CODE)
                                },
                                onNavigateToTips = {
                                    navController.navigate(Routes.TIPS)
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

                            val user = (homeUiState as? HomeUiState.Success)?.user

                            SettingsScreen(
                                user = user,
                                onNavigateBack = { navController.popBackStack() },
                                onSignOut = { homeViewModel.signOut() },
                                onNavigateToPrivacyPolicy = { navController.navigate(Routes.PRIVACY_POLICY) }
                            )
                        }
                        composable(Routes.PRIVACY_POLICY) {
                            com.localpulse.app.presentation.settings.PrivacyPolicyScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.BUSINESS_FORM) {
                            com.localpulse.app.presentation.diagnosis.BusinessFormScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onDiagnosisReady = { json ->
                                    navController.navigate("${Routes.DIAGNOSIS_RESULT}/${android.net.Uri.encode(json)}")
                                }
                            )
                        }
                        composable("${Routes.DIAGNOSIS_RESULT}/{json}") { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("json") ?: ""
                            com.localpulse.app.presentation.diagnosis.DiagnosisResultScreen(
                                diagnosisJson = android.net.Uri.decode(json),
                                onNavigateBack = { navController.popBackStack() },
                                onNewDiagnosis = {
                                    navController.navigate(Routes.BUSINESS_FORM) {
                                        popUpTo(Routes.DIAGNOSIS_RESULT) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.QR_CODE) {
                            com.localpulse.app.presentation.qrcode.QrCodeScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.TIPS) {
                            com.localpulse.app.presentation.tips.TipsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToCourse = { navController.navigate(Routes.COURSE_MODULE_1) },
                                onNavigateToProTips = { navController.navigate(Routes.PRO_TIPS) }
                            )
                        }
                        composable(Routes.PRO_TIPS) {
                            com.localpulse.app.presentation.tips.ProTipsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
