package com.localpulse.app.domain.model

/**
 * Resultado do diagnóstico gerado pela IA.
 */
data class DiagnosisResult(
    val score: Int = 0,
    val nivel: String = "",
    val resumo: String = "",
    val pontosPositivos: List<String> = emptyList(),
    val pontosNegativos: List<String> = emptyList(),
    val acoesPrioritarias: List<AcaoPrioritaria> = emptyList()
)

data class AcaoPrioritaria(
    val titulo: String = "",
    val descricao: String = "",
    val impacto: String = ""
)
