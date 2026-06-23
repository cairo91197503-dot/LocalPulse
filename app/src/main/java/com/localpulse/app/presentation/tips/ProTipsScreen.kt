package com.localpulse.app.presentation.tips

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProTipsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dicas Pro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
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
                    "As melhores estratégias para colocar sua empresa no Top 3 das buscas locais. Baseado em recomendações oficiais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                ExpandableTipCard(
                    title = "1. Como o Google decide o Ranking",
                    icon = Icons.Default.TrendingUp
                ) {
                    TipBullet("Relevância:", "O quão bem seu perfil corresponde à busca do cliente (ex: usar as palavras certas).")
                    TipBullet("Distância:", "Proximidade do usuário. Fator orgânico muito importante.")
                    TipBullet("Popularidade:", "Quantidade e qualidade das avaliações, fotos, cliques e presença online.")
                }
            }

            item {
                ExpandableTipCard(
                    title = "2. Configuração Impecável",
                    icon = Icons.Default.Settings
                ) {
                    TipBullet("Nome Real:", "Use o nome exato da sua empresa no mundo real.")
                    TipBullet("Categoria Principal:", "O fator individual mais importante. Seja específico (ex: 'Clínica Odontológica' em vez de apenas 'Saúde'). Adicione categorias secundárias compatíveis.")
                    TipBullet("Completude:", "Cadastre todos os serviços, produtos e mantenha os horários 100% atualizados.")
                }
            }

            item {
                ExpandableTipCard(
                    title = "3. A Mina de Ouro: Avaliações",
                    icon = Icons.Default.Star
                ) {
                    TipBullet("Frequência:", "O segredo não é só ter muitas, mas receber avaliações de forma *constante*. Peça sempre aos clientes.")
                    TipBullet("Responda TODAS:", "Agradeça as notas positivas. Para as negativas, seja profissional e ofereça uma solução. O Google e os clientes novos observam isso.")
                    TipBullet("Metas:", "Alcance rapidamente 50 avaliações com nota superior a 4.7.")
                }
            }

            item {
                ExpandableTipCard(
                    title = "4. Fotos e Postagens",
                    icon = Icons.Default.CameraAlt
                ) {
                    TipBullet("Fotos da Empresa:", "Mostre a fachada (essencial para clientes encontrarem fisicamente), o interior (traz confiança) e a equipe.")
                    TipBullet("Frequência Visual:", "Suba de 3 a 5 fotos de boa qualidade por semana.")
                    TipBullet("Postagens Frequentes:", "2 a 3 por semana. Publique promoções, bastidores ou depoimentos.")
                }
            }

            item {
                ExpandableTipCard(
                    title = "5. SEO Local",
                    icon = Icons.Default.Search
                ) {
                    TipBullet("Palavras-chave:", "Inclua seu serviço principal e sua cidade/bairro nas postagens e respostas a avaliações de forma totalmente natural.")
                    TipBullet("Consistência NAP:", "Nome, Endereço e Telefone devem ser *exatamente iguais* no seu site formatação e no Google.")
                }
            }

            item {
                ExpandableTipCard(
                    title = "6. Erros Restritivos (Evite!)",
                    icon = Icons.Default.Warning
                ) {
                    TipBullet("Falsificações:", "Se comprar avaliações falsas, você corre grande risco de ter o perfil derrubado ou sua visibilidade banida.")
                    TipBullet("Spam de Palavras-chave:", "A prática de encher o título com serviços (ex: 'João Mecânico Troca de Óleo Anápolis') diminui a autoridade.")
                    TipBullet("Desatualização:", "Lojas fechadas com horário como 'aberto' enfurecem os usuários, gerando notas negativas.")
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "A Fórmula do Crescimento",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Top Ranking = Perfil Completo + Avaliações Constantes + Fotos Frequentes + Engajamento Natural.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableTipCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun TipBullet(title: String, description: String) {
    Row(crossAxisAlignment = Alignment.Top) {
        Text(
            "•",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("$title ")
                }
                append(description)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
