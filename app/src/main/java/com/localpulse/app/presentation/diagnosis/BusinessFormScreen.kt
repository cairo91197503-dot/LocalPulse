package com.localpulse.app.presentation.diagnosis

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localpulse.app.domain.model.BusinessData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessFormScreen(
    viewModel: BusinessFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onDiagnosisReady: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val formData by viewModel.formData.collectAsState()
    val step by viewModel.currentStep.collectAsState()
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
                title = { Text("Diagnóstico com IA") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .systemBarsPadding()
        ) {
            // Barra de progresso das etapas
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) { index ->
                    LinearProgressIndicator(
                        progress = { if (step > index) 1f else if (step == index) 0.5f else 0f },
                        modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                    )
                }
            }
            Text(
                "Etapa ${step + 1} de 2",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { currentStep ->
                when (currentStep) {
                    0 -> Step1LinkInput(
                        businessLink = formData.businessLink ?: "",
                        businessName = formData.businessName,
                        onLinkChange = { viewModel.updateBusinessLink(it) },
                        onNameChange = { viewModel.updateBusinessName(it) },
                        onNext = { viewModel.goToStep2() }
                    )
                    1 -> Step2QuickData(
                        formData = formData,
                        isLoading = uiState is BusinessFormUiState.Loading,
                        onRatingChange = { viewModel.updateRating(it) },
                        onReviewsChange = { viewModel.updateTotalReviews(it) },
                        onRespondedChange = { viewModel.updateRespondedPercentage(it) },
                        onHasPhotoChange = { viewModel.updateHasProfilePhoto(it) },
                        onHasHoursChange = { viewModel.updateHasHours(it) },
                        onHasDescriptionChange = { viewModel.updateHasDescription(it) },
                        onBack = { viewModel.goToStep1() },
                        onGenerate = { viewModel.generateDiagnosis() }
                    )
                }
            }
        }
    }
}

@Composable
private fun Step1LinkInput(
    businessLink: String,
    businessName: String,
    onLinkChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(
                "🔗 Link do seu negócio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Cole o link do Google Maps do seu negócio para começarmos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = businessLink,
                onValueChange = onLinkChange,
                label = { Text("Link do Google Maps") },
                placeholder = { Text("https://maps.app.goo.gl/...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (businessLink.isNotEmpty()) {
                        IconButton(onClick = { onLinkChange("") }) {
                            Icon(Icons.Default.Clear, "Limpar")
                        }
                    }
                }
            )
        }

        // Tutorial como pegar o link
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Help,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Como encontrar o link?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    TutorialStep("1", "Abra o Google Maps no celular")
                    TutorialStep("2", "Pesquise pelo nome do seu negócio")
                    TutorialStep("3", "Toque no nome do negócio para abrir")
                    TutorialStep("4", "Toque em \"Compartilhar\" ↗")
                    TutorialStep("5", "Selecione \"Copiar link\"")
                    TutorialStep("6", "Cole o link no campo acima")
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://maps.google.com")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir Google Maps")
                    }
                }
            }
        }

        item {
            Text(
                "Nome do negócio",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = businessName,
                onValueChange = onNameChange,
                label = { Text("Ex: Restaurante do João") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = businessName.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Continuar →",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun Step2QuickData(
    formData: BusinessData,
    isLoading: Boolean,
    onRatingChange: (Float) -> Unit,
    onReviewsChange: (Int) -> Unit,
    onRespondedChange: (Int) -> Unit,
    onHasPhotoChange: (Boolean) -> Unit,
    onHasHoursChange: (Boolean) -> Unit,
    onHasDescriptionChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onGenerate: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(
                "📊 Dados rápidos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Responda rápido olhando seu Google Maps.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "⭐ Nota atual: ${"$"}{String.format("%.1f", formData.averageRating)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = formData.averageRating,
                        onValueChange = onRatingChange,
                        valueRange = 0f..5f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("0.0", style = MaterialTheme.typography.labelSmall)
                        Text("5.0", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "💬 Total de avaliações: ${"$"}{formData.totalReviews}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = formData.totalReviews.toFloat(),
                        onValueChange = { onReviewsChange(it.toInt()) },
                        valueRange = 0f..500f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("0", style = MaterialTheme.typography.labelSmall)
                        Text("500+", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "↩️ % respondidas: ${"$"}{formData.respondedPercentage}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = formData.respondedPercentage.toFloat(),
                        onValueChange = { onRespondedChange(it.toInt()) },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Seu perfil tem...",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    QuickToggle("📸 Foto de perfil", formData.hasProfilePhoto, onHasPhotoChange)
                    QuickToggle("🕐 Horário cadastrado", formData.hasHours, onHasHoursChange)
                    QuickToggle("📝 Descrição preenchida", formData.hasDescription, onHasDescriptionChange)
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("← Voltar")
                }
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.weight(2f).height(56.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Analisando...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gerar diagnóstico", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    number,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
private fun QuickToggle(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
