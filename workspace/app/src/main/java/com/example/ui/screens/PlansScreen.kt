package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    onNavigateBack: () -> Unit,
    onSubscribeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escolha o melhor plano para o seu negócio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PlanCard(
                title = "Grátis",
                price = "R$ 0",
                features = listOf(
                    Feature("Diagnóstico básico", true),
                    Feature("5 avaliações visíveis", true),
                    Feature("3 respostas IA/mês", true),
                    Feature("Gerador QRCode", true),
                    Feature("Sugestões de posts", false),
                    Feature("Alertas em tempo real", false)
                ),
                isHighlighted = false,
                buttonText = "Seu plano atual",
                onButtonClick = {}
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PlanCard(
                title = "Pro",
                price = "R$ 39/mês",
                features = listOf(
                    Feature("Diagnóstico detalhado completo", true),
                    Feature("Todas as avaliações liberadas", true),
                    Feature("Respostas IA ilimitadas", true),
                    Feature("Gerador QRCode", true),
                    Feature("Sugestões infinitas de posts", true),
                    Feature("Alertas de review negativa", true),
                    Feature("Relatório mensal em PDF", true),
                    Feature("Comparativo com concorrentes", true)
                ),
                isHighlighted = true,
                buttonText = "Assinar agora",
                onButtonClick = onSubscribeClick
            )
        }
    }
}

data class Feature(val text: String, val included: Boolean)

@Composable
fun PlanCard(
    title: String,
    price: String,
    features: List<Feature>,
    isHighlighted: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    val containerColor = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (feature.included) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Incluído",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Não incluído",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (feature.included) contentColor else contentColor.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                enabled = isHighlighted
            ) {
                Text(buttonText)
            }
        }
    }
}
