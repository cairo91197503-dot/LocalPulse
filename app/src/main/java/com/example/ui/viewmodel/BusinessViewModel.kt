package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PostItem(
    val id: String,
    val text: String,
    val scheduledTime: String,
    val platforms: List<String>,
    val status: String // "Agendado", "Publicado" ou "Cancelado"
)

class BusinessViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("pulse_prefs", Context.MODE_PRIVATE)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val db = com.example.data.db.AppDatabase.getDatabase(application)
    private val dao = db.localPulseDao()
    private val repository = com.example.data.repository.BusinessRepository(dao)

    // Selection State
    private val _hasSelectedAccountType = MutableStateFlow(prefs.getBoolean("has_selected_account_type", false))
    val hasSelectedAccountType: StateFlow<Boolean> = _hasSelectedAccountType.asStateFlow()

    // Reviews Filter States
    private val _reviewFilter = MutableStateFlow("ALL")
    val reviewFilter: StateFlow<String> = _reviewFilter.asStateFlow()

    val filteredReviews: StateFlow<List<Review>> = combine(
        repository.allReviews,
        _reviewFilter
    ) { list: List<Review>, filter: String ->
        when (filter) {
            "POSITIVE" -> list.filter { it.rating >= 4 }
            "NEGATIVE" -> list.filter { it.rating <= 3 }
            "UNREPLIED" -> list.filter { !it.isReplied }
            else -> list
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // AI suggestion States
    private val _aiReplyDraft = MutableStateFlow("")
    val aiReplyDraft: StateFlow<String> = _aiReplyDraft.asStateFlow()

    private val _isGeneratingReply = MutableStateFlow(false)
    val isGeneratingReply: StateFlow<Boolean> = _isGeneratingReply.asStateFlow()

    // Core States
    private val _businessNameInput = MutableStateFlow(prefs.getString("business_name", "Pulse Personal Creator") ?: "Pulse Personal Creator")
    val businessNameInput: StateFlow<String> = _businessNameInput.asStateFlow()

    private val _accountType = MutableStateFlow(prefs.getString("account_type", "PERSONAL") ?: "PERSONAL")
    val accountType: StateFlow<String> = _accountType.asStateFlow()

    private val _userPlan = MutableStateFlow(prefs.getString("user_plan", "PRO") ?: "PRO")
    val userPlan: StateFlow<String> = _userPlan.asStateFlow()

    private val _alertFrequency = MutableStateFlow(prefs.getString("alert_frequency", "diário") ?: "diário")
    val alertFrequency: StateFlow<String> = _alertFrequency.asStateFlow()

    private val _summaryLanguage = MutableStateFlow(prefs.getString("summary_language", "pt-BR") ?: "pt-BR")
    val summaryLanguage: StateFlow<String> = _summaryLanguage.asStateFlow()

    private val _isOffline = MutableStateFlow(prefs.getBoolean("is_offline", false))
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _isSimulationModeActive = MutableStateFlow(prefs.getBoolean("is_sim_mode", true))
    val isSimulationModeActive: StateFlow<Boolean> = _isSimulationModeActive.asStateFlow()

    // Platform Connections
    private val _isFacebookConnected = MutableStateFlow(prefs.getBoolean("facebook_conn", true))
    val isFacebookConnected: StateFlow<Boolean> = _isFacebookConnected.asStateFlow()

    private val _isInstagramConnected = MutableStateFlow(prefs.getBoolean("instagram_conn", false))
    val isInstagramConnected: StateFlow<Boolean> = _isInstagramConnected.asStateFlow()

    private val _isWhatsAppConnected = MutableStateFlow(prefs.getBoolean("whatsapp_conn", false))
    val isWhatsAppConnected: StateFlow<Boolean> = _isWhatsAppConnected.asStateFlow()

    private val _isTikTokConnected = MutableStateFlow(prefs.getBoolean("tiktok_conn", false))
    val isTikTokConnected: StateFlow<Boolean> = _isTikTokConnected.asStateFlow()

    // Configuration Toggles
    private val _notificationGmb = MutableStateFlow(prefs.getBoolean("notif_gmb", true))
    val notificationGmb: StateFlow<Boolean> = _notificationGmb.asStateFlow()

    private val _notificationFacebook = MutableStateFlow(prefs.getBoolean("notif_fb", true))
    val notificationFacebook: StateFlow<Boolean> = _notificationFacebook.asStateFlow()

    private val _notificationInstagram = MutableStateFlow(prefs.getBoolean("notif_ig", true))
    val notificationInstagram: StateFlow<Boolean> = _notificationInstagram.asStateFlow()

    private val _notificationWhatsApp = MutableStateFlow(prefs.getBoolean("notif_wa", false))
    val notificationWhatsApp: StateFlow<Boolean> = _notificationWhatsApp.asStateFlow()

    private val _notificationTikTok = MutableStateFlow(prefs.getBoolean("notif_tt", false))
    val notificationTikTok: StateFlow<Boolean> = _notificationTikTok.asStateFlow()

    // Tutorial Visibility State
    private val _isTutorialVisible = MutableStateFlow(prefs.getBoolean("tutorial_visible", true))
    val isTutorialVisible: StateFlow<Boolean> = _isTutorialVisible.asStateFlow()

    // Authentication State
    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        if (_isLoggedIn.value) {
            viewModelScope.launch {
                val profile = repository.businessProfile.firstOrNull()
                if (profile == null) {
                    repository.initializeBusiness(_businessNameInput.value, _accountType.value)
                }
            }
        }
    }

    // Campos de login
    private val _loginEmailOrPhoneOrUser = MutableStateFlow("")
    val loginEmailOrPhoneOrUser: StateFlow<String> = _loginEmailOrPhoneOrUser.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    // Campos de registro
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

    // Estados de UI
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode.asStateFlow()

    // Sample list of managed posts
    private val _postsList = MutableStateFlow<List<PostItem>>(
        listOf(
            PostItem("1", "Dicas incríveis de edição móvel! No ar às 19h.", "Hoje, 19:00", listOf("YouTube", "Instagram"), "Agendado"),
            PostItem("2", "A importância da consistência na postagem.", "Amanhã, 10:00", listOf("YouTube", "Facebook"), "Agendado"),
            PostItem("3", "Vídeo de bastidores: Como criamos nossos roteiros.", "Ontem, 15:00", listOf("YouTube", "TikTok"), "Publicado")
        )
    )
    val postsList: StateFlow<List<PostItem>> = _postsList.asStateFlow()

    // Helper Functions to Settle Input / Configuration Values
    fun setBusinessName(name: String) {
        _businessNameInput.value = name
        prefs.edit().putString("business_name", name).apply()
    }

    fun setAccountType(type: String) {
        _accountType.value = type
        prefs.edit().putString("account_type", type).apply()
    }

    fun setUserPlan(plan: String) {
        _userPlan.value = plan
        prefs.edit().putString("user_plan", plan).apply()
    }

    fun setAlertFrequency(freq: String) {
        _alertFrequency.value = freq
        prefs.edit().putString("alert_frequency", freq).apply()
    }

    fun setSummaryLanguage(lang: String) {
        _summaryLanguage.value = lang
        prefs.edit().putString("summary_language", lang).apply()
    }

    fun toggleOffline() {
        val next = !_isOffline.value
        _isOffline.value = next
        prefs.edit().putBoolean("is_offline", next).apply()
    }

    fun setSimulationMode(enabled: Boolean) {
        _isSimulationModeActive.value = enabled
        prefs.edit().putBoolean("is_sim_mode", enabled).apply()
    }

    // Platform toggles
    fun connectFacebook() {
        _isFacebookConnected.value = true
        prefs.edit().putBoolean("facebook_conn", true).apply()
    }
    fun disconnectFacebook() {
        _isFacebookConnected.value = false
        prefs.edit().putBoolean("facebook_conn", false).apply()
    }

    fun connectInstagram() {
        _isInstagramConnected.value = true
        prefs.edit().putBoolean("instagram_conn", true).apply()
    }
    fun disconnectInstagram() {
        _isInstagramConnected.value = false
        prefs.edit().putBoolean("instagram_conn", false).apply()
    }

    fun connectWhatsApp() {
        _isWhatsAppConnected.value = true
        prefs.edit().putBoolean("whatsapp_conn", true).apply()
    }
    fun disconnectWhatsApp() {
        _isWhatsAppConnected.value = false
        prefs.edit().putBoolean("whatsapp_conn", false).apply()
    }

    fun connectTikTok() {
        _isTikTokConnected.value = true
        prefs.edit().putBoolean("tiktok_conn", true).apply()
    }
    fun disconnectTikTok() {
        _isTikTokConnected.value = false
        prefs.edit().putBoolean("tiktok_conn", false).apply()
    }

    // Notification toggles
    fun toggleNotificationGmb() {
        _notificationGmb.value = !_notificationGmb.value
        prefs.edit().putBoolean("notif_gmb", _notificationGmb.value).apply()
    }
    fun toggleNotificationFacebook() {
        _notificationFacebook.value = !_notificationFacebook.value
        prefs.edit().putBoolean("notif_fb", _notificationFacebook.value).apply()
    }
    fun toggleNotificationInstagram() {
        _notificationInstagram.value = !_notificationInstagram.value
        prefs.edit().putBoolean("notif_ig", _notificationInstagram.value).apply()
    }
    fun toggleNotificationWhatsApp() {
        _notificationWhatsApp.value = !_notificationWhatsApp.value
        prefs.edit().putBoolean("notif_wa", _notificationWhatsApp.value).apply()
    }
    fun toggleNotificationTikTok() {
        _notificationTikTok.value = !_notificationTikTok.value
        prefs.edit().putBoolean("notif_tt", _notificationTikTok.value).apply()
    }

    // Tutorial Visibility mutations
    fun triggerTutorial() {
        _isTutorialVisible.value = true
        prefs.edit().putBoolean("tutorial_visible", true).apply()
    }

    fun dismissTutorial() {
        _isTutorialVisible.value = false
        prefs.edit().putBoolean("tutorial_visible", false).apply()
    }

    // Setters para os fluxos de autenticação
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

    // Auth actions real com Firebase Auth
    fun handleLogout() {
        performLogout()
    }

    fun performLogout() {
        firebaseAuth.signOut()
        _isLoggedIn.value = false
        _loginEmailOrPhoneOrUser.value = ""
        _loginPassword.value = ""
        _loginError.value = null
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun handleLogin(businessName: String) {
        setBusinessName(businessName)
        _isLoggedIn.value = true
        prefs.edit().putBoolean("is_logged_in", true).apply()
    }

    fun performLogin() {
        val identity = _loginEmailOrPhoneOrUser.value.trim()
        val password = _loginPassword.value.trim()

        if (identity.isEmpty()) {
            _loginError.value = "Insira um e-mail, celular ou usuário."
            return
        }
        if (password.length < 6) {
            _loginError.value = "A senha deve ter pelo menos 6 caracteres."
            return
        }

        // Se contiver @, tratamos como email. Caso contrário, geramos email placeholder estável
        val email = if (identity.contains("@")) identity else "$identity@placeholder.localpulse"

        viewModelScope.launch {
            _isAuthLoading.value = true
            _loginError.value = null
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val displayName = firebaseAuth.currentUser?.displayName ?: _registerBusinessName.value
                if (!displayName.isNullOrBlank()) {
                    setBusinessName(displayName)
                }
                _isLoggedIn.value = true
                prefs.edit().putBoolean("is_logged_in", true).apply()
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginError.value = "E-mail ou senha incorretos."
            } catch (e: FirebaseAuthInvalidUserException) {
                _loginError.value = "Usuário não cadastrado. Mude para a aba 'Criar Conta'."
            } catch (e: Exception) {
                _loginError.value = e.localizedMessage ?: "Erro ao realizar login."
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun performRegister() {
        val email = _registerEmail.value.trim()
        val password = _registerPassword.value.trim()
        val displayName = _registerUsername.value.trim()
        val businessName = _registerBusinessName.value.trim()

        if (email.isEmpty() || !email.contains("@")) {
            _loginError.value = "Insira um e-mail válido."
            return
        }
        if (password.length < 6) {
            _loginError.value = "A senha deve ter pelo menos 6 caracteres."
            return
        }
        if (displayName.isEmpty()) {
            _loginError.value = "Como deseja ser chamado?"
            return
        }
        if (businessName.isEmpty()) {
            _loginError.value = "Defina o nome da sua Marca/Canal."
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            _loginError.value = null
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                result.user?.updateProfile(profileUpdates)?.await()
                setBusinessName(businessName)
                _isLoggedIn.value = true
                prefs.edit().putBoolean("is_logged_in", true).apply()
            } catch (e: FirebaseAuthWeakPasswordException) {
                _loginError.value = "Escolha uma senha mais forte (mínimo 6 caracteres)."
            } catch (e: FirebaseAuthUserCollisionException) {
                _loginError.value = "Este e-mail já está sendo usado por outra conta."
            } catch (e: Exception) {
                _loginError.value = e.localizedMessage ?: "Erro ao registrar usuário."
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    // Social mock logins para compilação garantida no OnboardingScreen
    fun loginWithGoogle() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            setBusinessName("Google Creator")
            _isLoggedIn.value = true
            _isAuthLoading.value = false
        }
    }

    fun loginWithFacebook() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            setBusinessName("Facebook Creator")
            _isLoggedIn.value = true
            _isAuthLoading.value = false
        }
    }

    fun loginWithInstagram() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            setBusinessName("Instagram Creator")
            _isLoggedIn.value = true
            _isAuthLoading.value = false
        }
    }

    fun loginWithTikTok() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            setBusinessName("TikTok Creator")
            _isLoggedIn.value = true
            _isAuthLoading.value = false
        }
    }

    // Add scheduled post
    fun addPost(text: String, scheduledTime: String, platforms: List<String>) {
        val nextId = (_postsList.value.size + 1).toString()
        val newPost = PostItem(nextId, text, scheduledTime, platforms, "Agendado")
        _postsList.value = listOf(newPost) + _postsList.value
    }

    fun setHasSelectedAccountType(selected: Boolean) {
        _hasSelectedAccountType.value = selected
        prefs.edit().putBoolean("has_selected_account_type", selected).apply()
        viewModelScope.launch {
            repository.initializeBusiness(_businessNameInput.value, _accountType.value)
        }
    }

    fun setReviewFilter(filter: String) {
        _reviewFilter.value = filter
    }

    fun generateAIReply(review: Review) {
        viewModelScope.launch {
            _isGeneratingReply.value = true
            try {
                val suggestion = repository.generateResponseSuggestion(review, _businessNameInput.value)
                _aiReplyDraft.value = suggestion
            } catch (e: Exception) {
                _aiReplyDraft.value = "Muito obrigado pelo seu feedback positivo e pela visita!"
            } finally {
                _isGeneratingReply.value = false
            }
        }
    }

    fun submitFeedback(reviewId: String, replyText: String) {
        viewModelScope.launch {
            try {
                repository.replyToReview(reviewId, replyText)
                _aiReplyDraft.value = "" // Clear draft on successful submission
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
