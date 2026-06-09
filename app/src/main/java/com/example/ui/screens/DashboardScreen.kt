package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.viewmodel.BusinessViewModel
import com.example.ui.viewmodel.UiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BusinessViewModel,
    profile: BusinessProfile,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Diagnóstico, 1: Avaliações, 2: Posts

    val reviews by viewModel.reviews.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()
    val diagnosticState by viewModel.diagnosticState.collectAsState()

    // Run auto-diagnostic on start if status is idle to give premium immediate UX
    LaunchedEffect(profile.id) {
        if (diagnosticState is UiState.Idle) {
            viewModel.runDiagnostic(profile)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = profile.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.runDiagnostic(profile) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recarregar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Material 3 Custom Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBg,
                contentColor = PrimaryPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryPurple
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Diagnóstico", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(imageVector = Icons.Default.Assessment, contentDescription = null) },
                    selectedContentColor = PrimaryPurple,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Avaliações", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = null) },
                    selectedContentColor = PrimaryPurple,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Criar Posts", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(imageVector = Icons.Default.Campaign, contentDescription = null) },
                    selectedContentColor = PrimaryPurple,
                    unselectedContentColor = TextSecondary
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> DiagnosticTab(
                        profile = profile,
                        diagnosticState = diagnosticState,
                        onReRun = { viewModel.runDiagnostic(profile) }
                    )
                    1 -> ReviewsTab(
                        viewModel = viewModel,
                        profile = profile,
                        reviews = reviews
                    )
                    2 -> PostsTab(
                        viewModel = viewModel,
                        savedPosts = savedPosts
                    )
                }
            }
        }
    }
}

// ==================== DIAGNOSTIC TAB ====================

@Composable
fun DiagnosticTab(
    profile: BusinessProfile,
    diagnosticState: UiState<DiagnosticResult>,
    onReRun: () -> Unit
) {
    var showDescSheet by remember { mutableStateOf(false) }
    var currentOptimizedDesc by remember { mutableStateOf("") }
    val context = LocalContext.current

    when (diagnosticState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                    Text("Gemini auditando seu perfil do Google...", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                    Text("Erro ao conectar ao Gemini: ${diagnosticState.message}", color = TextSecondary, textAlign = TextAlign.Center)
                    Button(onClick = onReRun, colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
                        Text("Tentar Novamente")
                    }
                }
            }
        }
        is UiState.Success -> {
            val data = diagnosticState.data
            currentOptimizedDesc = data.optimizedDescription

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Score Gauge Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Circular score container
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFF20253F), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${data.score}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryPurple
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Otimização do Perfil",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = when {
                                        data.score >= 90 -> "Perfil muito excelente! Excelente ranqueamento local."
                                        data.score >= 70 -> "Boa configuração, mas você está perdendo clientes devido a detalhes cruciais."
                                        else -> "Alerta: Perfil mal otimizado. Risco de invisibilidade nos resultados de busca."
                                    },
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // AI Optimized description call to action
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16252C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1E353F), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { showDescSheet = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF1F3D4A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PrimaryCyan
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Descrição Otimizada por IA Pronta!",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Toque para visualizar e copiar a descrição de conversão local.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                        }
                    }
                }

                // Correct items ✅
                if (data.correctList.isNotEmpty()) {
                    item {
                        Text("O Que Está Correto ✅", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 16.sp)
                    }
                    items(data.correctList) { itemText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF101B17), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF123F1B), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Text(itemText, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }

                // Problem items ⚠️
                if (data.warningList.isNotEmpty()) {
                    item {
                        Text("O Que Está Faltando ou Errado ⚠️", fontWeight = FontWeight.Bold, color = WarningAmber, fontSize = 16.sp)
                    }
                    items(data.warningList) { itemText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF221F11), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF4C3F1B), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                            Text(itemText, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }

                // Actionable priority improvements suggestions
                if (data.improvementSuggestions.isNotEmpty()) {
                    item {
                        Text("Plano de Ação Sugerido por IA", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    }
                    items(data.improvementSuggestions) { suggest ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(suggest.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when (suggest.priority) {
                                                    "Alta" -> Color(0xFF3F191D)
                                                    "Média" -> Color(0xFF3B2F11)
                                                    else -> Color(0xFF182E3F)
                                                },
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            suggest.priority,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (suggest.priority) {
                                                "Alta" -> Color(0xFFFF5252)
                                                "Média" -> WarningAmber
                                                else -> PrimaryCyan
                                            }
                                        )
                                    }
                                }
                                Text(
                                    suggest.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 8.dp),
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { 
                                        Toast.makeText(context, "Ação '${suggest.title}' iniciada!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (suggest.priority == "Alta") PrimaryPurple else Color(0xFF2E354F)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(suggest.actionLabel, fontSize = 12.sp)
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
        else -> {}
    }

    // AI Description Viewer Dialog Modal
    if (showDescSheet) {
        AlertDialog(
            onDismissRequest = { showDescSheet = false },
            title = { Text("Descrição Otimizada (SEO Local)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Ricas palavras-chave incorporadas de forma humana para subir nos resultados do ranking dos mapas eletrónicos.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F121F), RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = currentOptimizedDesc,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDescSheet = false
                        Toast.makeText(context, "Copiado com sucesso para a área de transferência!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Descrição")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDescSheet = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

// ==================== REVIEWS TAB ====================

@Composable
fun ReviewsTab(
    viewModel: BusinessViewModel,
    profile: BusinessProfile,
    reviews: List<Review>
) {
    val reviewReplyState by viewModel.reviewReplyState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Review Stats Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${profile.rating}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Estrela", tint = WarningAmber, modifier = Modifier.size(24.dp))
                        }
                        Text("Nota do Google", fontSize = 12.sp, color = TextSecondary)
                    }
                    Divider(modifier = Modifier.height(48.dp).width(1.dp), color = CardBorder)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${profile.reviewsAnsweredPercent}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text("Resp. Efetuadas", fontSize = 12.sp, color = TextSecondary)
                    }
                    Divider(modifier = Modifier.height(48.dp).width(1.dp), color = CardBorder)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${profile.reviewsCount}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        Text("Total Recebidas", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        item {
            Text("Avaliações de Clientes", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
        }

        if (reviews.isNotEmpty()) {
            items(reviews) { review ->
                var customReplyText by remember { mutableStateOf("") }
                var isEditingReply by remember { mutableStateOf(false) }

                val currentReplyUiState = reviewReplyState[review.id] ?: UiState.Idle

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(review.authorName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            // Star Rating Row
                            Row {
                                repeat(5) { ind ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (ind < review.rating) WarningAmber else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Text(review.text, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)

                        // If response already exists in DB
                        if (!review.responseText.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF13172E), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Resposta Ativa no Google:", color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(review.responseText, color = TextPrimary, fontSize = 12.sp)
                                }
                            }
                        } else {
                            // Responses generator section with Gemini
                            when (currentReplyUiState) {
                                is UiState.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = PrimaryCyan, modifier = Modifier.size(24.dp))
                                    }
                                }
                                is UiState.Error -> {
                                    Text("Erro ao gerar resposta: ${currentReplyUiState.message}", color = Color.Red, fontSize = 12.sp)
                                }
                                is UiState.Success -> {
                                    val suggestedText = currentReplyUiState.data
                                    if (!isEditingReply) {
                                        customReplyText = suggestedText
                                        isEditingReply = true
                                    }
                                    
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Sugestão de Resposta do Gemini (Edite se quiser):", color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        OutlinedTextField(
                                            value = customReplyText,
                                            onValueChange = { customReplyText = it },
                                            minLines = 3,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = PrimaryPurple,
                                                unfocusedBorderColor = CardBorder
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            TextButton(onClick = { isEditingReply = false }) {
                                                Text("Cancelar", color = TextSecondary)
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.applyReviewResponseText(review, customReplyText)
                                                    isEditingReply = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                            ) {
                                                Text("Enviar Resposta")
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    // Default idle response action button
                                    Button(
                                        onClick = { viewModel.requestReviewReply(profile.name, review) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E284B)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.align(Alignment.End).testTag("ai_reply_btn_${review.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gerar Resposta IA", color = Color.White, fontSize = 12.sp)
                                    }
                                }
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

// ==================== POSTS TAB ====================

@Composable
fun PostsTab(
    viewModel: BusinessViewModel,
    savedPosts: List<PostSuggestion>
) {
    var themeFocus by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Oferta") }
    val postGenerationState by viewModel.postGenerationState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Input widget to construct post parameters
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "Criar Postagem com Gemini",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Category Selector
                    Column {
                        Text("Tipo de Postagem", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Oferta", "Novidade", "Evento").forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selectedCategory == cat) PrimaryPurple else Color(0xFF1E2034),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (selectedCategory == cat) PrimaryPurple else CardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedCategory = cat }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        cat,
                                        color = if (selectedCategory == cat) Color.White else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Theme text instruction
                    Column {
                        Text("Qual o foco do post?", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = themeFocus,
                            onValueChange = { themeFocus = it },
                            placeholder = { Text("Ex: Inauguração do wi-fi, brunch aos domingos ou 10% de desconto no pão de queijo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("post_theme_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (themeFocus.isNotBlank()) {
                                viewModel.generatePostSuggestion(selectedCategory, themeFocus)
                            } else {
                                Toast.makeText(context, "Digite um tema para guiar a IA!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_post_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escrever com IA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Display results of generated post
        item {
            AnimatedContent(targetState = postGenerationState, label = "post_gen_state_anim") { state ->
                when (state) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryPurple)
                        }
                    }
                    is UiState.Success -> {
                        val post = state.data
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF13172E)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PrimaryPurple, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Rascunho Inteligente", fontWeight = FontWeight.Bold, color = PrimaryCyan, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(PrimaryPurple, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(post.category, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(post.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
                                Text(post.content, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                                
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.toggleSavePost(post)
                                            Toast.makeText(context, "Postagem guardada!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Salvar Post")
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text("Erro: ${state.message}", color = Color.Red, fontSize = 13.sp, textAlign = TextAlign.Center)
                    }
                    else -> {}
                }
            }
        }

        item {
            Text("Suas Postagens Criadas", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
        }

        // Saved drafts list
        if (savedPosts.isNotEmpty()) {
            items(savedPosts) { pst ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (pst.isSaved) PrimaryCyan else CardBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF263238), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(pst.category, color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            IconButton(onClick = { viewModel.deletePost(pst.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }

                        Text(pst.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(pst.content, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Texto copiado para postagem!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E354F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copiar Texto")
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    "Você não tem nenhuma postagem salva ainda. Digite um tema acima para gerar um!",
                    color = TextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
