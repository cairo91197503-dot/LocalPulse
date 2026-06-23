package com.localpulse.app.presentation.tips

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

data class ProTipLesson(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val details: List<Pair<String, String>>
)

val proTipLessons = listOf(
    ProTipLesson(
        title = "1. Como o Google decide o Ranking",
        description = "Entenda o algoritmo e os 3 fatores principais que fazem você aparecer no topo.",
        icon = Icons.Default.TrendingUp,
        details = listOf(
            "Relevância" to "O quão bem seu perfil corresponde à busca do cliente (ex: usar as palavras certas).",
            "Distância" to "Proximidade do usuário. Fator orgânico muito importante.",
            "Popularidade" to "Quantidade e qualidade das avaliações, fotos, cliques e presença online."
        )
    ),
    ProTipLesson(
        title = "2. Configuração Impecável",
        description = "As configurações obrigatórias que a maioria das empresas erra.",
        icon = Icons.Default.Settings,
        details = listOf(
            "Nome Real" to "Use o nome exato da sua empresa no mundo real.",
            "Categoria Principal" to "O fator individual mais importante. Seja específico (ex: 'Clínica Odontológica' em vez de apenas 'Saúde'). Adicione categorias secundárias compatíveis.",
            "Completude" to "Cadastre todos os serviços, produtos e mantenha os horários 100% atualizados."
        )
    ),
    ProTipLesson(
        title = "3. A Mina de Ouro: Avaliações",
        description = "Estratégias avançadas para conseguir notas 5 estrelas constantemente.",
        icon = Icons.Default.Star,
        details = listOf(
            "Frequência" to "O segredo não é só ter muitas, mas receber avaliações de forma *constante*. Peça sempre aos clientes.",
            "Responda TODAS" to "Agradeça as notas positivas. Para as negativas, seja profissional e ofereça uma solução. O Google e os clientes novos observam isso.",
            "Metas" to "Alcance rapidamente 50 avaliações com nota superior a 4.7."
        )
    ),
    ProTipLesson(
        title = "4. Fotos e Postagens",
        description = "O conteúdo visual que converte visitantes em clientes.",
        icon = Icons.Default.CameraAlt,
        details = listOf(
            "Fotos da Empresa" to "Mostre a fachada (essencial para clientes encontrarem fisicamente), o interior (traz confiança) e a equipe.",
            "Frequência Visual" to "Suba de 3 a 5 fotos de boa qualidade por semana.",
            "Postagens Frequentes" to "2 a 3 por semana. Publique promoções, bastidores ou depoimentos."
        )
    ),
    ProTipLesson(
        title = "5. SEO Local",
        description = "Otimizações ocultas que turbinam seu alcance.",
        icon = Icons.Default.Search,
        details = listOf(
            "Palavras-chave" to "Inclua seu serviço principal e sua cidade/bairro nas postagens e respostas a avaliações de forma totalmente natural.",
            "Consistência NAP" to "Nome, Endereço e Telefone devem ser *exatamente iguais* no seu site formatação e no Google."
        )
    ),
    ProTipLesson(
        title = "6. Erros Restritivos (Evite!)",
        description = "O que NUNCA fazer se você não quiser ser punido pelo Google.",
        icon = Icons.Default.Warning,
        details = listOf(
            "Falsificações" to "Se comprar avaliações falsas, você corre grande risco de ter o perfil derrubado ou sua visibilidade banida.",
            "Spam de Palavras-chave" to "A prática de encher o título com serviços (ex: 'João Mecânico Troca de Óleo Anápolis') diminui a autoridade.",
            "Desatualização" to "Lojas fechadas com horário como 'aberto' enfurecem os usuários, gerando notas negativas."
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProTipsScreen(onNavigateBack: () -> Unit) {
    var selectedLessonIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedLessonIndex == null) "Dicas Pro" else proTipLessons[selectedLessonIndex!!].title) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedLessonIndex == null) {
                            onNavigateBack()
                        } else {
                            selectedLessonIndex = null
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = selectedLessonIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "LessonTransition"
        ) { activeIndex ->
            if (activeIndex == null) {
                LessonList(padding = padding, onLessonClick = { selectedLessonIndex = it })
            } else {
                ActiveLessonView(padding = padding, lesson = proTipLessons[activeIndex]) {
                    selectedLessonIndex = null
                }
            }
        }
    }
}

@Composable
private fun LessonList(padding: PaddingValues, onLessonClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                "Domine o Google Meu Negócio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Complete as lições abaixo para dominar o ranqueamento local e atrair mais clientes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }

        items(proTipLessons.size) { index ->
            val lesson = proTipLessons[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                lesson.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = lesson.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onLessonClick(index) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar Lição", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveLessonView(padding: PaddingValues, lesson: ProTipLesson, onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { lesson.details.size + 1 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { page ->
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                if (page == 0) {
                    // Intro page
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                lesson.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = lesson.description,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Detail page
                    val detailIndex = page - 1
                    val detail = lesson.details[detailIndex]
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${detailIndex + 1}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        Text(
                            text = detail.first,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = detail.second,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Pager indicators
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .background(
                            if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (pagerState.currentPage < pagerState.pageCount - 1) "Próximo" else "Concluir Lição",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

