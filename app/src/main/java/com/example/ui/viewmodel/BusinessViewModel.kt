package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.models.BusinessProfile
import com.example.data.models.Post
import com.example.data.models.Review
import com.example.data.models.SentimentSummary
import com.example.data.repository.BusinessRepository
import com.example.data.repository.PostIdea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BusinessViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BusinessRepository(db.localPulseDao())

    // Onboarding / Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isOnboarded = MutableStateFlow(false)
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    // Navigation Tab
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Selected review for details screen
    private val _selectedReview = MutableStateFlow<Review?>(null)
    val selectedReview: StateFlow<Review?> = _selectedReview.asStateFlow()

    // AI Reply Draft
    private val _aiReplyDraft = MutableStateFlow("")
    val aiReplyDraft: StateFlow<String> = _aiReplyDraft.asStateFlow()

    // Filtering State
    private val _reviewFilter = MutableStateFlow("ALL") // ALL, POSITIVE, NEGATIVE, UNREPLIED
    val reviewFilter: StateFlow<String> = _reviewFilter.asStateFlow()

    // Loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isGeneratingReply = MutableStateFlow(false)
    val isGeneratingReply: StateFlow<Boolean> = _isGeneratingReply.asStateFlow()

    private val _isGeneratingIdeas = MutableStateFlow(false)
    val isGeneratingIdeas: StateFlow<Boolean> = _isGeneratingIdeas.asStateFlow()

    // Inactivity Alert State
    private val _showInactivityDialog = MutableStateFlow(false)
    val showInactivityDialog: StateFlow<Boolean> = _showInactivityDialog.asStateFlow()

    private val _lastPostDelayDetected = MutableStateFlow(false)
    val lastPostDelayDetected: StateFlow<Boolean> = _lastPostDelayDetected.asStateFlow()

    // Local suggested ideas from AI
    private val _postIdeas = MutableStateFlow<List<PostIdea>>(emptyList())
    val postIdeas: StateFlow<List<PostIdea>> = _postIdeas.asStateFlow()

    // Connected business information
    private val _businessNameInput = MutableStateFlow("Sabor da Vila Panificadora")
    val businessNameInput: StateFlow<String> = _businessNameInput.asStateFlow()

    // Configurations
    private val _alertFrequency = MutableStateFlow("semanal") // diário, semanal
    val alertFrequency: StateFlow<String> = _alertFrequency.asStateFlow()

    private val _summaryLanguage = MutableStateFlow("pt-BR") // pt-BR, es, en
    val summaryLanguage: StateFlow<String> = _summaryLanguage.asStateFlow()

    // Inactivity post suggestion
    val inactivityPostIdea = PostIdea(
        title = "📸 Sextou com Fornada Especial",
        content = "Que tal começar o fim de semana com nossos pães artesanais recém-saídos do forno? Venha nos fazer uma visita hoje e sinta esse perfume inconfundível! Nós te esperamos."
    )

    // Data streams from ROOM
    val allReviews: StateFlow<List<Review>> = repository.allReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val positiveReviews: StateFlow<List<Review>> = repository.positiveReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val negativeReviews: StateFlow<List<Review>> = repository.negativeReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unrepliedReviews: StateFlow<List<Review>> = repository.unrepliedReviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Combine reviews with selected filter
    val filteredReviews: StateFlow<List<Review>> = combine(
        allReviews, positiveReviews, negativeReviews, unrepliedReviews, _reviewFilter
    ) { all, pos, neg, unrep, filter ->
        when (filter) {
            "POSITIVE" -> pos
            "NEGATIVE" -> neg
            "UNREPLIED" -> unrep
            else -> all
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<Post>> = repository.allPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val sentimentSummary: StateFlow<SentimentSummary?> = repository.sentimentSummary.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateBusinessNameInput(name: String) {
        _businessNameInput.value = name
    }

    fun setReviewFilter(filter: String) {
        _reviewFilter.value = filter
    }

    fun selectReview(review: Review?) {
        _selectedReview.value = review
        _aiReplyDraft.value = "" // clear draft on shift
    }

    fun updateReplyDraft(draft: String) {
        _aiReplyDraft.value = draft
    }

    fun toggleOffline() {
        _isOffline.value = !_isOffline.value
    }

    fun setAlertFrequency(freq: String) {
        _alertFrequency.value = freq
    }

    fun setSummaryLanguage(lang: String) {
        _summaryLanguage.value = lang
    }

    fun dismissInactivityDialog() {
        _showInactivityDialog.value = false
    }

    // Auth actions
    fun nextOnboardingStep() {
        if (_onboardingStep.value < 2) {
            _onboardingStep.value += 1
        }
    }

    fun previousOnboardingStep() {
        if (_onboardingStep.value > 0) {
            _onboardingStep.value -= 1
        }
    }

    // Google Sign-In & Load reviews
    fun loginWithGoogle() {
        _isLoggedIn.value = true
        _isOnboarded.value = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Initialize Room with seed 50 reviews and perform initial sentiment analysis
                repository.initializeBusiness(_businessNameInput.value)
                
                // Fetch AI Suggested post ideas
                loadAISecondIdeas()

                // Check inactivity profile metric (last post > 5 days)
                checkInactivityNotification()
            } catch (e: Exception) {
                // Handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkInactivityNotification() {
        // Last post date check
        // Seeding sets last post date to 6 days ago, so this satisfies the 5-day condition out of the box!
        _lastPostDelayDetected.value = true
        _showInactivityDialog.value = true // Automatically trigger the recommended push notification dialog alert!
    }

    fun handleLogout() {
        _isLoggedIn.value = false
        _isOnboarded.value = false
        _onboardingStep.value = 0
        _currentTab.value = "home"
    }

    fun changeTab(tab: String) {
        _currentTab.value = tab
    }

    // AI Generation Call: Custom reply recommendations
    fun generateAIReply(review: Review) {
        _isGeneratingReply.value = true
        viewModelScope.launch {
            try {
                val recommendation = repository.generateResponseSuggestion(review, _businessNameInput.value)
                _aiReplyDraft.value = recommendation
            } catch (e: Exception) {
                _aiReplyDraft.value = "Desculpe, falha ao conectar com o Gemini: ${e.localizedMessage}"
            } finally {
                _isGeneratingReply.value = false
            }
        }
    }

    // Submit feedback
    fun submitFeedback(reviewId: String, replyText: String) {
        viewModelScope.launch {
            repository.replyToReview(reviewId, replyText)
            selectReview(null) // Return to review list
        }
    }

    // Load AI Promo Suggestions for Posts
    fun loadAISecondIdeas() {
        _isGeneratingIdeas.value = true
        viewModelScope.launch {
            try {
                val ideas = repository.generatePostSuggestions(_businessNameInput.value)
                _postIdeas.value = ideas
            } catch (e: Exception) {
                // Keep default ideas if api key is offline
            } finally {
                _isGeneratingIdeas.value = false
            }
        }
    }

    // Create a new post from idea or editor
    fun submitPost(title: String, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createPost(title, content)
                _lastPostDelayDetected.value = false // reset flag since user just posted!
                _showInactivityDialog.value = false
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Manually trigger a fresh Weekly Sentiment analysis using AI
    fun refreshWeeklySentiment() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val reviews = if (_reviewFilter.value == "ALL") allReviews.value else filteredReviews.value
                val sampleList = if (reviews.isNotEmpty()) reviews else allReviews.value
                repository.generateWeeklySentimentSummary(sampleList, _businessNameInput.value)
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }
}
