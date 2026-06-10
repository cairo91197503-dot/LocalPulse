package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _newLowRatingReview = MutableSharedFlow<Review>()
    val newLowRatingReview: SharedFlow<Review> = _newLowRatingReview.asSharedFlow()

    init {
        observeNewLowRatingReviews()
    }

    private fun observeNewLowRatingReviews() {
        viewModelScope.launch {
            reviewRepository.getNewLowRatingReviews().collect { review ->
                _newLowRatingReview.emit(review)
            }
        }
    }
}
