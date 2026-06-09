package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProBanner(
    onShowPlansClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val benefits = listOf(
        "Ganhe tempo com respostas via IA ilimitadas.",
        "Saia na frente com o diagnóstico completo do perfil.",
        "Atraia mais clientes com sugestões de posts IA."
    )
    
    // Rotacionar em cada recomposição inicial
    var currentBenefit = remember { benefits.random() }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentBenefit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(onClick = onShowPlansClick) {
                Text("Ver planos")
            }
        }
    }
}
