package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AcademyCard(
    val id: Int,
    val tier: String,
    val tierColor: Long,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val tip: String,
    val xp: Int,
    val checklist: List<String>,
    val quizQuestion: String,
    val quizOptions: List<String>,
    val quizCorrect: Int,
    val cardColor: Long,
    val accentColor: Long,
)

val ACADEMY_CARDS = listOf(
    AcademyCard(
        id = 1, tier = "INICIANTE", tierColor = 0xFF4ade80,
        emoji = "🌱", title = "Perfil Completo", subtitle = "Google Meu Negócio",
        description = "Um perfil 100% completo recebe até 7x mais visitas. Cada campo vazio é uma oportunidade perdida.",
        tip = "Preencha horário, categoria, descrição e adicione pelo menos 5 fotos reais do seu negócio.",
        xp = 100,
        checklist = listOf("Nome do negócio exato e consistente","Endereço e telefone atualizados","Horário de funcionamento completo","Categoria principal correta","5+ fotos do espaço/produto"),
        quizQuestion = "Quantas vezes mais visitas um perfil completo recebe vs incompleto?",
        quizOptions = listOf("2x mais","5x mais","7x mais","10x mais"), quizCorrect = 2,
        cardColor = 0xFF0d3b2e, accentColor = 0xFF4ade80
    ),
    AcademyCard(
        id = 2, tier = "INICIANTE", tierColor = 0xFF4ade80,
        emoji = "⭐", title = "Responda Avaliações", subtitle = "Gestão de Reputação",
        description = "93% dos consumidores leem respostas às avaliações negativas antes de decidir. Silêncio é a pior resposta.",
        tip = "Responda em até 24h. Para negativas: agradeça, peça desculpas, ofereça solução. Nunca discuta.",
        xp = 150,
        checklist = listOf("Responder avaliações positivas com personalização","Responder avaliações negativas sem atacar","Incluir nome do cliente na resposta","Mencionar o nome do negócio naturalmente","Oferecer canal privado para resolver problemas"),
        quizQuestion = "Qual o prazo ideal máximo para responder uma avaliação negativa?",
        quizOptions = listOf("1 hora","24 horas","3 dias","1 semana"), quizCorrect = 1,
        cardColor = 0xFF1a1a2e, accentColor = 0xFFf59e0b
    ),
    AcademyCard(
        id = 3, tier = "INTERMEDIÁRIO", tierColor = 0xFF60a5fa,
        emoji = "📅", title = "Calendário de Posts", subtitle = "Consistência nas Redes",
        description = "Algoritmos priorizam criadores consistentes. Postar 3x por semana regularmente supera postar 7x numa semana e sumir.",
        tip = "Use o LocalPulse para agendar com antecedência. Segunda, Quarta e Sexta é um ritmo sustentável para começar.",
        xp = 200,
        checklist = listOf("Definir frequência mínima semanal","Criar banco de conteúdo (mínimo 2 semanas)","Agendar posts com antecedência","Variar formatos: foto, vídeo, stories, carrossel","Monitorar horários de maior engajamento"),
        quizQuestion = "O que os algoritmos priorizam mais?",
        quizOptions = listOf("Volume máximo de posts","Consistência regular ao longo do tempo","Posts apenas nos fins de semana","Conteúdo somente em vídeo"), quizCorrect = 1,
        cardColor = 0xFF0f172a, accentColor = 0xFF60a5fa
    ),
    AcademyCard(
        id = 4, tier = "INTERMEDIÁRIO", tierColor = 0xFF60a5fa,
        emoji = "🤖", title = "IA como Aliada", subtitle = "Gemini no LocalPulse",
        description = "A IA não substitui sua voz — ela amplifica. Use sugestões como ponto de partida, sempre adicione seu toque pessoal.",
        tip = "Revise toda sugestão da IA antes de publicar. Adicione detalhes locais: nome do bairro, eventos da cidade, referências do seu público.",
        xp = 250,
        checklist = listOf("Usar sugestões de resposta como rascunho","Personalizar com detalhes do negócio","Verificar tom antes de publicar","Analisar sentimento das avaliações via IA","Ajustar estratégia com base nos relatórios"),
        quizQuestion = "Qual é o uso correto da IA para criar conteúdo?",
        quizOptions = listOf("Publicar direto sem revisar","Ignorar e criar tudo do zero","Usar como rascunho e personalizar","Usar apenas para responder negativos"), quizCorrect = 2,
        cardColor = 0xFF1e0a3c, accentColor = 0xFFa78bfa
    ),
    AcademyCard(
        id = 5, tier = "AVANÇADO", tierColor = 0xFFf97316,
        emoji = "📊", title = "Métricas que Importam", subtitle = "Análise de Dados",
        description = "Engajamento real vale mais que seguidores. 1000 fãs ativos superam 100k seguidores passivos em conversão.",
        tip = "Acompanhe: taxa de resposta, sentimento médio, crescimento de avaliações e alcance semanal. Esses 4 dizem tudo.",
        xp = 300,
        checklist = listOf("Definir KPIs mensais claros","Monitorar taxa de resposta às avaliações","Acompanhar evolução do sentimento (positivo/negativo)","Comparar engajamento entre plataformas","Ajustar estratégia mensal com base nos dados"),
        quizQuestion = "Qual métrica melhor indica saúde da reputação?",
        quizOptions = listOf("Número de seguidores","Curtidas por post","Taxa de resposta + sentimento médio","Quantidade de posts publicados"), quizCorrect = 2,
        cardColor = 0xFF1c0a00, accentColor = 0xFFf97316
    ),
    AcademyCard(
        id = 6, tier = "MESTRE", tierColor = 0xFFe879f9,
        emoji = "🚀", title = "Piloto Automático", subtitle = "Expert+ no LocalPulse",
        description = "A automação inteligente libera seu tempo para o que humanos fazem melhor: criar conexões reais com clientes.",
        tip = "Configure gatilhos: avaliação 1-2 estrelas → alerta imediato. Avaliação 5 estrelas → sugestão de compartilhamento.",
        xp = 500,
        checklist = listOf("Configurar alertas por pontuação de avaliação","Ativar sugestões automáticas de resposta","Programar relatório semanal de reputação","Definir regras de piloto automático de posts","Revisar automações mensalmente"),
        quizQuestion = "O piloto automático do Expert+ serve para:",
        quizOptions = listOf("Substituir completamente o gestor","Ampliar capacidade mantendo controle estratégico","Postar conteúdo sem revisão humana","Responder avaliações sem personalização"), quizCorrect = 1,
        cardColor = 0xFF1a0020, accentColor = 0xFFe879f9
    )
)

class AcademyViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("academy_prefs", Context.MODE_PRIVATE)

    enum class CardPhase { OVERVIEW, CHECKLIST, QUIZ, REWARD }

    private val _unlockedCards = MutableStateFlow(
        prefs.getStringSet("unlocked", setOf("1"))?.mapNotNull { it.toIntOrNull() }?.toSet() ?: setOf(1)
    )
    val unlockedCards: StateFlow<Set<Int>> = _unlockedCards.asStateFlow()

    private val _completedCards = MutableStateFlow(
        prefs.getStringSet("completed", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val completedCards: StateFlow<Set<Int>> = _completedCards.asStateFlow()

    val dailyTip: StateFlow<AcademyCard?> = combine(_unlockedCards, _completedCards) { _, completed ->
        ACADEMY_CARDS.firstOrNull { it.id !in completed } ?: ACADEMY_CARDS.lastOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ACADEMY_CARDS.firstOrNull()
    )

    private val _totalXP = MutableStateFlow(prefs.getInt("total_xp", 0))
    val totalXP: StateFlow<Int> = _totalXP.asStateFlow()

    private val _openCardId = MutableStateFlow<Int?>(null)
    val openCardId: StateFlow<Int?> = _openCardId.asStateFlow()

    private val _cardPhase = MutableStateFlow(CardPhase.OVERVIEW)
    val cardPhase: StateFlow<CardPhase> = _cardPhase.asStateFlow()

    private val _checkedItems = MutableStateFlow<Set<Int>>(emptySet())
    val checkedItems: StateFlow<Set<Int>> = _checkedItems.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer.asStateFlow()

    private val _showQuizResult = MutableStateFlow(false)
    val showQuizResult: StateFlow<Boolean> = _showQuizResult.asStateFlow()

    private val _justUnlockedCardId = MutableStateFlow<Int?>(null)
    val justUnlockedCardId: StateFlow<Int?> = _justUnlockedCardId.asStateFlow()

    val maxXP = ACADEMY_CARDS.sumOf { it.xp }

    fun openCard(cardId: Int) {
        if (!_unlockedCards.value.contains(cardId)) return
        _openCardId.value = cardId
        _cardPhase.value = CardPhase.OVERVIEW
        _checkedItems.value = emptySet()
        _selectedAnswer.value = null
        _showQuizResult.value = false
        _justUnlockedCardId.value = null
    }

    fun closeCard() {
        _openCardId.value = null
        _justUnlockedCardId.value = null
    }

    fun setPhase(phase: CardPhase) {
        _cardPhase.value = phase
    }

    fun toggleCheckItem(index: Int) {
        val current = _checkedItems.value.toMutableSet()
        if (current.contains(index)) current.remove(index) else current.add(index)
        _checkedItems.value = current
    }

    fun selectQuizAnswer(index: Int) {
        if (_showQuizResult.value) return
        _selectedAnswer.value = index
        _showQuizResult.value = true
    }

    fun retryQuiz() {
        _selectedAnswer.value = null
        _showQuizResult.value = false
    }

    fun completeCard(card: AcademyCard) {
        val nextId = card.id + 1
        val earned = if (_completedCards.value.contains(card.id)) 0 else card.xp
        val newCompleted = _completedCards.value + card.id
        val newUnlocked = if (nextId <= ACADEMY_CARDS.size) _unlockedCards.value + nextId else _unlockedCards.value
        val newXP = _totalXP.value + earned

        _completedCards.value = newCompleted
        _unlockedCards.value = newUnlocked
        _totalXP.value = newXP

        if (!_unlockedCards.value.contains(nextId) && nextId <= ACADEMY_CARDS.size) {
            _justUnlockedCardId.value = nextId
        }

        prefs.edit()
            .putStringSet("unlocked", newUnlocked.map { it.toString() }.toSet())
            .putStringSet("completed", newCompleted.map { it.toString() }.toSet())
            .putInt("total_xp", newXP)
            .apply()

        _cardPhase.value = CardPhase.REWARD
    }

    fun getNivel(): String = when {
        _totalXP.value < 300 -> "Novato"
        _totalXP.value < 700 -> "Praticante"
        _totalXP.value < 1200 -> "Especialista"
        else -> "Mestre LocalPulse"
    }
}
