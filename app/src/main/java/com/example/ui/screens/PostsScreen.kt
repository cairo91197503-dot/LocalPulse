package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Post
import com.example.data.repository.PostIdea
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(viewModel: BusinessViewModel) {
    val posts by viewModel.allPosts.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val suggestions by viewModel.postIdeas.collectAsState()
    val isGenerating by viewModel.isGeneratingIdeas.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isSimulationModeActive by viewModel.isSimulationModeActive.collectAsState()

    // Collect social media connection states
    val isFacebookConnected by viewModel.isFacebookConnected.collectAsState()
    val isInstagramConnected by viewModel.isInstagramConnected.collectAsState()
    val isWhatsAppConnected by viewModel.isWhatsAppConnected.collectAsState()
    val isTikTokConnected by viewModel.isTikTokConnected.collectAsState()

    val userPlan by viewModel.userPlan.collectAsState()
    val isAutopilotActive by viewModel.isAutopilotActive.collectAsState()

    var showEditor by remember { mutableStateOf(false) }
    var selectedIdeaForEditor by remember { mutableStateOf<PostIdea?>(null) }
    
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }
    
    var isScheduledPost by remember { mutableStateOf(false) }
    var scheduledDateText by remember { mutableStateOf("2026-06-05 15:00") }

    // Synchronize to the editor when an idea is selected
    LaunchedEffect(selectedIdeaForEditor) {
        val idea = selectedIdeaForEditor
        if (idea != null) {
            editorTitle = idea.title
            editorContent = idea.content
            // Reset scheduling when opening different suggestions
            isScheduledPost = false
            showEditor = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("posts_screen_container")
    ) {
        if (isOffline) {
            Spacer(modifier = Modifier.height(16.dp))
            OfflineIndicatorBanner()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen title and Custom create Action button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Postagens Multiplataforma",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Button(
                onClick = {
                    selectedIdeaForEditor = null
                    editorTitle = ""
                    editorContent = ""
                    showEditor = true
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("create_new_post_btn")
            ) {
                Icon(imageVector = Icons.Default.PostAdd, contentDescription = "Novo Post")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Novo Post")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Status de Atividade",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Seu Perfil Conectado",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Último post ativo: ${profile?.lastPostDate ?: "Sem postagens recentes"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("last_post_date_profile")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EXPERT+ ONLY: O App Faz Tudo / Piloto Automático Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isAutopilotActive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                                 else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isAutopilotActive) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isAutopilotActive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Piloto Automático",
                            tint = if (isAutopilotActive) MaterialTheme.colorScheme.secondary else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Piloto Automático (O App Faz Tudo)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isAutopilotActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
                            )
                            if (userPlan != "EXPERT_PLUS") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.11f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "EXPERT+",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isAutopilotActive) "🤖 Modo automático ativo! A IA está planejando, agendando e postando mídias sociais de forma inteligente por você."
                                   else "Deixe que a Inteligência Artificial gerencie todo o agendamento e engajamento das suas contas.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Switch(
                    checked = isAutopilotActive,
                    onCheckedChange = { active ->
                        viewModel.setAutopilotActive(active)
                    },
                    modifier = Modifier.testTag("autopilot_pilot_mode_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Suggestions header with Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "IA",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sugestões Inteligentes (Gemini)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            IconButton(
                onClick = { viewModel.loadAISecondIdeas() },
                modifier = Modifier.testTag("refresh_ai_suggestions_btn")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recarregar",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal suggested ideas layout
        if (suggestions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                suggestions.take(3).forEachIndexed { idx, idea ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedIdeaForEditor = idea }
                            .testTag("ai_suggestion_card_$idx"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = idea.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = idea.content,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Toque para postar",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        } else if (!isSimulationModeActive) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Como funcionam as sugestões com IA?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "O recomendador de conteúdo do LocalPulse utiliza inteligência artificial (Gemini) para ler os insights de sentimentos da sua empresa e planejar postagens integrando imagens sugeridas, hashtags prontas e chamadas diretas de venda.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🔑 Para obter sugestões personalizadas do seu negócio real, primeiro sincronize avaliações verdadeiras de clientes ativando suas integrações de rede.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History Posts listing
        Text(
            text = "Histórico de Postagens",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (posts.isEmpty()) {
            if (!isSimulationModeActive) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "💡 O que esta tela faz?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "A ferramenta de Postagens Multiplataforma permite redigir, planejar e publicar de forma simultânea materiais de marketing, ofertas de produtos e comunicados urgentes diretamente no seu Google Meu Negócio, Facebook, Instagram e TikTok com 1 único toque.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🔑 O que é necessário para obter dados de postagem ativa?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = "• Perfil Comercial no Instagram:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Sua conta do Instagram deve ser cadastrada como Comercial ou de Criador, e estar devidamente vinculada a uma Página do Facebook.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "• Escopos de Escrita aprovados pela Meta:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Seus tokens empresariais devem conter permissões ativas de publicação: 'pages_manage_posts' (para Facebook) e 'instagram_content_publish' (para fotos/vídeos no Instagram).",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.setSimulationMode(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("activate_simulated_posts_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver dados simulados para teste (Demo)")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ainda não há postagens criadas pelo aplicativo.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_item_${post.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = post.createTime,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                )
                            }
                            if (post.scheduledTime != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Agendado",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Agendado para: ${post.scheduledTime}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Post draft editor Dialog (simulates direct publish in 1-tap)
    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (editorTitle.isNotBlank() && editorContent.isNotBlank()) {
                            viewModel.submitPost(
                                title = editorTitle,
                                content = editorContent,
                                scheduledTime = if (isScheduledPost) scheduledDateText else null
                            )
                            showEditor = false
                        }
                    },
                    modifier = Modifier.testTag("publish_post_confirm_btn")
                ) {
                    Text(
                        text = if (isScheduledPost) "Agendar Publicação" else if (isFacebookConnected || isInstagramConnected || isTikTokConnected) "Publicar Multiplataforma" else "Publicar agora no Perfil"
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditor = false },
                    modifier = Modifier.testTag("publish_post_cancel_btn")
                ) {
                    Text("Cancelar")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = "Postar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Revisar Publicação", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "A postagem será publicada diretamente nos seus perfis sociais selecionados de forma simultânea.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = editorTitle,
                        onValueChange = { editorTitle = it },
                        label = { Text("Título da Postagem / Chamada") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("editor_title_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editorContent,
                        onValueChange = { editorContent = it },
                        label = { Text("Corpo do post / Texto promocional") },
                        placeholder = { Text("Descreva sua oferta...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("editor_content_input"),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 6
                    )

                    // Scheduling Section (Agendar posts - Premium Expert+ exclusive)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isScheduledPost) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) else Color.Transparent)
                            .clickable {
                                if (userPlan == "EXPERT_PLUS") {
                                    isScheduledPost = !isScheduledPost
                                } else {
                                    viewModel.showPremiumUpgrade()
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Agendamento",
                                tint = if (isScheduledPost) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Agendar Publicação",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (userPlan != "EXPERT_PLUS") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Bloqueado",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Escolha um dia e horário futuros para postagem",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Switch(
                            checked = isScheduledPost,
                            onCheckedChange = { checked ->
                                if (userPlan == "EXPERT_PLUS") {
                                    isScheduledPost = checked
                                } else {
                                    viewModel.showPremiumUpgrade()
                                }
                            },
                            modifier = Modifier.testTag("scheduler_post_switch")
                        )
                    }

                    if (isScheduledPost && userPlan == "EXPERT_PLUS") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = scheduledDateText,
                            onValueChange = { scheduledDateText = it },
                            label = { Text("Data/Hora do Agendamento") },
                            placeholder = { Text("Ex: AAAA-MM-DD HH:MM") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Schedule, contentDescription = "Data") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scheduler_date_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Publicar simultaneamente em:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    var postToFacebook by remember { mutableStateOf(isFacebookConnected) }
                    var postToInstagram by remember { mutableStateOf(isInstagramConnected) }

                    var postToTikTok by remember { mutableStateOf(isTikTokConnected) }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Linha 1: Google e TikTok
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. Google connection (always active)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(2.dp)
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = { },
                                    enabled = false
                                )
                                Text(
                                    text = "Google",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1
                                )
                            }

                            // 2. TikTok
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (postToTikTok) Color(0xFF010101).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                                    .clickable(enabled = isTikTokConnected) { postToTikTok = !postToTikTok }
                                    .padding(2.dp)
                            ) {
                                Checkbox(
                                    checked = postToTikTok,
                                    onCheckedChange = { postToTikTok = it },
                                    enabled = isTikTokConnected,
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF010101))
                                )
                                Text(
                                    text = "TikTok",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTikTokConnected) Color(0xFF010101) else Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }

                        // Linha 2: Facebook e Instagram
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 3. Facebook
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (postToFacebook) Color(0xFF1877F2).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                                    .clickable(enabled = isFacebookConnected) { postToFacebook = !postToFacebook }
                                    .padding(2.dp)
                            ) {
                                Checkbox(
                                    checked = postToFacebook,
                                    onCheckedChange = { postToFacebook = it },
                                    enabled = isFacebookConnected,
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1877F2))
                                )
                                Text(
                                    text = "Facebook",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFacebookConnected) Color(0xFF1877F2) else Color.Gray,
                                    maxLines = 1
                                )
                            }

                            // 4. Instagram
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (postToInstagram) Color(0xFFE1306C).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                                    .clickable(enabled = isInstagramConnected) { postToInstagram = !postToInstagram }
                                    .padding(2.dp)
                            ) {
                                Checkbox(
                                    checked = postToInstagram,
                                    onCheckedChange = { postToInstagram = it },
                                    enabled = isInstagramConnected,
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE1306C))
                                )
                                Text(
                                    text = "Instagram",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInstagramConnected) Color(0xFFE1306C) else Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    if (!isFacebookConnected || !isInstagramConnected || !isTikTokConnected) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 Vá em Configurações para integrar o TikTok, Facebook ou Instagram e ativar a postagem simultânea.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            lineHeight = 13.sp
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
