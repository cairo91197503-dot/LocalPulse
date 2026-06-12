package com.localpulse.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
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
    fun onGoogleSignInResult(data: android.content.Intent?) {
        viewModelScope.launch {
            try {
                val account = GoogleSignIn
                    .getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
                
                val idToken = account.idToken
                if (idToken == null) {
                    _uiState.value = LoginUiState.Error(
                        "Token nulo - verifique WEB_CLIENT_ID"
                    )
                    return@launch
                }
                
                authRepository.signInWithGoogle(idToken).onSuccess { user ->
                    _uiState.value = LoginUiState.Success(user)
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Erro desconhecido")
                }
            } catch (e: ApiException) {
                _uiState.value = LoginUiState.Error(
                    "Código de erro: ${e.statusCode} - ${e.message}"
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    "Erro: ${e.javaClass.simpleName} - ${e.message}"
                )
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
