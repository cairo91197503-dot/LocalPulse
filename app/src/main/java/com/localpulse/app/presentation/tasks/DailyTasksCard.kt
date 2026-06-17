package com.localpulse.app.presentation.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localpulse.app.domain.model.DailyTask
import com.localpulse.app.domain.model.TasksBank
import com.localpulse.app.domain.model.UserProgress

@Composable
fun DailyTasksCard(
    tasks: List<DailyTask>,
    progress: UserProgress?,
    isLoading: Boolean,
    onCompleteTask: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header com XP e nível
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Tarefas de hoje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (progress != null) {
                        Text(
                            "${TasksBank.getLevelName(progress.level)} • ${progress.totalXp} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (progress != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "Nível ${progress.level}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Barra de progresso do dia
            val completedCount = tasks.count { it.isCompleted }
            val progressFraction = if (tasks.isEmpty()) 0f else completedCount / tasks.size.toFloat()

            Text(
                "$completedCount/${tasks.size} concluídas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (progressFraction == 1f)
                    Color(0xFF2E7D32)
                else
                    MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // Lista de tarefas
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp)
                )
            } else {
                tasks.forEach { task ->
                    TaskItem(task = task, onComplete = { onCompleteTask(task.id, task.xp) })
                    if (task != tasks.last()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            // Mensagem de parabéns
            if (completedCount == tasks.size && tasks.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "🎉 Incrível! Todas as tarefas concluídas hoje!",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(task: DailyTask, onComplete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(task.emoji, fontSize = 24.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted)
                    TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                "+${task.xp} XP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (!task.isCompleted) {
            FilledTonalButton(
                onClick = onComplete,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Feito", style = MaterialTheme.typography.labelMedium)
            }
        } else {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Concluída",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
