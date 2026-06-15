package com.localpulse.app.presentation.diagnosis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessFormScreen(
    viewModel: BusinessFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onDiagnosisReady: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val formData by viewModel.formData.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is BusinessFormUiState.Success -> onDiagnosisReady(state.diagnosisJson)
            is BusinessFormUiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnóstico do Negócio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "A IA vai analisar seu negócio e gerar recomendações personalizadas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                Text("Informações básicas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            item {
                OutlinedTextField(
                    value = formData.businessName,
                    onValueChange = { viewModel.updateBusinessName(it) },
                    label = { Text("Nome do negócio") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = formData.category,
                    onValueChange = { viewModel.updateCategory(it) },
                    label = { Text("Categoria (ex: Restaurante, Salão, Loja)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text("Avaliações no Google",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            item {
                Column {
                    Text(
                        "Nota média: ${formData.averageRating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = formData.averageRating,
                        onValueChange = { viewModel.updateRating(it) },
                        valueRange = 0f..5f,
                        steps = 9
                    )
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("0", style = MaterialTheme.typography.bodySmall)
                        Text("5", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = if (formData.totalReviews == 0) "" else formData.totalReviews.toString(),
                    onValueChange = { viewModel.updateTotalReviews(it.toIntOrNull() ?: 0) },
                    label = { Text("Total de avaliações") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Column {
                    Text(
                        "% de avaliações respondidas: ${formData.respondedPercentage}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = formData.respondedPercentage.toFloat(),
                        onValueChange = { viewModel.updateRespondedPercentage(it.toInt()) },
                        valueRange = 0f..100f
                    )
                }
            }

            item {
                Text("Perfil do Google completo?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            item {
                CheckItem("Tem foto de perfil", formData.hasProfilePhoto) {
                    viewModel.updateHasProfilePhoto(it)
                }
            }
            item {
                CheckItem("Tem horário cadastrado", formData.hasHours) {
                    viewModel.updateHasHours(it)
                }
            }
            item {
                CheckItem("Tem descrição do negócio", formData.hasDescription) {
                    viewModel.updateHasDescription(it)
                }
            }
            item {
                CheckItem("Tem site vinculado", formData.hasWebsite) {
                    viewModel.updateHasWebsite(it)
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.generateDiagnosis() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = formData.businessName.isNotBlank() &&
                              formData.category.isNotBlank() &&
                              uiState !is BusinessFormUiState.Loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState is BusinessFormUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Analisando com IA...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gerar Diagnóstico", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckItem(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
