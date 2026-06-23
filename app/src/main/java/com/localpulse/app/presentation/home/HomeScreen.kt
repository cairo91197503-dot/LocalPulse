package com.localpulse.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localpulse.app.domain.model.User
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    dailyTasksViewModel: com.localpulse.app.presentation.tasks.DailyTasksViewModel = hiltViewModel(),
    onSignOutClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDiagnosis: () -> Unit,
    onNavigateToQrCode: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tasks by dailyTasksViewModel.tasks.collectAsState()
    val progress by dailyTasksViewModel.progress.collectAsState()
    val isTasksLoading by dailyTasksViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LocalPulse", fontWeight = FontWeight.Bold) },
                actions = {
                    val userPhotoUrl = (uiState as? HomeUiState.Content)?.userPhotoUrl
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userPhotoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        placeholder = painterResource(id = android.R.drawable.ic_menu_myplaces),
                        error = painterResource(id = android.R.drawable.ic_menu_myplaces)
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                    IconButton(onClick = onSignOutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HomeUiState.Content -> {
                    val isRefreshing = uiState is HomeUiState.Loading
                    DashboardContent(
                        user = state.user,
                        reputationScore = state.reputationScore,
                        isRefreshing = isRefreshing,
                        tasks = tasks,
                        progress = progress,
                        isTasksLoading = isTasksLoading,
                        onCompleteTask = dailyTasksViewModel::completeTask,
                        onRefresh = { 
                            viewModel.loadData()
                            dailyTasksViewModel.loadTasks()
                        },
                        onNavigateToDiagnosis = onNavigateToDiagnosis,
                        onNavigateToQrCode = onNavigateToQrCode,
                        onNavigateToTips = onNavigateToTips
                    )
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
                is HomeUiState.LoggedOut -> {
                    // Navegação tratada na MainActivity
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    user: User?, 
    reputationScore: Int,
    isRefreshing: Boolean,
    tasks: List<com.localpulse.app.domain.model.DailyTask>,
    progress: com.localpulse.app.domain.model.UserProgress?,
    isTasksLoading: Boolean,
    onCompleteTask: (String, Int) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToDiagnosis: () -> Unit,
    onNavigateToQrCode: () -> Unit,
    onNavigateToTips: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Boas-vindas Card
                val userName = user?.name ?: "Usuário"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Olá, ${userName}! 👋",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bem-vindo ao seu painel LocalPulse.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                com.localpulse.app.presentation.tasks.DailyTasksCard(
                    tasks = tasks,
                    progress = progress,
                    isLoading = isTasksLoading,
                    onCompleteTask = onCompleteTask
                )
            }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTips() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Dicas e Tutoriais",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Reveja apresentações e aprenda mais",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        item {
            // Reputação Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Score de Reputação",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reputationScore.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Analisando seu perfil...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDiagnosis() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Diagnóstico com IA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Analise sua reputação online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToQrCode() },
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "QR Code de Avaliações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Gere e compartilhe seu QR Code",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }

        item {
            // Avaliações Recentes Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Avaliações Recentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma avaliação encontrada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            // Ações Recomendadas Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ações Recomendadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma ação no momento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    }
}
