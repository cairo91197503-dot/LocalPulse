package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Business
import com.example.data.models.Review
import com.example.data.repository.BusinessRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BusinessViewModel(private val repository: BusinessRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val businessesList: StateFlow<List<Business>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                repository.allBusinesses
            } else {
                repository.getBusinessesByCategory(category)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBusiness = MutableStateFlow<Business?>(null)
    val selectedBusiness: StateFlow<Business?> = _selectedBusiness.asStateFlow()

    val selectedBusinessReviews: StateFlow<List<Review>> = _selectedBusiness
        .filterNotNull()
        .flatMapLatest { business ->
            repository.getReviewsForBusiness(business.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiRecommendations = MutableStateFlow<String>("")
    val aiRecommendations: StateFlow<String> = _aiRecommendations.asStateFlow()

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    val currentUserEmail = "resident@localpulse.com"

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectBusiness(business: Business?) {
        _selectedBusiness.value = business
    }

    fun recommendBusiness(businessId: Int) {
        viewModelScope.launch {
            repository.recommendBusiness(businessId)
            // Refresh selected business detail if active
            val current = _selectedBusiness.value
            if (current != null && current.id == businessId) {
                _selectedBusiness.value = current.copy(recommendedCount = current.recommendedCount + 1)
            }
        }
    }

    fun submitReview(businessId: Int, rating: Float, reviewText: String) {
        if (reviewText.isBlank()) return
        viewModelScope.launch {
            val review = Review(
                targetId = businessId,
                targetType = "business",
                userEmail = currentUserEmail,
                reviewText = reviewText,
                rating = rating
            )
            repository.insertReview(review)
        }
    }

    fun getAiBusinessSuggestions(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoadingAi.value = true
            _aiRecommendations.value = "Consulting LocalPulse AI Guide..."
            try {
                // Get pre-filtered or standard businesses to ground recommendation
                val businesses = businessesList.value
                val aiResponse = repository.getAiBusinessRecommendations(query, businesses)
                _aiRecommendations.value = aiResponse
            } catch (e: Exception) {
                _aiRecommendations.value = "Unable to fetch recommendation: ${e.message}"
            } finally {
                _isLoadingAi.value = false
            }
        }
    }

    fun clearAiRecommendations() {
        _aiRecommendations.value = ""
    }
}

class BusinessViewModelFactory(private val repository: BusinessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
