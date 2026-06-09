package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.models.AcademyArticle
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen(
    viewModel: AcademyViewModel,
    onBack: () -> Unit
) {
    val articles by viewModel.articles.collectAsState()
    var selectedArticle by remember { mutableStateOf<AcademyArticle?>(null) }

    val readCount = articles.count { it.isRead }
    val totalCount = articles.size
    val progressPercent = if (totalCount > 0) (readCount * 100) / totalCount else 100

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LocalPulse Academy", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedArticle != null) {
                            selectedArticle = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedArticle,
            transitionSpec = {
                slideInHorizontally { width -> if (targetState != null) width else -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> if (targetState != null) -width else width } + fadeOut()
            },
            label = "academy_scenes"
        ) { article ->
            if (article != null) {
                // Article Detail View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E1F3D), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(article.category, color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Text(article.title, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = TextPrimary, lineHeight = 34.sp)
                    
                    Divider(color = CardBorder)

                    // Simple clean rendering of markdown
                    Text(
                        text = article.contentMarkdown,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!article.isRead) {
                        Button(
                            onClick = {
                                viewModel.markAsRead(article.id)
                                selectedArticle = article.copy(isRead = true)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("mark_as_read_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Lido")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Marcar lição como Concluída", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF102A17), RoundedCornerShape(12.dp))
                                .border(1.dp, SuccessGreen, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Você concluiu esta lição!", color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                // Articles List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Progress Board
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFF22284C), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = "Progress",
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Seu Progresso de Aprendizado",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "$readCount de $totalCount Lições Lidas ($progressPercent%)",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = progressPercent / 100f,
                                        color = PrimaryPurple,
                                        trackColor = CardBorder,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp)
                                            .height(6.dp)
                                            .background(Color.Transparent, RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Guias de Crescimento GMN",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    items(articles) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedArticle = item }
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .testTag("article_card_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF1B233F), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(item.category, color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    if (item.isRead) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Lido", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = item.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Acessar Aula", color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
