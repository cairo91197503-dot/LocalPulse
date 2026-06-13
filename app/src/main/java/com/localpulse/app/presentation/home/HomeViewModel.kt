package com.localpulse.app.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.localpulse.app.data.auth.AuthRepository
import com.localpulse.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state of the home screen.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Content(val user: User?, val reputationScore: Int = 0, val userPhotoUrl: String? = null) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object LoggedOut : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            // Simulate network delay for loading state
            delay(1000)
            try {
                val user = authRepository.getCurrentUser()
                val photoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()
                _uiState.value = HomeUiState.Content(user = user, reputationScore = 0, userPhotoUrl = photoUrl)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Falha ao carregar dados. Tente novamente.")
            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(
            getApplication<Application>(),
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).signOut()
        _uiState.value = HomeUiState.LoggedOut
    }
}
