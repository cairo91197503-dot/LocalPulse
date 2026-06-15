package com.localpulse.app.presentation.diagnosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localpulse.app.data.gemini.GeminiService
import com.localpulse.app.domain.model.BusinessData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessFormViewModel @Inject constructor(
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<BusinessFormUiState>(BusinessFormUiState.Idle)
    val uiState: StateFlow<BusinessFormUiState> = _uiState.asStateFlow()

    private val _formData = MutableStateFlow(BusinessData())
    val formData: StateFlow<BusinessData> = _formData.asStateFlow()

    fun updateBusinessName(name: String) {
        _formData.value = _formData.value.copy(businessName = name)
    }

    fun updateCategory(category: String) {
        _formData.value = _formData.value.copy(category = category)
    }

    fun updateRating(rating: Float) {
        _formData.value = _formData.value.copy(averageRating = rating)
    }

    fun updateTotalReviews(total: Int) {
        _formData.value = _formData.value.copy(totalReviews = total)
    }

    fun updateRespondedPercentage(percentage: Int) {
        _formData.value = _formData.value.copy(respondedPercentage = percentage)
    }

    fun updateHasProfilePhoto(has: Boolean) {
        _formData.value = _formData.value.copy(hasProfilePhoto = has)
    }

    fun updateHasHours(has: Boolean) {
        _formData.value = _formData.value.copy(hasHours = has)
    }

    fun updateHasDescription(has: Boolean) {
        _formData.value = _formData.value.copy(hasDescription = has)
    }

    fun updateHasWebsite(has: Boolean) {
        _formData.value = _formData.value.copy(hasWebsite = has)
    }

    fun generateDiagnosis() {
        viewModelScope.launch {
            _uiState.value = BusinessFormUiState.Loading
            val result = geminiService.generateDiagnosis(_formData.value)
            result.fold(
                onSuccess = { json ->
                    _uiState.value = BusinessFormUiState.Success(json)
                },
                onFailure = { error ->
                    _uiState.value = BusinessFormUiState.Error(
                        error.message ?: "Erro ao gerar diagnóstico"
                    )
                }
            )
        }
    }
}

sealed class BusinessFormUiState {
    object Idle : BusinessFormUiState()
    object Loading : BusinessFormUiState()
    data class Success(val diagnosisJson: String) : BusinessFormUiState()
    data class Error(val message: String) : BusinessFormUiState()
}
