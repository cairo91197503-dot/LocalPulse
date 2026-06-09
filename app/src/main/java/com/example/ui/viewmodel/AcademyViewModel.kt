package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Academy
import com.example.data.models.Course
import com.example.data.models.Enrollment
import com.example.data.models.Review
import com.example.data.repository.AcademyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AcademyViewModel(private val repository: AcademyRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val academiesList: StateFlow<List<Academy>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                repository.allAcademies
            } else {
                repository.getAcademiesByCategory(category)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enrollmentsList: StateFlow<List<Enrollment>> = repository.allEnrollments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAcademy = MutableStateFlow<Academy?>(null)
    val selectedAcademy: StateFlow<Academy?> = _selectedAcademy.asStateFlow()

    val selectedAcademyCourses: StateFlow<List<Course>> = _selectedAcademy
        .filterNotNull()
        .flatMapLatest { academy ->
            repository.getCoursesByAcademyId(academy.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedAcademyReviews: StateFlow<List<Review>> = _selectedAcademy
        .filterNotNull()
        .flatMapLatest { academy ->
            repository.getReviewsForAcademy(academy.id)
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

    fun selectAcademy(academy: Academy?) {
        _selectedAcademy.value = academy
    }

    fun enrollInCourse(courseId: Int, academyId: Int, userGoal: String) {
        viewModelScope.launch {
            _isLoadingAi.value = true
            repository.enrollInCourse(courseId, academyId, currentUserEmail, userGoal)
            _isLoadingAi.value = false
        }
    }

    fun leaveCourse(enrollment: Enrollment) {
        viewModelScope.launch {
            repository.leaveCourse(enrollment)
        }
    }

    fun submitReview(academyId: Int, rating: Float, reviewText: String) {
        if (reviewText.isBlank()) return
        viewModelScope.launch {
            val review = Review(
                targetId = academyId,
                targetType = "academy",
                userEmail = currentUserEmail,
                reviewText = reviewText,
                rating = rating
            )
            repository.insertReview(review)
        }
    }

    fun getAiCourseRecommendations(goal: String) {
        if (goal.isBlank()) return
        viewModelScope.launch {
            _isLoadingAi.value = true
            _aiRecommendations.value = "Tailoring study path recommendations..."
            try {
                val academies = repository.allAcademies.first()
                val courses = mutableListOf<Course>()
                for (ac in academies) {
                    courses.addAll(repository.getCoursesByAcademyId(ac.id).first())
                }
                val aiResponse = repository.getAiLearningRecommendations(goal, academies, courses)
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

class AcademyViewModelFactory(private val repository: AcademyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AcademyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AcademyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
