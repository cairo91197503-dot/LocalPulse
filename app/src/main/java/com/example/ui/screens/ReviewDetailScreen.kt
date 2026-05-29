package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
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
import com.example.data.models.Review
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    review: Review,
    viewModel: BusinessViewModel,
    onBack: () -> Unit
) {
    val aiDraft by viewModel.aiReplyDraft.collectAsState()
    val isGeneratingReply by viewModel.isGeneratingReply.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var userEditedReply by remember { mutableStateOf("") }

    // Sync draft updates to internal editor state
    LaunchedEffect(aiDraft) {
        if (aiDraft.isNotEmpty()) {
            userEditedReply = aiDraft
        }
    }

    // Set initial custom reply if already responded
    LaunchedEffect(review) {
        if (review.isReplied && review.replyText != null) {
            userEditedReply = review.replyText
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Responder Feedback", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("review_detail_container")
        ) {
            if (isOffline) {
                OfflineIndicatorBanner()
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Customer feedback profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = review.authorName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = review.authorName,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("detail_customer_name")
                                )
                                Text(
                                    text = "Interação do Perfil Pessoal",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }

                        // Rating representation
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFB300).copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${review.rating}.0",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier.testTag("detail_customer_comment")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reply section
            Text(
                text = "Sua Resposta",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Response Generation & Editing Block
            if (!review.isReplied) {
                // Not replied yet, show AI recommendation action
                Button(
                    onClick = { viewModel.generateAIReply(review) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_ai_reply_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    )
                ) {
                    if (isGeneratingReply) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerando sugestão...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "IA")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Responder com IA (Gemini)")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Editable response TextField
            OutlinedTextField(
                value = userEditedReply,
                onValueChange = { userEditedReply = it },
                label = { Text("Texto da resposta comercial") },
                placeholder = { Text("Escreva uma resposta profissional ou use o botão do Gemini para gerar uma sugestão inteligente de um toque...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("reply_editor_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submitting Button
            Button(
                onClick = {
                    if (userEditedReply.isNotBlank()) {
                        viewModel.submitFeedback(review.id, userEditedReply)
                    }
                },
                enabled = userEditedReply.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_response_gmb_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (review.isReplied) "Atualizar resposta no GMB" else "Enviar resposta agora",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
