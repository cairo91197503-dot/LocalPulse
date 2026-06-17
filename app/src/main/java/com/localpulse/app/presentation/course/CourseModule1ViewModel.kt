package com.localpulse.app.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localpulse.app.data.preferences.AppPreferences
import com.localpulse.app.domain.model.CourseLesson
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

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    fun nextPage() {
        if (_currentPage.value < lessons.size - 1) {
            _currentPage.value++
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun completeModule() {
        viewModelScope.launch {
            appPreferences.setHasSeenOnboarding(true)
        }
    }
}
