package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BusinessProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profiles: List<BusinessProfile>,
    onSelectProfile: (String) -> Unit,
    onAddProfile: (String, String, String, String, String, String, String) -> Unit,
    onNavigateToAcademy: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showStepGuide by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryPurple, PrimaryCyan)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OfflineBolt,
                                contentDescription = "Pulse",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text("LocalPulse", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAcademy) {
                        Icon(imageVector = Icons.Default.School, contentDescription = "Academy", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_custom_profile_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Perfil")
            }
        },
        containerColor = DarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Seus Perfis do Google",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Selecione uma empresa integrada ou comece o diagnóstico completo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Existing Profiles List
            if (profiles.isNotEmpty()) {
                items(profiles) { profile ->
                    BusinessProfileCard(
                        profile = profile,
                        onClick = { onSelectProfile(profile.id) }
                    )
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Store",
                                tint = TextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Nenhuma empresa cadastrada",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Toque no botão + para adicionar manualmente os dados de uma empresa existente ou use nossos modelos demonstrativos.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Expanded/Collapsible Step-By-Step Register Wizard
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13172E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF28325C), RoundedCornerShape(16.dp))
                        .clickable { showStepGuide = !showStepGuide },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF22294F), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Help",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Ainda não tem perfil no Google?",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Guia Passo-a-Passo de Registro",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Icon(
                                imageVector = if (showStepGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = TextSecondary
                            )
                        }

                        AnimatedVisibility(
                            visible = showStepGuide,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp, start = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Divider(color = Color(0xFF28325C))
                                StepItem(
                                    stepNumber = "1",
                                    title = "Crie uma Conta de Negócio",
                                    description = "Acesse o portal oficial google.com/business e clique em 'Gerenciar agora' usando uma conta Gmail."
                                )
                                StepItem(
                                    stepNumber = "2",
                                    title = "Defina Categoria e Nome",
                                    description = "Insira o nome oficial da empresa e selecione uma categoria principal exata (ex: Cafeteria)."
                                )
                                StepItem(
                                    stepNumber = "3",
                                    title = "Mapeie sua Localização",
                                    description = "Preencha o CEP e endereço correto atendido para indexar suas rotas e exibição no Google Maps."
                                )
                                StepItem(
                                    stepNumber = "4",
                                    title = "Verifique o Perfil",
                                    description = "Realize a autenticação requisitada pelo Google (mídia de vídeo, ligação, ou código postal) para ser ativado publicamente."
                                )
                                
                                Button(
                                    onClick = onNavigateToAcademy,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Class, contentDescription = "Learn")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ver Detalhes na Academia", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // New Profile Dialog Modal (Material 3 standard)
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var website by remember { mutableStateOf("") }
        var hours by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    "Integrar Novo Perfil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome da Empresa") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Categoria Principal (Ex: Cafeteria)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_category_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Endereço Completo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone Comercial") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = hours,
                            onValueChange = { hours = it },
                            label = { Text("Horários (Ex: Seg-Sáb 8h-18h)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Descrição Atual do Negócio (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank()) {
                            onAddProfile(name, category, address, phone, website, hours, desc)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Salvar Perfil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BusinessProfileCard(
    profile: BusinessProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .testTag("business_card_${profile.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (profile.hasCompletenessError) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF3A1F26), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Ajustes Pendentes",
                                    color = Color(0xFFFF5252),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = profile.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryCyan,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Profile health score badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            when {
                                profile.diagnosticScore >= 90 -> Color(0xFF0F3A22)
                                profile.diagnosticScore >= 70 -> Color(0xFF3F3B1A)
                                else -> Color(0xFF3F1B22)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${profile.diagnosticScore}%",
                        color = when {
                            profile.diagnosticScore >= 90 -> SuccessGreen
                            profile.diagnosticScore >= 70 -> WarningAmber
                            else -> Color(0xFFFF5252)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Address description snippet
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = profile.address,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Gerenciar",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stepNumber,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Column {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            Text(
                description,
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
