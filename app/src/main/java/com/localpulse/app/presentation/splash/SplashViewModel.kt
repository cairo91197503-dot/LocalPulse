package com.localpulse.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.localpulse.app.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela de splash que decide para onde navegar.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination

    init {
        checkDestination()
    }

    private fun checkDestination() {
        viewModelScope.launch {
            val hasSeenOnboarding = appPreferences.hasSeenOnboarding.first()
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

            _destination.value = when {
                !hasSeenOnboarding -> SplashDestination.COURSE_MODULE_1
                isLoggedIn -> SplashDestination.HOME
                else -> SplashDestination.LOGIN
            }
        }
    }
}

enum class SplashDestination {
    COURSE_MODULE_1,
    LOGIN,
    HOME
}
