package com.localpulse.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data class Content(val user: User?, val reputationScore: Int = 0) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
                _uiState.value = HomeUiState.Content(user = user, reputationScore = 0)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Falha ao carregar dados. Tente novamente.")
            }
        }
    }
}
