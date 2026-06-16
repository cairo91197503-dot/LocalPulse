package com.localpulse.app.presentation.course

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localpulse.app.ui.theme.LocalPulseTheme

@Composable
fun CourseModule1Screen(
    viewModel: CourseModule1ViewModel = hiltViewModel(),
    onCourseCompleted: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val showQuiz by viewModel.showQuiz.collectAsState()
    val currentQuizIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val moduleCompleted by viewModel.moduleCompleted.collectAsState()

    LaunchedEffect(moduleCompleted) {
        if (moduleCompleted) {
            viewModel.completeModule()
            onCourseCompleted()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = showQuiz,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "Course Content"
        ) { isQuiz ->
            if (!isQuiz) {
                LessonContent(
                    lesson = viewModel.lessons[currentPage],
                    currentPage = currentPage,
                    totalPages = viewModel.lessons.size,
                    onNext = { viewModel.nextPage() },
                    onPrevious = { viewModel.previousPage() }
                )
            } else {
                QuizContent(
                    question = viewModel.quizQuestions[currentQuizIndex],
                    currentIndex = currentQuizIndex,
                    totalQuestions = viewModel.quizQuestions.size,
                    selectedAnswer = selectedAnswer,
                    score = quizScore,
                    onSelectAnswer = { viewModel.selectAnswer(it) },
                    onNext = { viewModel.nextQuestion() }
                )
            }
        }
    }
}

@Composable
private fun LessonContent(
    lesson: com.localpulse.app.domain.model.CourseLesson,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Emoji grande
        Text(
            text = lesson.emoji,
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Título
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        // Descrição
        Text(
            text = lesson.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Bullet points
        if (lesson.bulletPoints.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    lesson.bulletPoints.forEach { point ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✓ ", color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold)
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Indicador de progresso
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentPage > 0) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anterior")
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (currentPage == totalPages - 1) "Fazer Quiz! 🎯" else "Próximo",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun QuizContent(
    question: com.localpulse.app.domain.model.QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswer: Int?,
    score: Int,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = "Quiz 🎯",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Pergunta ${currentIndex + 1} de $totalQuestions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { (currentIndex + 1f) / totalQuestions },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = question.question,
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswer == index
            val isCorrect = index == question.correctIndex
            val hasAnswered = selectedAnswer != null

            val containerColor = when {
                hasAnswered && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                hasAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                onClick = { if (!hasAnswered) onSelectAnswer(index) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            hasAnswered && isCorrect -> "✅"
                            hasAnswered && isSelected && !isCorrect -> "❌"
                            else -> "${('A' + index)}"
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        if (selectedAnswer != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "💡 ${question.explanation}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (selectedAnswer != null) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (currentIndex == totalQuestions - 1) "Ver resultado! 🏆" else "Próxima pergunta",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
