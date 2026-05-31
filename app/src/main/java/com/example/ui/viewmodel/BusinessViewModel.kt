package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PostItem(
    val id: String,
    val text: String,
    val scheduledTime: String,
    val platforms: List<String>,
    val status: String // "Agendado", "Publicado" ou "Cancelado"
)

class BusinessViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("pulse_prefs", Context.MODE_PRIVATE)

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
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

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

    // Auth actions
    fun handleLogout() {
        _isLoggedIn.value = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun handleLogin(businessName: String) {
        setBusinessName(businessName)
        _isLoggedIn.value = true
        prefs.edit().putBoolean("is_logged_in", true).apply()
    }

    // Add scheduled post
    fun addPost(text: String, scheduledTime: String, platforms: List<String>) {
        val nextId = (_postsList.value.size + 1).toString()
        val newPost = PostItem(nextId, text, scheduledTime, platforms, "Agendado")
        _postsList.value = listOf(newPost) + _postsList.value
    }
}
