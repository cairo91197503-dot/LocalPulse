package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun AppTutorialDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("tutorial_done_button")
            ) {
                Text("Entendido, vamos lá!")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informações do Tutorial",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bem-vindo ao PulsePersonal 👋",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Veja o que você pode fazer por aqui:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                TutorialRow(
                    icon = Icons.Default.Dashboard,
                    title = "Painel de Reputação",
                    description = "Acompanhe sua pontuação de reputação, análise de sentimento e feedbacks recebidos nas suas redes sociais em um só lugar."
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialRow(
                    icon = Icons.AutoMirrored.Filled.Send,
                    title = "Agendamento de Posts",
                    description = "Visualize e gerencie seus posts agendados e publicados no Instagram, Facebook, TikTok e YouTube diretamente pela aba Posts."
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Sugestões por IA",
                    description = "Receba sugestões automáticas de resposta para avaliações e ideias de conteúdo geradas pelo Gemini AI."
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialRow(
                    icon = Icons.Default.Hub,
                    title = "Integração de Redes",
                    description = "Conecte suas contas do Instagram, Facebook, TikTok e WhatsApp na aba Ajustes para centralizar sua gestão."
                )

                Spacer(modifier = Modifier.height(12.dp))

                TutorialRow(
                    icon = Icons.Default.Rocket,
                    title = "Piloto Automático",
                    description = "No plano Expert+, ative o piloto automático para publicações e respostas gerenciadas pela IA sem intervenção manual."
                )
            }
        },
        modifier = Modifier.testTag("app_tutorial_dialog")
    )
}

@Composable
fun TutorialRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
