package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.models.BusinessProfile
import com.example.data.models.DiagnosticResult
import com.example.data.models.PostSuggestion
import com.example.data.models.Review
import com.example.data.repository.BusinessRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class BusinessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BusinessRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BusinessRepository(database.businessDao())
        
        // Seed default profiles and reviews if empty to enable immediate rich prototyping
        viewModelScope.launch {
            repository.getAllProfiles().first().let { currentList ->
                if (currentList.isEmpty()) {
                    seedDefaultData()
                }
            }
        }
    }

    // --- State Streams ---

    val profiles: StateFlow<List<BusinessProfile>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProfileId = MutableStateFlow<String?>(null)
    val selectedProfileId: StateFlow<String?> = _selectedProfileId.asStateFlow()

    val selectedProfile: StateFlow<BusinessProfile?> = _selectedProfileId
        .flatMapLatest { id ->
            if (id != null) repository.getProfileById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val reviews: StateFlow<List<Review>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id != null) repository.getReviewsForBusiness(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostSuggestion>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id != null) repository.getPostSuggestions(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Action-focused UI states ---

    private val _diagnosticState = MutableStateFlow<UiState<DiagnosticResult>>(UiState.Idle)
    val diagnosticState: StateFlow<UiState<DiagnosticResult>> = _diagnosticState.asStateFlow()

    private val _reviewReplyState = MutableStateFlow<Map<String, UiState<String>>>(emptyMap())
    val reviewReplyState: StateFlow<Map<String, UiState<String>>> = _reviewReplyState.asStateFlow()

    private val _postGenerationState = MutableStateFlow<UiState<PostSuggestion>>(UiState.Idle)
    val postGenerationState: StateFlow<UiState<PostSuggestion>> = _postGenerationState.asStateFlow()

    // --- Actions ---

    fun selectProfile(id: String?) {
        _selectedProfileId.value = id
        _diagnosticState.value = UiState.Idle
        _postGenerationState.value = UiState.Idle
    }

    fun runDiagnostic(profile: BusinessProfile) {
        viewModelScope.launch {
            _diagnosticState.value = UiState.Loading
            try {
                val result = repository.analyzeProfile(profile)
                _diagnosticState.value = UiState.Success(result)
            } catch (e: Exception) {
                _diagnosticState.value = UiState.Error(e.message ?: "Erro desconhecido na análise.")
            }
        }
    }

    fun requestReviewReply(profileName: String, review: Review) {
        viewModelScope.launch {
            val currentMap = _reviewReplyState.value.toMutableMap()
            currentMap[review.id] = UiState.Loading
            _reviewReplyState.value = currentMap

            try {
                val reply = repository.generateReviewAnswer(profileName, review)
                val successMap = _reviewReplyState.value.toMutableMap()
                successMap[review.id] = UiState.Success(reply)
                _reviewReplyState.value = successMap
            } catch (e: Exception) {
                val errorMap = _reviewReplyState.value.toMutableMap()
                errorMap[review.id] = UiState.Error(e.message ?: "Erro ao gerar rascunho.")
                _reviewReplyState.value = errorMap
            }
        }
    }

    fun applyReviewResponseText(review: Review, text: String) {
        viewModelScope.launch {
            val updatedReview = review.copy(
                responseText = text,
                isResponsePending = false
            )
            repository.updateReview(updatedReview)
        }
    }

    fun generatePostSuggestion(category: String, theme: String) {
        val currentProfile = selectedProfile.value ?: return
        viewModelScope.launch {
            _postGenerationState.value = UiState.Loading
            try {
                val suggestion = repository.generatePostDraft(currentProfile.id, currentProfile.name, category, theme)
                _postGenerationState.value = UiState.Success(suggestion)
            } catch (e: Exception) {
                _postGenerationState.value = UiState.Error(e.message ?: "Erro ao gerar ideia de post.")
            }
        }
    }

    fun toggleSavePost(post: PostSuggestion) {
        viewModelScope.launch {
            repository.updatePostSuggestion(post.copy(isSaved = !post.isSaved))
        }
    }

    fun deletePost(id: Int) {
        viewModelScope.launch {
            repository.deletePostSuggestion(id)
        }
    }

    fun addCustomProfile(name: String, category: String, address: String, phone: String, website: String, hours: String, desc: String) {
        viewModelScope.launch {
            val newProfile = BusinessProfile(
                id = "custom_" + System.currentTimeMillis(),
                name = name,
                category = category,
                address = address,
                phone = phone,
                website = website,
                hours = hours,
                description = desc,
                photosCount = 0,
                rating = 0.0,
                reviewsCount = 0,
                reviewsAnsweredCount = 0,
                postsRecentCount = 0,
                hasCompletenessError = true,
                diagnosticScore = 30
            )
            repository.insertProfile(newProfile)
            _selectedProfileId.value = newProfile.id
        }
    }

    // --- Seeding Routine ---

    private suspend fun seedDefaultData() {
        val seedProfiles = listOf(
            BusinessProfile(
                id = "aroma_brew",
                name = "Aroma Brew Café",
                category = "Cafeteria",
                address = "Rua das Flores, 123 - Jardins, São Paulo",
                phone = "+55 11 98765-4321",
                website = "", // Missing critical website!
                hours = "Seg-Sáb: 08:00 - 19:00",
                description = "", // Missing description!
                photosCount = 4, // Too few photos
                rating = 4.2,
                reviewsCount = 18,
                reviewsAnsweredCount = 4, // low response percentage
                postsRecentCount = 0, // No posts in the last month
                hasCompletenessError = true,
                diagnosticScore = 55
            ),
            BusinessProfile(
                id = "barbearia_silva",
                name = "Barbearia Silva",
                category = "Barbearia de Luxo",
                address = "Av. Paulista, 1000 - Bela Vista, São Paulo",
                phone = "+55 11 99999-8888",
                website = "www.barbeariasilva.com.br",
                hours = "Ter-Sáb: 09:00 - 21:00",
                description = "A Barbearia Silva é o espaço ideal para o homem moderno. Oferecemos cortes de cabelo clássicos e modernos, design de barba com navalha e toalhas quentes, e produtos masculinos premium. Ambiente com Wi-Fi, café quentinho e atendimento especial.",
                photosCount = 44,
                rating = 4.8,
                reviewsCount = 120,
                reviewsAnsweredCount = 115,
                postsRecentCount = 3,
                hasCompletenessError = false,
                diagnosticScore = 94
            )
        )

        val seedReviews = listOf(
            Review(
                id = "rev_1",
                businessId = "aroma_brew",
                authorName = "Guilherme Santos",
                authorPhotoUrl = null,
                rating = 5,
                text = "Lugar espetacular! O cappuccino é o melhor que já tomei na vida e o pão de queijo de tapioca sempre vem super quentinho. Só falta ter rede wi-fi liberada pros clientes poderem trabalhar.",
                publishTime = System.currentTimeMillis() - 86400000 * 2,
                responseText = null,
                aiSuggestedResponse = null
            ),
            Review(
                id = "rev_2",
                businessId = "aroma_brew",
                authorName = "Mariana Costa",
                authorPhotoUrl = null,
                rating = 3,
                text = "Fui no sábado de tarde e estava muito lotado. Demoraram mais de 25 minutos para trazer um expresso simples e veio frio. O ambiente é lindo, mas o serviço de entrega falhou.",
                publishTime = System.currentTimeMillis() - 86400000 * 5,
                responseText = null,
                aiSuggestedResponse = null
            ),
            Review(
                id = "rev_3",
                businessId = "aroma_brew",
                authorName = "Carlos Eduardo",
                authorPhotoUrl = null,
                rating = 4,
                text = "Os doces de pistache são incríveis e os baristas entendem demais de grãos especiais. Recomendo o coado da prensa francesa. Preço salgado, mas compensa pro fim de semana.",
                publishTime = System.currentTimeMillis() - 86400000 * 10,
                responseText = "Olá Carlos! Agradecemos demais pelo feedback positivo! Ficamos contentes que tenha gostado do café e de nossos doces finos de pistache. Volte sempre!",
                aiSuggestedResponse = null
            )
        )

        repository.insertProfiles(seedProfiles)
        repository.insertReviews(seedReviews)
    }
}
