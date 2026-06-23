package com.localpulse.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidade") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Política de Privacidade do LocalPulse",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                Text(
                    text = "Última atualização: 23 de Junho de 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Text(
                    text = "1. Coleta de Informações\n" +
                            "O LocalPulse coleta informações de sua conta Google, incluindo nome, endereço de e-mail e foto do perfil, exclusivamente para fins de autenticação e personalização da sua experiência no aplicativo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Text(
                    text = "2. Uso das Informações\n" +
                            "As informações coletadas são usadas para criar seu perfil no aplicativo, fornecer diagnósticos do Google Meu Negócio e apresentar dicas personalizadas baseadas no seu segmento de atuação.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Text(
                    text = "3. Compartilhamento de Dados\n" +
                            "Nós não vendemos, alugamos ou compartilhamos suas informações pessoais com terceiros para fins comerciais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Text(
                    text = "4. Segurança\n" +
                            "Implementamos medidas de segurança para proteger suas informações contra acesso não autorizado, alteração, divulgação ou destruição.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
