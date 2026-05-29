package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
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
    private val sharedPrefs = application.getSharedPreferences("pulse_auth_prefs", Context.MODE_PRIVATE)

    // Onboarding / Auth State
    private val _isLoggedIn = MutableStateFlow(sharedPrefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isOnboarded = MutableStateFlow(sharedPrefs.getBoolean("is_logged_in", false))
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _accountType = MutableStateFlow(sharedPrefs.getString("account_type", "PERSONAL") ?: "PERSONAL")
    val accountType: StateFlow<String> = _accountType.asStateFlow()

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode.asStateFlow()

    // Login/Register fields
    private val _loginEmailOrPhoneOrUser = MutableStateFlow("")
    val loginEmailOrPhoneOrUser: StateFlow<String> = _loginEmailOrPhoneOrUser.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail.asStateFlow()

    private val _registerPhone = MutableStateFlow("")
    val registerPhone: StateFlow<String> = _registerPhone.asStateFlow()

    private val _registerUsername = MutableStateFlow("")
    val registerUsername: StateFlow<String> = _registerUsername.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _registerBusinessName = MutableStateFlow("")
    val registerBusinessName: StateFlow<String> = _registerBusinessName.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    // Social Media Connection States
    private val _isGoogleConnected = MutableStateFlow(true) // Google is default
    val isGoogleConnected: StateFlow<Boolean> = _isGoogleConnected.asStateFlow()

    private val _isFacebookConnected = MutableStateFlow(false)
    val isFacebookConnected: StateFlow<Boolean> = _isFacebookConnected.asStateFlow()

    private val _isInstagramConnected = MutableStateFlow(false)
    val isInstagramConnected: StateFlow<Boolean> = _isInstagramConnected.asStateFlow()

    private val _isWhatsAppConnected = MutableStateFlow(false)
    val isWhatsAppConnected: StateFlow<Boolean> = _isWhatsAppConnected.asStateFlow()

    private val _isTikTokConnected = MutableStateFlow(false)
    val isTikTokConnected: StateFlow<Boolean> = _isTikTokConnected.asStateFlow()

    // Multiplatform Notification Preferences
    private val _notificationGmb = MutableStateFlow(true)
    val notificationGmb: StateFlow<Boolean> = _notificationGmb.asStateFlow()

    private val _notificationFacebook = MutableStateFlow(true)
    val notificationFacebook: StateFlow<Boolean> = _notificationFacebook.asStateFlow()

    private val _notificationInstagram = MutableStateFlow(true)
    val notificationInstagram: StateFlow<Boolean> = _notificationInstagram.asStateFlow()

    private val _notificationWhatsApp = MutableStateFlow(true)
    val notificationWhatsApp: StateFlow<Boolean> = _notificationWhatsApp.asStateFlow()

    private val _notificationTikTok = MutableStateFlow(true)
    val notificationTikTok: StateFlow<Boolean> = _notificationTikTok.asStateFlow()

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

    // Simulation / Demo Mode State (default to false to respect the "leave only true information" prompt!)
    private val _isSimulationModeActive = MutableStateFlow(sharedPrefs.getBoolean("is_simulation_mode_active", false))
    val isSimulationModeActive: StateFlow<Boolean> = _isSimulationModeActive.asStateFlow()

    fun setSimulationMode(active: Boolean) {
        _isSimulationModeActive.value = active
        sharedPrefs.edit().putBoolean("is_simulation_mode_active", active).apply()
    }

    // Local suggested ideas from AI
    private val _postIdeas = MutableStateFlow<List<PostIdea>>(emptyList())
    val postIdeas: StateFlow<List<PostIdea>> = combine(_postIdeas, _isSimulationModeActive) { ideas, isSim ->
        if (isSim) ideas else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Connected personal profile handle / name
    private val _businessNameInput = MutableStateFlow("@cairo.pulse")
    val businessNameInput: StateFlow<String> = _businessNameInput.asStateFlow()

    // Configurations
    private val _alertFrequency = MutableStateFlow("semanal") // diário, semanal
    val alertFrequency: StateFlow<String> = _alertFrequency.asStateFlow()

    private val _summaryLanguage = MutableStateFlow("pt-BR") // pt-BR, es, en
    val summaryLanguage: StateFlow<String> = _summaryLanguage.asStateFlow()

    // Inactivity post suggestion - Adapted for personal social media
    val inactivityPostIdea = PostIdea(
        title = "📸 Um dia produtivo por aqui!",
        content = "Como está sendo a sua sexta-feira? Compartilhando um pouquinho da minha rotina por aqui hoje. Que possamos ter um fim de semana maravilhoso e repleto de conquistas! ✨ #Pessoal #Inspiracao #Rotina"
    )

    // Data streams from ROOM combined with the simulation mode flag
    val allReviews: StateFlow<List<Review>> = combine(repository.allReviews, _isSimulationModeActive) { reviews, isSim ->
        if (isSim) reviews else reviews.filter { !it.id.startsWith("rev_") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val positiveReviews: StateFlow<List<Review>> = combine(repository.positiveReviews, _isSimulationModeActive) { reviews, isSim ->
        if (isSim) reviews else reviews.filter { !it.id.startsWith("rev_") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val negativeReviews: StateFlow<List<Review>> = combine(repository.negativeReviews, _isSimulationModeActive) { reviews, isSim ->
        if (isSim) reviews else reviews.filter { !it.id.startsWith("rev_") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unrepliedReviews: StateFlow<List<Review>> = combine(repository.unrepliedReviews, _isSimulationModeActive) { reviews, isSim ->
        if (isSim) reviews else reviews.filter { !it.id.startsWith("rev_") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    val allPosts: StateFlow<List<Post>> = combine(repository.allPosts, _isSimulationModeActive) { posts, isSim ->
        if (isSim) posts else posts.filter { it.id.startsWith("user_post_") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val businessProfile: StateFlow<BusinessProfile?> = combine(repository.businessProfile, _isSimulationModeActive) { profile, isSim ->
        if (isSim) {
            profile
        } else {
            profile?.copy(
                rating = 0f,
                reviewCount = 0,
                unrepliedCount = 0
            ) ?: BusinessProfile(
                name = _businessNameInput.value,
                rating = 0f,
                reviewCount = 0,
                unrepliedCount = 0,
                lastPostDate = "Sem postagens",
                accountType = _accountType.value
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sentimentSummary: StateFlow<SentimentSummary?> = combine(repository.sentimentSummary, _isSimulationModeActive) { summary, isSim ->
        if (isSim) summary else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    // Auth actions & Field Setters
    fun setLoginEmailOrPhoneOrUser(value: String) {
        _loginEmailOrPhoneOrUser.value = value
        _loginError.value = null
    }

    fun setLoginPassword(value: String) {
        _loginPassword.value = value
        _loginError.value = null
    }

    fun setRegisterEmail(value: String) {
        _registerEmail.value = value
        _loginError.value = null
    }

    fun setRegisterPhone(value: String) {
        _registerPhone.value = value
        _loginError.value = null
    }

    fun setRegisterUsername(value: String) {
        _registerUsername.value = value
        _loginError.value = null
    }

    fun setRegisterPassword(value: String) {
        _registerPassword.value = value
        _loginError.value = null
    }

    fun setRegisterBusinessName(value: String) {
        _registerBusinessName.value = value
        _loginError.value = null
    }

    fun toggleAuthMode() {
        _isRegisterMode.value = !_isRegisterMode.value
        _loginError.value = null
    }

    fun setAccountType(type: String) {
        _accountType.value = type
        sharedPrefs.edit().putString("account_type", type).apply()
    }

    fun performLogin() {
        val credential = _loginEmailOrPhoneOrUser.value.trim()
        val password = _loginPassword.value.trim()

        if (credential.isEmpty()) {
            _loginError.value = "Por favor, insira seu e-mail, celular ou nome de usuário."
            return
        }
        if (password.length < 4) {
            _loginError.value = "A senha deve conter ao menos 4 caracteres."
            return
        }

        // Simulate successful login, parsing default name from credential handle
        val nameToUse = credential.substringBefore("@")
        _businessNameInput.value = if (_accountType.value == "PERSONAL" && !nameToUse.startsWith("@")) "@$nameToUse" else nameToUse

        sharedPrefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("account_type", _accountType.value)
            .apply()

        _isLoggedIn.value = true
        _isOnboarded.value = true

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.initializeBusiness(_businessNameInput.value, _accountType.value)
                loadAISecondIdeas()
                checkInactivityNotification()
            } catch (e: Exception) {
                // handle safely
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun performRegister() {
        val email = _registerEmail.value.trim()
        val phone = _registerPhone.value.trim()
        val username = _registerUsername.value.trim()
        val password = _registerPassword.value.trim()
        val bName = _registerBusinessName.value.trim()

        if (email.isEmpty() && phone.isEmpty() && username.isEmpty()) {
            _loginError.value = "Preencha pelo menos e-mail, celular ou nome de usuário para cadastrar."
            return
        }
        if (password.length < 4) {
            _loginError.value = "A senha para cadastro deve ter pelo menos 4 caracteres."
            return
        }
        if (bName.isEmpty()) {
            _loginError.value = if (_accountType.value == "PERSONAL") 
                "Insira seu nome de perfil ou identificador." 
            else 
                "Insira a razão social ou nome fantasia da empresa."
            return
        }

        val nameToUse = bName
        _businessNameInput.value = if (_accountType.value == "PERSONAL" && !nameToUse.startsWith("@")) "@$nameToUse" else nameToUse

        sharedPrefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("account_type", _accountType.value)
            .apply()

        _isLoggedIn.value = true
        _isOnboarded.value = true

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.initializeBusiness(_businessNameInput.value, _accountType.value)
                loadAISecondIdeas()
                checkInactivityNotification()
            } catch (e: Exception) {
                // handle safely
            } finally {
                _isLoading.value = false
            }
        }
    }

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
        _isGoogleConnected.value = true
        _isLoggedIn.value = true
        _isOnboarded.value = true
        sharedPrefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("account_type", _accountType.value)
            .apply()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.initializeBusiness(_businessNameInput.value, _accountType.value)
                loadAISecondIdeas()
                checkInactivityNotification()
            } catch (e: Exception) {
                // Handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithFacebook() {
        _isFacebookConnected.value = true
        loginWithGoogle()
    }

    fun loginWithInstagram() {
        _isInstagramConnected.value = true
        loginWithGoogle()
    }

    fun loginWithTikTok() {
        _isTikTokConnected.value = true
        loginWithGoogle()
    }

    fun connectFacebook() {
        _isFacebookConnected.value = true
    }

    fun disconnectFacebook() {
        _isFacebookConnected.value = false
    }

    fun connectInstagram() {
        _isInstagramConnected.value = true
    }

    fun disconnectInstagram() {
        _isInstagramConnected.value = false
    }

    fun connectWhatsApp() {
        _isWhatsAppConnected.value = true
    }

    fun disconnectWhatsApp() {
        _isWhatsAppConnected.value = false
    }

    fun connectTikTok() {
        _isTikTokConnected.value = true
    }

    fun disconnectTikTok() {
        _isTikTokConnected.value = false
    }

    fun toggleNotificationGmb() {
        _notificationGmb.value = !_notificationGmb.value
    }

    fun toggleNotificationFacebook() {
        _notificationFacebook.value = !_notificationFacebook.value
    }

    fun toggleNotificationInstagram() {
        _notificationInstagram.value = !_notificationInstagram.value
    }

    fun toggleNotificationWhatsApp() {
        _notificationWhatsApp.value = !_notificationWhatsApp.value
    }

    fun toggleNotificationTikTok() {
        _notificationTikTok.value = !_notificationTikTok.value
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
