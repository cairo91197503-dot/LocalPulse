package com.example.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            reviewRepository.getReviews().collect { reviewList ->
                // Sort by date descending
                _reviews.value = reviewList.sortedByDescending { it.date }
            }
        }
    }
}
