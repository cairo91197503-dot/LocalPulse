package com.localpulse.app.domain.model

object TasksBank {

    val allTasks = listOf(
        DailyTask("t1", "📸", "Adicione uma foto ao perfil",
            "Negócios com fotos recebem 42% mais pedidos de rotas", 15, category = "foto"),
        DailyTask("t2", "✍️", "Responda uma avaliação",
            "Responder avaliações aumenta sua credibilidade no Google", 20, category = "avaliacao"),
        DailyTask("t3", "📝", "Crie um post no Google",
            "Posts mantêm seu perfil ativo e atraem mais clientes", 15, category = "post"),
        DailyTask("t4", "🕐", "Verifique seu horário",
            "Horários incorretos afastam clientes. Confira se está atualizado", 10, category = "perfil"),
        DailyTask("t5", "💬", "Peça avaliação para um cliente",
            "Envie seu QR Code para um cliente hoje", 20, category = "avaliacao"),
        DailyTask("t6", "🔍", "Revise a descrição do negócio",
            "Uma boa descrição melhora seu ranqueamento na busca", 15, category = "perfil"),
        DailyTask("t7", "📊", "Analise seu score",
            "Faça o diagnóstico do seu perfil e veja o que melhorar", 10, category = "diagnostico"),
        DailyTask("t8", "🎯", "Defina uma meta de avaliações",
            "Negócios com mais de 50 avaliações recebem 2x mais cliques", 10, category = "avaliacao"),
        DailyTask("t9", "💡", "Adicione um produto ou serviço",
            "Perfis com produtos têm mais conversão", 15, category = "perfil"),
        DailyTask("t10", "🌟", "Compartilhe seu QR Code",
            "Coloque seu QR Code em um local visível do seu negócio", 20, category = "qrcode")
    )

    fun getTasksForToday(dayOfYear: Int): List<DailyTask> {
        val shuffled = allTasks.shuffled(java.util.Random(dayOfYear.toLong()))
        return shuffled.take(3)
    }

    fun getLevelName(level: Int): String = when (level) {
        1 -> "Iniciante"
        2 -> "Em Crescimento"
        3 -> "Estabelecido"
        4 -> "Destaque Local"
        5 -> "Referência"
        else -> "Lenda Local"
    }

    fun getXpForNextLevel(level: Int): Int = level * 100
}
