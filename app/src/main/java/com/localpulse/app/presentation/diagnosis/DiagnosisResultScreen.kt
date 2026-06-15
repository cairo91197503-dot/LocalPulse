package com.localpulse.app.presentation.diagnosis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.localpulse.app.domain.model.AcaoPrioritaria
import com.localpulse.app.domain.model.DiagnosisResult
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisResultScreen(
    diagnosisJson: String,
    onNavigateBack: () -> Unit,
    onNewDiagnosis: () -> Unit
) {
    val diagnosis = remember(diagnosisJson) { parseDiagnosis(diagnosisJson) }

    val scoreColor = when {
        diagnosis.score >= 80 -> Color(0xFF2E7D32)
        diagnosis.score >= 60 -> Color(0xFF1565C0)
        diagnosis.score >= 40 -> Color(0xFFF57F17)
        else -> Color(0xFFC62828)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnóstico de Reputação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${diagnosis.score}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                        Text(
                            text = diagnosis.nivel,
                            style = MaterialTheme.typography.titleLarge,
                            color = scoreColor
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { diagnosis.score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = scoreColor
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = diagnosis.resumo,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (diagnosis.pontosPositivos.isNotEmpty()) {
                item {
                    Text("✅ Pontos positivos",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32))
                }
                items(diagnosis.pontosPositivos) { ponto ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = "• $ponto",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (diagnosis.pontosNegativos.isNotEmpty()) {
                item {
                    Text("⚠️ Pontos de atenção",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFC62828))
                }
                items(diagnosis.pontosNegativos) { ponto ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = "• $ponto",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (diagnosis.acoesPrioritarias.isNotEmpty()) {
                item {
                    Text("🎯 Ações prioritárias",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                }
                items(diagnosis.acoesPrioritarias) { acao ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = acao.titulo,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                val impactoColor = when (acao.impacto) {
                                    "Alto" -> Color(0xFFC62828)
                                    "Médio" -> Color(0xFFF57F17)
                                    else -> Color(0xFF2E7D32)
                                }
                                Surface(
                                    color = impactoColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = acao.impacto,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = impactoColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = acao.descricao,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNewDiagnosis,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Novo diagnóstico")
                }
            }
        }
    }
}

private fun parseDiagnosis(json: String): DiagnosisResult {
    return try {
        val cleanJson = json.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val obj = JSONObject(cleanJson)
        val positivos = obj.getJSONArray("pontos_positivos")
        val negativos = obj.getJSONArray("pontos_negativos")
        val acoes = obj.getJSONArray("acoes_prioritarias")

        DiagnosisResult(
            score = obj.getInt("score"),
            nivel = obj.getString("nivel"),
            resumo = obj.getString("resumo"),
            pontosPositivos = (0 until positivos.length()).map { positivos.getString(it) },
            pontosNegativos = (0 until negativos.length()).map { negativos.getString(it) },
            acoesPrioritarias = (0 until acoes.length()).map {
                val a = acoes.getJSONObject(it)
                AcaoPrioritaria(
                    titulo = a.getString("titulo"),
                    descricao = a.getString("descricao"),
                    impacto = a.getString("impacto")
                )
            }
        )
    } catch (e: Exception) {
        DiagnosisResult(
            score = 0,
            nivel = "Erro",
            resumo = "Não foi possível processar o diagnóstico. Tente novamente.",
        )
    }
}
