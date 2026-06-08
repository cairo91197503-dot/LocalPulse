package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ACADEMY_CARDS
import com.example.ui.viewmodel.AcademyCard
import com.example.ui.viewmodel.AcademyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val unlockedCards by viewModel.unlockedCards.collectAsState()
    val completedCards by viewModel.completedCards.collectAsState()
    val totalXP by viewModel.totalXP.collectAsState()
    val openCardId by viewModel.openCardId.collectAsState()
    val cardPhase by viewModel.cardPhase.collectAsState()
    val checkedItems by viewModel.checkedItems.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val showQuizResult by viewModel.showQuizResult.collectAsState()
    val justUnlockedCardId by viewModel.justUnlockedCardId.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()

    val level = viewModel.getNivel()
    val maxXP = viewModel.maxXP

    val openCard = remember(openCardId) {
        ACADEMY_CARDS.find { it.id == openCardId }
    }

    // Fundo gradient escuro #0a0a0f -> #0d1117 -> #0a0f1a
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A0A0F),
            Color(0xFF0D1117),
            Color(0xFF0A0F1A)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .testTag("academy_screen_container")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // HEADER
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("⚡", fontSize = 28.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            text = "LocalPulse ",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Academy",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF60A5FA)
                        )
                    }

                    Text(
                        text = "Colecione cards. Domine sua reputação online.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Dica Diária / Foco do Dia baseado no progresso
                    if (dailyTip != null) {
                        val isAllCompleted = completedCards.size == ACADEMY_CARDS.size
                        Spacer(modifier = Modifier.height(18.dp))
                        Card(
                            onClick = { viewModel.openCard(dailyTip!!.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .testTag("daily_tip_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAllCompleted) Color(0xFF111827) else Color(dailyTip!!.cardColor).copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (isAllCompleted) Color(0xFF60A5FA).copy(alpha = 0.4f) else Color(dailyTip!!.accentColor).copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isAllCompleted) "🌟" else "💡",
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Text(
                                            text = if (isAllCompleted) "REPUTAÇÃO MÁXIMA" else "FOCO RECOMENDADO",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = if (isAllCompleted) Color(0xFF60A5FA) else Color(dailyTip!!.accentColor)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = (if (isAllCompleted) Color(0xFF60A5FA) else Color(dailyTip!!.accentColor)).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isAllCompleted) "SUCESSO" else "DICA DIÁRIA",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.sp
                                            ),
                                            color = if (isAllCompleted) Color(0xFF60A5FA) else Color(dailyTip!!.accentColor)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                                ) {
                                    Text(
                                        text = if (isAllCompleted) "🚀" else dailyTip!!.emoji,
                                        fontSize = 28.sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isAllCompleted) "Fórmula de Sucesso" else "Lição sugerida: ${dailyTip!!.title}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isAllCompleted) "Expert na LocalPulse Academy" else dailyTip!!.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = (if (isAllCompleted) Color(0xFF60A5FA) else Color(dailyTip!!.accentColor)).copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isAllCompleted) {
                                        "Parabéns! Você concluiu com sucesso todos os cards teóricos e práticos e se consagrou Mestre LocalPulse! Continue revisando os conceitos sempre que desejar cristalizar seu aprendizado."
                                    } else {
                                        "Dica de hoje: ${dailyTip!!.description} que tal reservar alguns minutos para revisar os conceitos práticos e coletar ${dailyTip!!.xp} XP para subir seu nível?"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 1.5.sp),
                                    color = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Button(
                                    onClick = { viewModel.openCard(dailyTip!!.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("btn_daily_focus_action"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAllCompleted) Color(0xFF60A5FA) else Color(dailyTip!!.accentColor),
                                        contentColor = if (isAllCompleted) Color(0xFF0D1117) else Color(dailyTip!!.cardColor)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isAllCompleted) "Rever os Cards de Estudo 🔄" else "Acessar Card Recomendado →",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold)
                                    )
                                }
                            }
                        }
                    }

                    // Card de Progresso Global
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.04f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "NÍVEL ATUAL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White.copy(alpha = 0.45f)
                                    )
                                    Text(
                                        text = "🏆 $level",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "EXPERIÊNCIA TOTAL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White.copy(alpha = 0.45f)
                                    )
                                    Text(
                                        text = "$totalXP / $maxXP XP",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color(0xFF60A5FA)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val progress = if (maxXP > 0) totalXP.toFloat() / maxXP else 0f
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .testTag("academy_xp_progress"),
                                color = Color(0xFF60A5FA),
                                trackColor = Color.White.copy(alpha = 0.08f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${unlockedCards.size}/${ACADEMY_CARDS.size} cards desbloqueados",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Cards por Tier
            val tiers = listOf("INICIANTE", "INTERMEDIÁRIO", "AVANÇADO", "MESTRE")
            for (tier in tiers) {
                val tierCards = ACADEMY_CARDS.filter { it.tier == tier }
                if (tierCards.isNotEmpty()) {
                    val badgeColor = Color(tierCards.first().tierColor)
                    
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tier,
                                    color = badgeColor,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        }
                    }

                    val rows = tierCards.chunked(2)
                    for (rowCards in rows) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                for (card in rowCards) {
                                    val isUnlocked = unlockedCards.contains(card.id)
                                    val isCompleted = completedCards.contains(card.id)

                                    Box(modifier = Modifier.weight(1f)) {
                                        Card(
                                            onClick = { if (isUnlocked) viewModel.openCard(card.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .padding(4.dp)
                                                .alpha(if (isUnlocked) 1f else 0.45f)
                                                .testTag("academy_card_${card.id}"),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isUnlocked) Color(card.cardColor) else Color(0xFF0D1117)
                                            ),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (isUnlocked) Color(card.accentColor).copy(alpha = 0.33f) else Color.White.copy(alpha = 0.06f)
                                            )
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                                if (!isUnlocked) {
                                                    Text(
                                                        text = "🔒",
                                                        fontSize = 14.sp,
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .alpha(0.6f)
                                                    )
                                                } else if (isCompleted) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .background(Color(card.accentColor).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ COMPLETO",
                                                            color = Color(card.accentColor),
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        )
                                                    }
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = card.emoji,
                                                        style = MaterialTheme.typography.headlineMedium
                                                    )
                                                    Column {
                                                        Text(
                                                            text = card.title,
                                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = Color.White,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = card.subtitle,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = Color(card.accentColor),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(card.accentColor).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                            .border(1.dp, Color(card.accentColor).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "⚡ ${card.xp} XP",
                                                            color = Color(card.accentColor),
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowCards.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM SHEET DE MINI QUESTS (TUTORIALS)
        if (openCard != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeCard() },
                containerColor = Color(openCard.cardColor),
                modifier = Modifier.testTag("card_detail_bottom_sheet"),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                    )
                }
            ) {
                CardDetailContent(
                    card = openCard,
                    phase = cardPhase,
                    checkedItems = checkedItems,
                    selectedAnswer = selectedAnswer,
                    showResult = showQuizResult,
                    justUnlockedId = justUnlockedCardId,
                    onToggleCheck = { viewModel.toggleCheckItem(it) },
                    onSelectAnswer = { viewModel.selectQuizAnswer(it) },
                    onRetryQuiz = { viewModel.retryQuiz() },
                    onComplete = { viewModel.completeCard(openCard) },
                    onSetPhase = { viewModel.setPhase(it) },
                    onClose = { viewModel.closeCard() }
                )
            }
        }
    }
}

@Composable
fun CardDetailContent(
    card: AcademyCard,
    phase: AcademyViewModel.CardPhase,
    checkedItems: Set<Int>,
    selectedAnswer: Int?,
    showResult: Boolean,
    justUnlockedId: Int?,
    onToggleCheck: (Int) -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onRetryQuiz: () -> Unit,
    onComplete: () -> Unit,
    onSetPhase: (AcademyViewModel.CardPhase) -> Unit,
    onClose: () -> Unit
) {
    val accentColor = Color(card.accentColor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // CORPO CABEÇALHO DO CARD NO BOTTOM SHEET
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = card.emoji,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.tier,
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = card.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "recompensa",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = "⚡${card.xp}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = accentColor
                )
            }
        }

        if (phase != AcademyViewModel.CardPhase.REWARD) {
            // TABS (3 botões de fase)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(4.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    AcademyViewModel.CardPhase.OVERVIEW to "📖 Lição",
                    AcademyViewModel.CardPhase.CHECKLIST to "✅ Tarefas",
                    AcademyViewModel.CardPhase.QUIZ to "🧠 Quiz"
                ).forEach { (p, label) ->
                    val isSelected = phase == p
                    val tabBg = if (isSelected) accentColor else Color.White.copy(alpha = 0.06f)
                    val tabContentColor = if (isSelected) Color(card.cardColor) else Color.White.copy(alpha = 0.5f)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tabBg)
                            .clickable { onSetPhase(p) }
                            .testTag("tab_button_${p.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = tabContentColor
                        )
                    }
                }
            }
        }

        // CONTEÚDO DE ACORDO COM A ABA ATIVA
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
            },
            label = "tab_content_transition"
        ) { currentPhase ->
            when (currentPhase) {
                AcademyViewModel.CardPhase.OVERVIEW -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = card.description,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 1.7.sp),
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, accentColor.copy(alpha = 0.33f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "💡 DICA PRÁTICA",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = accentColor,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = card.tip,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 1.6.sp),
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onSetPhase(AcademyViewModel.CardPhase.CHECKLIST) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_goto_checklist"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color(card.cardColor)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Começar Tarefas →",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                AcademyViewModel.CardPhase.CHECKLIST -> {
                    val allChecked = checkedItems.size == card.checklist.size

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Marque todas as tarefas para avançar ao quiz",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        card.checklist.forEachIndexed { idx, item ->
                            val isChecked = checkedItems.contains(idx)
                            val itemBg = if (isChecked) accentColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f)
                            val itemBorderColor = if (isChecked) accentColor.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.08f)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(itemBg)
                                    .border(1.dp, itemBorderColor, RoundedCornerShape(12.dp))
                                    .clickable { onToggleCheck(idx) }
                                    .padding(14.dp)
                                    .testTag("checklist_item_$idx"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggleCheck(idx) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = accentColor,
                                        uncheckedColor = Color.White.copy(alpha = 0.4f),
                                        checkmarkColor = Color(card.cardColor)
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress bar para checklist
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val checkProgress = if (card.checklist.isNotEmpty()) checkedItems.size.toFloat() / card.checklist.size else 0f
                            LinearProgressIndicator(
                                progress = checkProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = accentColor,
                                trackColor = Color.White.copy(alpha = 0.08f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${checkedItems.size}/${card.checklist.size}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onSetPhase(AcademyViewModel.CardPhase.QUIZ) },
                            enabled = allChecked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_goto_quiz"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (allChecked) accentColor else Color.White.copy(alpha = 0.12f),
                                contentColor = if (allChecked) Color(card.cardColor) else Color.White.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Ir para o Quiz 🧠",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                AcademyViewModel.CardPhase.QUIZ -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "PERGUNTA",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = accentColor,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = card.quizQuestion,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 1.6.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        card.quizOptions.forEachIndexed { index, option ->
                            val isSelected = selectedAnswer == index
                            val isRight = index == card.quizCorrect
                            val itemBg = when {
                                showResult && isRight -> Color(0xFF1B5E20).copy(alpha = 0.25f)
                                showResult && isSelected && !isRight -> Color(0xFFB71C1C).copy(alpha = 0.25f)
                                isSelected -> accentColor.copy(alpha = 0.22f)
                                else -> Color.White.copy(alpha = 0.04f)
                            }
                            val itemBorder = when {
                                showResult && isRight -> Color(0xFF4CAF50).copy(alpha = 0.6f)
                                showResult && isSelected && !isRight -> Color(0xFFF44336).copy(alpha = 0.6f)
                                isSelected -> accentColor.copy(alpha = 0.6f)
                                else -> Color.White.copy(alpha = 0.08f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(itemBg)
                                    .border(1.dp, itemBorder, RoundedCornerShape(12.dp))
                                    .clickable { if (!showResult) onSelectAnswer(index) }
                                    .padding(14.dp)
                                    .testTag("quiz_option_$index"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Círculo ou Indicador
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isSelected) accentColor else Color.White.copy(alpha = 0.08f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (showResult && isRight) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(card.cardColor) else Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else if (showResult && isSelected && !isRight) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "ABCD"[index].toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Color(card.cardColor) else Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }

                        if (showResult) {
                            val isCorrect = selectedAnswer == card.quizCorrect
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isCorrect) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color(0xFFB71C1C).copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.33f) else Color(0xFFF44336).copy(alpha = 0.33f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "🎉 Excelente! Resposta exata." else "❌ Que pena! Não foi dessa vez.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isCorrect) {
                                Button(
                                    onClick = onComplete,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_collect_xp"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor,
                                        contentColor = Color(card.cardColor)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "⚡ Coletar ${card.xp} XP e Concluir Card!",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = onRetryQuiz,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_retry_quiz"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.08f),
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Tentar novamente",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                AcademyViewModel.CardPhase.REWARD -> {
                    val justUnlockedCard = ACADEMY_CARDS.find { it.id == justUnlockedId }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = "Card Completo!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = "Você ganhou ${card.xp} XP",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Visualização estática do card de recompensa
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .height(230.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = accentColor.copy(alpha = 0.12f),
                            border = BorderStroke(2.dp, accentColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = card.emoji,
                                    fontSize = 44.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = card.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Concluído",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = accentColor,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "⚡ ADICIONADO",
                                        color = accentColor,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }

                        if (justUnlockedCard != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "🔓 Novo card desbloqueado: ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = justUnlockedCard.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(justUnlockedCard.accentColor)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onClose,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_reward_close"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color(card.cardColor)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Ver Coleção",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
