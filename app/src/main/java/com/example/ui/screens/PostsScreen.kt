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

    var showEditor by remember { mutableStateOf(false) }
    var selectedIdeaForEditor by remember { mutableStateOf<PostIdea?>(null) }
    
    var editorTitle by remember { mutableStateOf("") }
    var editorContent by remember { mutableStateOf("") }

    // Synchronize to the editor when an idea is selected
    LaunchedEffect(selectedIdeaForEditor) {
        val idea = selectedIdeaForEditor
        if (idea != null) {
            editorTitle = idea.title
            editorContent = idea.content
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
                text = "Postagens GMB",
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
                        text = "Seu Perfil no Google Meu Negócio",
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
                            viewModel.submitPost(editorTitle, editorContent)
                            showEditor = false
                        }
                    },
                    modifier = Modifier.testTag("publish_post_confirm_btn")
                ) {
                    Text("Publicar agora no Google")
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
                    Text("Revisar Publicação GMB", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "A publicação será enviada para a API do Google Meu Negócio e aparecerá no perfil da sua loja virtual instantaneamente.",
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
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
