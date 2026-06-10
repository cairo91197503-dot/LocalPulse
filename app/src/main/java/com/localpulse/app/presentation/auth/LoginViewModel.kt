package com.localpulse.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localpulse.app.data.auth.AuthRepository
import com.localpulse.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state of the login screen.
 */
sealed class LoginUiState {
    /** The initial, idle state before any action is taken. */
    object Idle : LoginUiState()
    /** Loading state when a sign-in operation is in progress. */
    object Loading : LoginUiState()
    /** Success state indicating a user has successfully signed in. */
    data class Success(val user: User) : LoginUiState()
    /** Error state with a message when a sign-in operation fails. */
    data class Error(val message: String) : LoginUiState()
}

/**
 * ViewModel for handling login operations and state.
 *
 * @property authRepository The repository for authentication tasks.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Called when a successful Google Sign-In result provides an ID token.
     *
     * @param idToken The Google ID token.
     */
    fun onGoogleSignInResult(idToken: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess { user ->
                _uiState.value = LoginUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error.localizedMessage ?: "Unknown error occurred.")
            }
        }
    }

    /**
     * Called when the Google Sign-In flow encounters an error or is cancelled.
     */
    fun onSignInError() {
        _uiState.value = LoginUiState.Error("Failed to complete Google Sign In.")
    }
}
