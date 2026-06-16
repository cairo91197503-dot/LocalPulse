package com.localpulse.app.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localpulse.app.data.preferences.AppPreferences
import com.localpulse.app.domain.model.CourseLesson
import com.localpulse.app.domain.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseModule1ViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val lessons = listOf(
        CourseLesson(
            emoji = "🗺️",
            title = "O que é o Google Meu Negócio?",
            description = "É a ferramenta gratuita do Google que permite que sua empresa apareça na Pesquisa Google e no Google Maps quando clientes buscam produtos ou serviços como os seus.",
            bulletPoints = listOf(
                "100% gratuito para usar",
                "Aparece na Pesquisa Google e Maps",
                "Disponível para qualquer tipo de negócio"
            )
        ),
        CourseLesson(
            emoji = "👥",
            title = "Como os clientes te encontram?",
            description = "Quando alguém pesquisa por um produto ou serviço perto dele, o Google mostra os negócios mais relevantes. Ter um perfil completo aumenta muito suas chances de aparecer.",
            bulletPoints = listOf(
                "46% das buscas no Google têm intenção local",
                "76% das pessoas visitam o negócio no mesmo dia",
                "Perfis completos recebem 7x mais visitas"
            )
        ),
        CourseLesson(
            emoji = "⭐",
            title = "O poder das avaliações",
            description = "As avaliações dos clientes são um dos fatores mais importantes para seu ranqueamento no Google e para a decisão de compra de novos clientes.",
            bulletPoints = listOf(
                "88% dos consumidores confiam em avaliações online",
                "Responder avaliações aumenta a credibilidade",
                "Nota acima de 4.0 gera muito mais cliques"
            )
        ),
        CourseLesson(
            emoji = "📊",
            title = "O que você pode gerenciar?",
            description = "Com o Google Meu Negócio você tem controle total sobre como sua empresa aparece online e acessa dados valiosos sobre seus clientes.",
            bulletPoints = listOf(
                "Informações: endereço, telefone e horário",
                "Fotos do negócio e produtos",
                "Postagens e promoções",
                "Métricas de visualizações e cliques"
            )
        ),
        CourseLesson(
            emoji = "🚀",
            title = "LocalPulse vai te ajudar!",
            description = "O LocalPulse usa Inteligência Artificial para analisar seu perfil e te dar recomendações personalizadas para melhorar sua reputação online e atrair mais clientes.",
            bulletPoints = listOf(
                "Diagnóstico completo do seu perfil",
                "Score de reputação em tempo real",
                "Ações prioritárias com IA",
                "QR Code para mais avaliações"
            )
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            question = "Qual é o principal benefício do Google Meu Negócio?",
            options = listOf(
                "Criar anúncios pagos",
                "Aparecer gratuitamente na Pesquisa e Maps",
                "Vender produtos online",
                "Criar um site profissional"
            ),
            correctIndex = 1,
            explanation = "O Google Meu Negócio é 100% gratuito e permite que sua empresa apareça na Pesquisa Google e no Maps!"
        ),
        QuizQuestion(
            question = "Qual porcentagem das buscas no Google têm intenção local?",
            options = listOf("12%", "23%", "46%", "78%"),
            correctIndex = 2,
            explanation = "46% das buscas têm intenção local! Isso significa que quase metade das pessoas que pesquisam estão procurando negócios perto delas."
        ),
        QuizQuestion(
            question = "Perfis completos no Google recebem quantas vezes mais visitas?",
            options = listOf("2x", "4x", "7x", "10x"),
            correctIndex = 2,
            explanation = "Perfis completos recebem até 7x mais visitas! Vale a pena preencher todas as informações do seu negócio."
        )
    )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _showQuiz = MutableStateFlow(false)
    val showQuiz: StateFlow<Boolean> = _showQuiz

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore

    private val _moduleCompleted = MutableStateFlow(false)
    val moduleCompleted: StateFlow<Boolean> = _moduleCompleted

    fun nextPage() {
        if (_currentPage.value < lessons.size - 1) {
            _currentPage.value++
        } else {
            _showQuiz.value = true
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun selectAnswer(index: Int) {
        if (_selectedAnswer.value == null) {
            _selectedAnswer.value = index
            if (index == quizQuestions[_currentQuizIndex.value].correctIndex) {
                _quizScore.value++
            }
        }
    }

    fun nextQuestion() {
        if (_currentQuizIndex.value < quizQuestions.size - 1) {
            _currentQuizIndex.value++
            _selectedAnswer.value = null
        } else {
            _moduleCompleted.value = true
        }
    }

    fun completeModule() {
        viewModelScope.launch {
            appPreferences.setHasSeenOnboarding(true)
        }
    }
}
