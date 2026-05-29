package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.GeminiApiClient
import com.example.data.db.LocalPulseDao
import com.example.data.models.BusinessProfile
import com.example.data.models.Post
import com.example.data.models.Review
import com.example.data.models.SentimentSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class BusinessRepository(private val dao: LocalPulseDao) {

    val allReviews: Flow<List<Review>> = dao.getAllReviews()
    val positiveReviews: Flow<List<Review>> = dao.getPositiveReviews()
    val negativeReviews: Flow<List<Review>> = dao.getNegativeReviews()
    val unrepliedReviews: Flow<List<Review>> = dao.getUnrepliedReviews()
    val allPosts: Flow<List<Post>> = dao.getAllPosts()
    val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfile()
    val sentimentSummary: Flow<SentimentSummary?> = dao.getSentimentSummary()

    // Initialize business data on first login
    suspend fun initializeBusiness(businessName: String, accountType: String) = withContext(Dispatchers.IO) {
        // Prepare initial layout
        val initialProfile = BusinessProfile(
            name = businessName,
            rating = 4.4f,
            reviewCount = 50,
            unrepliedCount = 18,
            lastPostDate = formatDateMinusDays(6),
            accountType = accountType
        )
        dao.saveBusinessProfile(initialProfile)

        // Seed reviews
        val seed = generateSeedReviews(businessName)
        dao.insertReviews(seed)

        // Seed initial posts
        val seedPosts = listOf(
            Post(
                id = "p1",
                title = "🥖 Pão Quentinho Todo Dia!",
                content = "Venha experimentar o nosso famoso pão artesanal feito com fermentação natural. Fornada nova saindo a cada hora! Esperamos você na nossa loja física ou faça o seu pedido hoje mesmo.",
                createTime = formatDateMinusDays(6)
            ),
            Post(
                id = "p2",
                title = "🎉 Novidades no Cardápio",
                content = "Estamos lançando novas sobremesas deliciosas e totalmente artesanais! Diga não ao estresse e adoce o seu dia. Garanta o seu com desconto especial de lançamento mencionando LocalPulse!",
                createTime = formatDateMinusDays(12)
            )
        )
        dao.clearPosts()
        dao.insertPosts(seedPosts)

        // Generate the first AI summary
        generateWeeklySentimentSummary(seed, businessName)
    }

    // Generate Weekly Sentiment Summary with Gemini
    suspend fun generateWeeklySentimentSummary(reviews: List<Review>, businessName: String): String = withContext(Dispatchers.IO) {
        val ratingsCount = reviews.groupingBy { it.rating }.eachCount()
        val total = reviews.size
        val negativeCount = reviews.count { it.rating <= 3 }
        val negativePercentage = if (total > 0) (negativeCount * 100) / total else 0

        val reviewsSample = reviews.take(15).map {
            "Nota: ${it.rating} estrelas, Comentário: ${it.comment}"
        }.joinToString("\n")

        val prompt = """
            Você é um analista de reputação online especialista. Analise o lote de avaliações a seguir para o estabelecimento '$businessName':
            
            $reviewsSample
            
            Com base em todas as avaliações, gere um resumo estratégico semanal de sentimentos no seguinte formato exato de parágrafo único, em português do Brasil:
            "Seus clientes amam [X], mas [Y]% reclamaram de [Z]."
            Substitua [X] pelas qualidades mais mencionadas com entusiasmo. Substitua [Y] pelo percentual real informado ($negativePercentage). Substitua [Z] pelo principal ponto de queixa, dor ou insatisfação dos clientes.
            
            Após essa linha de resumo, pule uma linha e adicione exatamente 2 tópicos rápidos de sugestões de melhoria (cada um começando com '•').
            Escreva de forma profissional, animadora e direta.
        """.trimIndent()

        val systemInstruction = "Você é um profissional com foco em marketing local e business intelligence."
        val summaryText = GeminiApiClient.generateContent(prompt, systemInstruction)
        
        val summary = SentimentSummary(
            text = summaryText,
            dateUpdated = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        )
        dao.saveSentimentSummary(summary)
        summaryText
    }

    // Generate AI response recommendation for a review
    suspend fun generateResponseSuggestion(review: Review, businessName: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Escreva uma resposta de agradecimento ou esclarecimento (máximo 3 frases) em português do Brasil para a seguinte avaliação de cliente para o negócio '$businessName':
            Nome do Cliente: ${review.authorName}
            Nota: ${review.rating} estrelas
            Comentário: "${review.comment}"
            
            Se a avaliação for positiva (4-5 estrelas), agradeça com simpatia e convide para voltar.
            Se a avaliação for neutra/negativa (1-3 estrelas), expresse sinceras desculpas, mostre empatia e mencione o compromisso do negócio em resolver o problema técnico ou de atendimento.
            
            Mantenha o tom profissional, caloroso, e pessoal. Não use placeholders como [Seu Nome] ou [Telefone], assine apenas como "Equipe $businessName".
        """.trimIndent()

        GeminiApiClient.generateContent(prompt)
    }

    // Generate suggested post drafts
    suspend fun generatePostSuggestions(businessName: String): List<PostIdea> = withContext(Dispatchers.IO) {
        val prompt = """
            Como especialista em engajamento local para o Google Meu Negócio do estabelecimento '$businessName', crie exatamente 3 ideias criativas e completas de postagens em português do Brasil para atrair clientes locais.
            
            Retorne um JSON de array de objetos no seguinte formato de exemplo:
            [
              {
                "title": "Título chamativo do post",
                "content": "Conteúdo persuasivo do post contendo hashtags e Call to Action, por exemplo venha nos visitar!"
              }
            ]
            Retorne APENAS o JSON válido sem formatações de markdown adicionais como ```json. Comece com '[' e termine com ']'. Mantenha o conteúdo em português.
        """.trimIndent()

        val response = GeminiApiClient.generateContent(prompt)
        try {
            // Clean up backticks if any
            val cleanJson = response.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(cleanJson)
            val ideas = mutableListOf<PostIdea>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                ideas.add(
                    PostIdea(
                        title = obj.getString("title"),
                        content = obj.getString("content")
                    )
                )
            }
            ideas
        } catch (e: Exception) {
            Log.e("BusinessRepository", "Falha ao analisar ideias de posts", e)
            listOf(
                PostIdea("☕ Promoção Especial", "Venha dar um impulso no seu dia com nosso café moído na hora e acompanhado de delícias saídas do forno agora mesmo! Marque seu amigo nos comentários."),
                PostIdea("🌟 Sabor Que Conecta", "Cada detalhe aqui é pensado para dar a você a melhor experiência possível. Veja o que nossos clientes dizem de nós e comprove!"),
                PostIdea("📅 Reserve Seu Momento", "Que tal planejar aquela pausa de qualidade no meio do dia corrido? Estamos abertos e prontos para atender você com o carinho de sempre!")
            )
        }
    }

    // Submit reply through API / Update local cache
    suspend fun replyToReview(reviewId: String, replyText: String) = withContext(Dispatchers.IO) {
        val review = dao.getReviewById(reviewId) ?: return@withContext
        val updatedReview = review.copy(
            replyText = replyText,
            isReplied = true,
            isOfflinePending = false
        )
        dao.updateReview(updatedReview)

        // Decrement unreplied count in profile
        val profile = dao.getBusinessProfile().firstOrNull()
        if (profile != null) {
            val updatedProfile = profile.copy(
                unrepliedCount = (profile.unrepliedCount - 1).coerceAtLeast(0)
            )
            dao.saveBusinessProfile(updatedProfile)
        }
    }

    // Create a new post
    suspend fun createPost(title: String, content: String, imageUrl: String? = null, scheduledTime: String? = null) = withContext(Dispatchers.IO) {
        val newPost = Post(
            id = "user_post_${System.currentTimeMillis()}",
            title = title,
            content = content,
            imageUrl = imageUrl,
            createTime = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            scheduledTime = scheduledTime
        )
        dao.insertPost(newPost)

        // Update profile last post date
        val profile = dao.getBusinessProfile().firstOrNull()
        if (profile != null) {
            val updatedProfile = profile.copy(
                lastPostDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            )
            dao.saveBusinessProfile(updatedProfile)
        }
    }

    private fun formatDateMinusDays(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
    }

    // Local Review Generator (50 items) with varying ratings & comments in pt-BR
    private fun generateSeedReviews(businessName: String): List<Review> {
        val authors = listOf(
            "Carlos Andrade", "Mariana Costa", "Roberto Silva", "Juliana Nogueira",
            "Felipe Melo", "Beatriz Pinheiro", "Gustavo Santos", "Amanda Oliveira",
            "Lucas Teixeira", "Camila Martins", "Ricardo Souza", "Larissa Ferreira",
            "Daniel Carvalho", "Patricia Ramos", "Eduardo Guedes", "Fernanda Lima",
            "Tiago Araujo", "Sofia Rocha", "Gabriel Marques", "Julio Cesar",
            "Priscila Mendes", "Otavio Neto", "Marina Dias", "Marcelo Vieira",
            "Vanessa Castro", "Cesar Machado", "Regina Souza", "Alexandre Cruz",
            "Tatiane Reis", "Fabricio Duarte", "Isabela Ribeiro", "Leandro Lima",
            "Clara Mendes", "Bruno Alves", "Elisa Fonseca", "Rodrigo Cunha",
            "Helena Torres", "Mateus Costa", "Gabriela Novaes", "Andre Guimarães",
            "Jessica Santos", "Valter Jr", "Cristina Rocha", "Mauricio Neves",
            "Luana Siqueira", "Enzo Gabriel", "Valentina Flores", "Lorena Viana",
            "Aline Morais", "Yago Santana"
        )

        val comments = listOf(
            // 5 stars
            Pair(5, "Atendimento impecável! O melhor da região disparado. Super recomendo a todos!"),
            Pair(5, "Ambiente super aconchegante, as opções do cardápio são incríveis. Voltarei sempre!"),
            Pair(5, "Fácil acesso, equipe sorridente e produtos de alta qualidade técnica."),
            Pair(5, "Maravilhoso! Sempre que venho me sinto extremamente satisfeito. Nota dez!"),
            Pair(5, "Agilidade incrível no atendimento, adorei a recepção."),
            Pair(5, "Simplesmente sensacional, preço justo pela excelente qualidade dos serviços."),
            
            // 4 stars
            Pair(4, "Gostei muito dos produtos, muito saborosos. Teve um pequeno atraso, mas aceitável."),
            Pair(4, "Muito bom. Preços em conta e ambiente limpo. Só acho que a fila do caixa poderia ser mais rápida."),
            Pair(4, "Excelente opção local. Recomendo o pão de fermentação natural. Atendimento nota 9!"),
            Pair(4, "Espaço muito bem iluminado e os atendentes conhecem bem os produtos."),
            Pair(4, "Gostei bastante. O café estava delicioso. Apenas achei um pouco barulhento."),
            Pair(4, "Os bolos são deliciosos! Só podiam aceitar mais bandeiras de vale-refeição."),

            // 3 stars
            Pair(3, "O serviço é okay, mas o tempo de espera no caixa foi muito longo hoje. Estava cheio demais."),
            Pair(3, "Produtos de qualidade média, mas o valor é um pouco acima do esperado para o bairro."),
            Pair(3, "Bom atendimento dos funcionários, mas falta maior variedade de produtos no final da tarde."),
            Pair(3, "Instalações boas, mas o estacionamento na frente é muito apertado e confuso."),
            Pair(3, "O ar condicionado estava muito gelado, parecia uma geladeira. Fora isso, o pão é gostoso."),

            // 2 stars
            Pair(2, "Infelizmente o atendimento deixou a desejar hoje. Fiquei 15 minutos esperando na fila do caixa sem suporte."),
            Pair(2, "Os produtos são razoáveis, mas faltou cordialidade da equipe no caixa. Estavam impacientes."),
            Pair(2, "Preço caro e entrega demorada. Chegou frio."),

            // 1 star
            Pair(1, "Estive no local no sábado e foi uma péssima experiência. Fila gigante, falta de higienização e atendentes grossos."),
            Pair(1, "Não recomendo. Erraram meu pedido duas vezes e demorou uma eternidade para estornar. Terrível."),
            Pair(1, "Atendimento péssimo. Cara feia de quem nos atende, parece que estão fazendo um favor.")
        )

        val random = Random(42) // Use a seed for predictability
        val reviewsList = mutableListOf<Review>()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()

        for (i in 0 until 50) {
            val author = authors.getOrElse(i) { "Usuário ${i + 1}" }
            val pair = comments[random.nextInt(comments.size)]
            val rating = pair.first
            val comment = pair.second

            // Subtract minutes/hours to spread them cleanly
            cal.time = Date()
            cal.add(Calendar.HOUR_OF_DAY, -random.nextInt(1, 120))
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(0, 15))
            val dateStr = sdf.format(cal.time)

            // Make some of them already replied (e.g. 5 stars usually, about 60% replied)
            val isReplied = i >= 18 // First 18 are "not replied", rest of the 50 are already replied
            val replyText = if (isReplied) {
                if (rating >= 4) "Muito obrigado pelo excelente feedback, ${author}! Esperamos vê-lo de novo em breve."
                else "Pedimos desculpas pelo transtorno, ${author}. Levamos suas palavras muito a sério para melhorar."
            } else null

            reviewsList.add(
                Review(
                    id = "rev_$i",
                    authorName = author,
                    authorPhotoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100&q=80",
                    rating = rating,
                    comment = comment,
                    createTime = dateStr,
                    replyText = replyText,
                    isReplied = isReplied
                )
            )
        }

        // Sort reviews so recent ones are first
        return reviewsList.sortedByDescending { it.createTime }
    }
}

data class PostIdea(
    val title: String,
    val content: String
)
