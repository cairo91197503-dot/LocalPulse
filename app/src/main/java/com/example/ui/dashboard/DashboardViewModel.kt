package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class WeeklySummary(
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val sentimentDistribution: Map<String, Int> = emptyMap()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _weeklySummary = MutableStateFlow(WeeklySummary())
    val weeklySummary: StateFlow<WeeklySummary> = _weeklySummary.asStateFlow()

    init {
        loadWeeklySummary()
    }

    private fun loadWeeklySummary() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.time

        viewModelScope.launch {
            reviewRepository.getReviewsFrom(oneWeekAgo).collect { reviews ->
                val total = reviews.size
                val avg = if (total > 0) reviews.map { it.rating }.average().toFloat() else 0f
                val sentiments = reviews.groupingBy { 
                    it.sentiment.ifEmpty { "Desconhecido" }
                }.eachCount()

                _weeklySummary.update {
                    it.copy(
                        averageRating = avg,
                        totalReviews = total,
                        sentimentDistribution = sentiments
                    )
                }
            }
        }
    }
}
