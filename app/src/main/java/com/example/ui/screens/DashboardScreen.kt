package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PulseViewModel
import com.example.ui.viewmodel.PulseRecord
import com.example.ui.viewmodel.HealthProfile
import com.example.ui.viewmodel.ScanStage
import kotlinx.coroutines.delay
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PulseViewModel,
    onShowTutorial: () -> Unit
) {
    val records by viewModel.records.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val scanStage by viewModel.scanStage.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val heartRateSim by viewModel.currentHeartRateSim.collectAsState()
    val spo2Sim by viewModel.currentSpO2Sim.collectAsState()
    val scanMessage by viewModel.scanMessage.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()

    var showManualAddDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Screen dimensions to support Adaptive layouts
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Pulse Tracking Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PulsePersonal",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onShowTutorial,
                        modifier = Modifier.testTag("tutorial_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Show Tutorial",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("profile_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManualAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("manual_entry_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Log pulse manually")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        
        // Responsive Adaptive Grid Split
        if (isTablet) {
            // Horizontal split
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Left Column: Interactive Scanner & Profiles
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .padding(end = 12.dp)
                ) {
                    ScannerSection(
                        scanStage = scanStage,
                        scanProgress = scanProgress,
                        heartRateSim = heartRateSim,
                        spo2Sim = spo2Sim,
                        scanMessage = scanMessage,
                        onScanStart = { viewModel.startFingerScan() },
                        onScanCancel = { viewModel.cancelScan() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileMetricsSummaryView(
                        profile = profile,
                        avgHR = viewModel.getAverageHeartRate(),
                        avgSpO2 = viewModel.getAverageSpO2(),
                        onEdit = { showProfileDialog = true }
                    )
                }

                // Right Column: Filtered List of Cardio History logs
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .padding(start = 12.dp)
                ) {
                    HistoryLogsSection(
                        records = records,
                        activeFilter = filter,
                        onFilterChanged = { viewModel.setFilter(it) },
                        onDelete = { viewModel.deleteRecord(it) }
                    )
                }
            }
        } else {
            // Vertical Stack for standard mobiles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ScannerSection(
                            scanStage = scanStage,
                            scanProgress = scanProgress,
                            heartRateSim = heartRateSim,
                            spo2Sim = spo2Sim,
                            scanMessage = scanMessage,
                            onScanStart = { viewModel.startFingerScan() },
                            onScanCancel = { viewModel.cancelScan() }
                        )
                    }

                    item {
                        ProfileMetricsSummaryView(
                            profile = profile,
                            avgHR = viewModel.getAverageHeartRate(),
                            avgSpO2 = viewModel.getAverageSpO2(),
                            onEdit = { showProfileDialog = true }
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        ) {
                            HistoryLogsSection(
                                records = records,
                                activeFilter = filter,
                                onFilterChanged = { viewModel.setFilter(it) },
                                onDelete = { viewModel.deleteRecord(it) }
                            )
                        }
                    }
                    
                    // Extra spacer for floating actions
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Modal: User Metrics settings edit
    if (showProfileDialog) {
        ProfileEditDialog(
            currentProfile = profile,
            onDismiss = { showProfileDialog = false },
            onSave = { w, h, s, d, m ->
                viewModel.updateProfile(w, h, s, d, m)
                showProfileDialog = false
            }
        )
    }

    // Modal: Manual Track vital inputs
    if (showManualAddDialog) {
        ManualLoggingDialog(
            onDismiss = { showManualAddDialog = false },
            onSave = { bpm, spo2, cat, note ->
                viewModel.logManualEntry(bpm, spo2, cat, note)
                showManualAddDialog = false
            }
        )
    }
}

// Interactive photoplethysmogram Scanner Circle + Wave Visualizer
@Composable
fun ScannerSection(
    scanStage: ScanStage,
    scanProgress: Float,
    heartRateSim: Int,
    spo2Sim: Int,
    scanMessage: String,
    onScanStart: () -> Unit,
    onScanCancel: () -> Unit
) {
    // Infinite transition for Pulsing glowing rings around scanner core
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_glow_animation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pulse_scanner_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CARDIAC PPG SCANNER",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Hold finger on core button to measure",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Interactive Scan Ring Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                try {
                                    onScanStart()
                                    // Sustain wait until finger lift releases gesture
                                    awaitRelease()
                                    onScanCancel()
                                } catch (e: Exception) {
                                    onScanCancel()
                                }
                            }
                        )
                    }
                    .testTag("scan_fingerprint_button")
            ) {
                // Radiant animated ring backgrounds
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = if (scanStage == ScanStage.MEASURING) breatheScale * 1.05f else 1.0f
                            scaleY = if (scanStage == ScanStage.MEASURING) breatheScale * 1.05f else 1.0f
                        }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Scan Progress Outer Circular Slider Indicator
                val progressAnimated by animateFloatAsState(
                    targetValue = scanProgress,
                    animationSpec = tween(durationMillis = 200, easing = LinearEasing),
                    label = "scan_progress_indicator"
                )
                Canvas(modifier = Modifier.size(150.dp)) {
                    drawArc(
                        color = Color.DarkGray.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx())
                    )
                    drawArc(
                        color = if (scanStage == ScanStage.MEASURING) Color(0xFFE57373) else Color(0xFF4DB6AC),
                        startAngle = -90f,
                        sweepAngle = progressAnimated * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }

                // Middle scanning core button circle
                val coreBg = when (scanStage) {
                    ScanStage.PREPARING -> Color(0xFFFFA726)
                    ScanStage.MEASURING -> Color(0xFFEF5350)
                    ScanStage.SUCCESS -> Color(0xFF66BB6A)
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(116.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .background(coreBg, shape = CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (scanStage == ScanStage.SUCCESS) Icons.Default.Check else Icons.Default.Fingerprint,
                            contentDescription = "Scan target",
                            tint = if (scanStage == ScanStage.IDLE) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                        if (scanStage == ScanStage.MEASURING) {
                            Text(
                                text = "$heartRateSim",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "BPM",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        } else {
                            Text(
                                text = if (scanStage == ScanStage.SUCCESS) "Done" else "Hold Me",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (scanStage == ScanStage.IDLE) MaterialTheme.colorScheme.onPrimaryContainer else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pulse wave line simulator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                if (scanStage == ScanStage.MEASURING) {
                    LivePpgWaveCanvas(heartRate = heartRateSim)
                } else {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Rhythm",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Finger sensor holding wave idle",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guidance message and state feedback
            Text(
                text = scanMessage,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = when (scanStage) {
                    ScanStage.PREPARING -> Color(0xFFE65100)
                    ScanStage.MEASURING -> MaterialTheme.colorScheme.primary
                    ScanStage.SUCCESS -> Color(0xFF2E7D32)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.testTag("scan_message_label")
            )

            if (scanStage == ScanStage.MEASURING) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = scanProgress,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

// Canvas which draws an animating PPG Blood Pulse Wave
@Composable
fun LivePpgWaveCanvas(heartRate: Int) {
    val transition = rememberInfiniteTransition(label = "ppg_wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val wavePath = Path()

        val points = 50
        val segmentWidth = width / points
        
        // Use a combined formulation of sine/cosine values to replicate a cardiac systolic peak and dicrotic notch
        for (i in 0..points) {
            val x = i * segmentWidth
            val normalizedX = (i.toFloat() / points) * 4f * Math.PI.toFloat()
            
            // Equation generating standard PPG cardiovascular twin bump shape
            val waveYOne = sin(normalizedX - phase)
            val waveYTwo = 0.4f * sin(2f * (normalizedX - phase) + 1.2f)
            val combinedSinVal = waveYOne + waveYTwo
            
            // Center wave vertically on canvas
            val y = (height / 2) + combinedSinVal * (height * 0.3f)
            
            if (i == 0) {
                wavePath.moveTo(x, y)
            } else {
                wavePath.lineTo(x, y)
            }
        }

        drawPath(
            path = wavePath,
            color = Color(0xFFE57373),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

// User Metrics and health profile dashboard overview
@Composable
fun ProfileMetricsSummaryView(
    profile: HealthProfile,
    avgHR: Int,
    avgSpO2: Int,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERSONAL VITALS PROFILE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Metrics",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body parameters Grid Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weightLabel = if (profile.isMetric) "${profile.weightKg} kg" else "${String.format("%.1f", profile.weightKg * 2.20462f)} lbs"
                val heightLabel = if (profile.isMetric) "${profile.heightCm} cm" else "${String.format("%.1f", profile.heightCm * 0.393701f)} in"

                MetricValueCard(
                    title = "Weight",
                    value = weightLabel,
                    icon = Icons.Default.LineWeight,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                MetricValueCard(
                    title = "Height",
                    value = heightLabel,
                    icon = Icons.Default.Height,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricValueCard(
                    title = "Blood Pressure",
                    value = "${profile.systolicBp}/${profile.diastolicBp} mmHg",
                    icon = Icons.Default.Speed,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                val sys = profile.systolicBp
                val condition = when {
                    sys < 120 -> "Optimal"
                    sys in 120..129 -> "Normal"
                    sys in 130..139 -> "High Normal"
                    else -> "Elevated"
                }
                MetricValueCard(
                    title = "BP Condition",
                    value = condition,
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // Average statistical aggregates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "AVERAGE BPM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (avgHR > 0) "$avgHR" else "--",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "AVERAGE SPO₂", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (avgSpO2 > 0) "$avgSpO2%" else "--",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

// Single metric styling block
@Composable
fun MetricValueCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

// Cardiovascular history list panel
@Composable
fun HistoryLogsSection(
    records: List<PulseRecord>,
    activeFilter: String,
    onFilterChanged: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .testTag("history_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "HEART LOG HISTORY",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Filtering Row pills
            val options = listOf("All", "Resting", "Active", "Normal")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { opt ->
                    FilterChip(
                        selected = activeFilter == opt,
                        onClick = { onFilterChanged(opt) },
                        label = { Text(text = opt) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter calculation list
            val filteredRecords = if (activeFilter == "All") records else records.filter { it.category.equals(activeFilter, ignoreCase = true) }

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Empty list",
                            tint = Color.Gray,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No recorded readings match filter",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRecords, key = { it.id }) { rec ->
                        PulseRecordRow(record = rec, onDelete = { onDelete(rec.id) })
                    }
                }
            }
        }
    }
}

// Single log list row formatting
@Composable
fun PulseRecordRow(
    record: PulseRecord,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Heart Circle color based on rate severity
            val circleColor = when {
                record.heartRateBpm > 100 -> Color(0xFFEF5350) // High active (red)
                record.heartRateBpm < 60 -> Color(0xFF42A5F5)  // Slow resting (blue)
                else -> Color(0xFF66BB6A)                      // Optimal normal (green)
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(circleColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = circleColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${record.heartRateBpm}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = " BPM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• SpO₂ ${record.spo2Percentage}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${record.category}  |  ${record.note}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = record.dateString,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.Gray
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete log entry",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// User Profile metrics dynamic dialog edit form
@Composable
fun ProfileEditDialog(
    currentProfile: HealthProfile,
    onDismiss: () -> Unit,
    onSave: (Float, Float, Int, Int, Boolean) -> Unit
) {
    var isMetric by remember { mutableStateOf(currentProfile.isMetric) }
    var weightInput by remember { mutableStateOf(currentProfile.weightKg.toString()) }
    var heightInput by remember { mutableStateOf(currentProfile.heightCm.toString()) }
    var systolicInput by remember { mutableStateOf(currentProfile.systolicBp.toString()) }
    var diastolicInput by remember { mutableStateOf(currentProfile.diastolicBp.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toFloatOrNull() ?: currentProfile.weightKg
                    val h = heightInput.toFloatOrNull() ?: currentProfile.heightCm
                    val s = systolicInput.toIntOrNull() ?: currentProfile.systolicBp
                    val d = diastolicInput.toIntOrNull() ?: currentProfile.diastolicBp
                    onSave(w, h, s, d, isMetric)
                },
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "Edit Vitals Profile", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Metric Imperial selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Measurement System", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = isMetric,
                            onClick = { isMetric = true },
                            label = { Text("Metric (kg/cm)") }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = !isMetric,
                            onClick = { isMetric = false },
                            label = { Text("Imperial (lb/in)") }
                        )
                    }
                }

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text(if (isMetric) "Weight (kg)" else "Weight (lbs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text(if (isMetric) "Height (cm)" else "Height (inches)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = systolicInput,
                        onValueChange = { systolicInput = it },
                        label = { Text("Systolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    )

                    OutlinedTextField(
                        value = diastolicInput,
                        onValueChange = { diastolicInput = it },
                        label = { Text("Diastolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp)
                    )
                }
            }
        }
    )
}

// Manual health vitals manual entry form modal
@Composable
fun ManualLoggingDialog(
    onDismiss: () -> Unit,
    onSave: (Int, Int, String, String) -> Unit
) {
    var bpmInput by remember { mutableStateOf("") }
    var spo2Input by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Normal") }
    var noteInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val bpm = bpmInput.toIntOrNull() ?: 70
                    val spo2 = spo2Input.toIntOrNull() ?: 98
                    onSave(bpm, spo2, selectedCategory, noteInput)
                },
                modifier = Modifier.testTag("submit_manual_log")
            ) {
                Text("Log Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "Log Vital Reading", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = bpmInput,
                    onValueChange = { bpmInput = it },
                    label = { Text("Heart Rate (BPM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = { Text("e.g. 72") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = spo2Input,
                    onValueChange = { spo2Input = it },
                    label = { Text("Oxygen Saturation (SpO₂ %)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = { Text("e.g. 98") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Activity Context", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val categories = listOf("Resting", "Active", "Normal")
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(text = cat) }
                        )
                    }
                }

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Session Notes") },
                    singleLine = true,
                    placeholder = { Text("e.g. Normal office reading") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        modifier = Modifier.testTag("manual_log_entry_dialog")
    )
}
