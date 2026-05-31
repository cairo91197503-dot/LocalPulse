package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.BusinessViewModel
import com.example.ui.viewmodel.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    viewModel: BusinessViewModel,
    onNavigateBack: () -> Unit
) {
    val posts by viewModel.postsList.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var postText by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("Hoje, 20:00") }

    // Platform checklist states for Scheduling
    var checkYouTube by remember { mutableStateOf(true) }
    var checkFacebook by remember { mutableStateOf(false) }
    var checkInstagram by remember { mutableStateOf(false) }
    var checkWhatsApp by remember { mutableStateOf(false) }
    var checkTikTok by remember { mutableStateOf(false) }

    // Validation or limits computed state based on tier limits
    val checkedCount = listOf(checkYouTube, checkFacebook, checkInstagram, checkWhatsApp, checkTikTok).count { it }
    val isPlanRestricted = userPlan == "FREE" && checkedCount > 2

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronograma de Posts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("posts_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = Modifier.testTag("posts_screen_container")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (isOffline) {
                item {
                    OfflineIndicatorBanner()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Quick Create block
            item {
                Text(
                    text = "Criar Nova Publicação",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("add_post_form_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            label = { Text("O que você deseja publicar?") },
                            placeholder = { Text("Escreva a legenda ou roteiro de sua mídia aqui...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("post_text_input_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = selectedTime,
                            onValueChange = { selectedTime = it },
                            label = { Text("Data & Hora de Veiculação") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("post_time_input_field")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected target platforms
                        Text(
                            text = "Selecione as Redes Sociais:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlatformCheckbox(label = "Google", checked = checkYouTube, onCheckedChange = { checkYouTube = it })
                            PlatformCheckbox(label = "FB", checked = checkFacebook, onCheckedChange = { checkFacebook = it })
                            PlatformCheckbox(label = "IG", checked = checkInstagram, onCheckedChange = { checkInstagram = it })
                            PlatformCheckbox(label = "WA", checked = checkWhatsApp, onCheckedChange = { checkWhatsApp = it })
                            PlatformCheckbox(label = "TikTok", checked = checkTikTok, onCheckedChange = { checkTikTok = it })
                        }

                        // Plan usage warning rules
                        if (isPlanRestricted) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "O plano Gratuito limita em 2 canais simultâneos. Mude para o plano PRO ou Expert+ nas configurações para liberar todos!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (postText.isNotBlank() && !isPlanRestricted) {
                                    val platformsList = mutableListOf<String>()
                                    if (checkYouTube) platformsList.add("YouTube")
                                    if (checkFacebook) platformsList.add("Facebook")
                                    if (checkInstagram) platformsList.add("Instagram")
                                    if (checkWhatsApp) platformsList.add("WhatsApp")
                                    if (checkTikTok) platformsList.add("TikTok")

                                    viewModel.addPost(postText, selectedTime, platformsList)

                                    // Clear draft state
                                    postText = ""
                                }
                            },
                            enabled = postText.isNotBlank() && !isPlanRestricted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("schedule_post_submit_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Agendar Publicação Integrada", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // List Title
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Postagens Programadas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Lazy Items block mapping posts
            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum agendamento programado.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                items(posts) { post ->
                    ScheduledPostRow(post)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun PlatformCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ScheduledPostRow(post: PostItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scheduled_post_row_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.text,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.scheduledTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (post.status == "Publicado") Color(0xFF25D366).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = post.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (post.status == "Publicado") Color(0xFF25D366) else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Channels list for this post
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Publicidade em: ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                post.platforms.forEachIndexed { index, platform ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = platform,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
