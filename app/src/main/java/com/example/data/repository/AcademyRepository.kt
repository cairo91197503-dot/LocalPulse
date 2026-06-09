package com.example.data.repository

import com.example.data.models.AcademyArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AcademyRepository {

    private val articles = MutableStateFlow(
        listOf(
            AcademyArticle(
                id = "create_profile",
                title = "Como criar seu Perfil do Google Meu Negócio do zero",
                description = "Um guia detalhado com o passo-a-passo exato para registrar sua empresa no Google e aparecer nas buscas e maps gratuitamente.",
                category = "Como Iniciar",
                contentMarkdown = """
                    ### Guia Completo: Criando seu Perfil no Google Meu Negócio
                    
                    Ter o seu negócio cadastrado no Google é o primeiro e mais importante passo para atrair clientes locais. Siga os passos abaixo para registrar sua conta agora mesmo:

                    Let's go:
                    1. **Acesse o Portal**: Vá para [google.com/business](https://google.com/business) e clique em "Gerenciar agora".
                    2. **Digite o Nome da sua Empresa**: Escolha um nome claro. Não tente entupir o nome de palavras-chave desnecessárias para não violar as diretrizes do Google (ex: use "Aroma Brew Café" em vez de "Aroma Brew Café Melhor Cappuccino Pão de Queijo Barato").
                    3. **Escolha sua Categoria**: Esse passo é crucial! Selecione uma categoria que descreva exatamente o seu negócio principal (ex: "Restaurante", "Salão de Beleza", "Advogado"). Você poderá adicionar subcategorias depois.
                    4. **Adicione seu Local Físico**: Se você atende clientes no seu endereço, marque "Sim" e insira o endereço completo. Caso você seja um prestador de serviço que atende a domicílio (como encanador ou eletricista), marque "Não" e defina sua área de cobertura.
                    5. **Insira Contato e Website**: Adicione seu telefone comercial e o link do seu site (se não tiver, opte por link do WhatsApp ou redes sociais).
                    6. **Verificação**: O Google geralmente exige uma verificação para garantir que você é o proprietário. Ela pode ser feita por telefone, e-mail, gravação de vídeo do local ou carta postal enviado ao endereço indicado.
                    
                    **Dica de Ouro:** Preencha 100% de todas as informações opcionais logo após criar. Perfis completos ganham mais relevância rápida no ranqueamento local!
                """.trimIndent()
            ),
            AcademyArticle(
                id = "seo_rules",
                title = "Regras Secretas de SEO Local para ranquear no topo",
                description = "Como o algoritmo do Google decide quem aparece nos 3 primeiros lugares do Mapa, e como você pode vencer os concorrentes.",
                category = "SEO Local",
                contentMarkdown = """
                    ### Dominando o Algoritmo de Busca Local do Google
                    
                    Quando alguém pesquisa por "padaria perto de mim" ou "dentista em São Paulo", o Google exibe uma seção especial chamada **Google Local Pack** (os 3 negócios em maior destaque no mapa). O algoritmo avalia principalmente 3 grandes pilares:

                    1. **Relevância**: O quão bem o seu perfil corresponde à pesquisa da pessoa. Ter categorias bem configuradas e uma descrição rica em termos chaves ajuda o Google a entender as suas atividades.
                    2. **Distância**: O quão próximo o seu negócio está do usuário buscando. Não há muito controle técnico sobre isso, mas você pode definir claramente sua área de atuação nas configurações.
                    3. **Proeminência**: A reputação online que o seu negócio construiu. Ela é calculada através de links espalhados pela web, notícias e, de modo mais forte, pela atividade e notas nas **Avaliações do Google**.
                    
                    #### Plano de Ação Prático para Ranquear Melhor:
                    - **Uniformidade do NAP**: Garanta que seu Nome, Endereço e Telefone (NAP) sejam exatamente idênticos no Google, no seu site próprio, no Facebook, Instagram e listas telefônicas.
                    - **Atributos de Pesquisa**: Marque itens opcionais do painel, como "Estacionamento gratuito", "Acessível para cadeirantes", "Possui Wi-Fi". Muitas buscas utilizam filtros baseados nisso!
                    - **Atualizações Frequentes**: Perfis que publicam posts semanais mostram ao robozinho do Google que o negócio está ativo e operando.
                """.trimIndent()
            ),
            AcademyArticle(
                id = "ratings_strategy",
                title = "Estratégia infalível para ganhar avaliações 5 estrelas",
                description = "Templates de mensagens reais, QR Codes para colocar no balcão e abordagens elegantes para aumentar suas notas positivas.",
                category = "Avaliações",
                contentMarkdown = """
                    ### Como Dobrar suas Avaliações de Forma Orgânica e Rápida
                    
                    Avaliações não influenciam apenas o seu SEO — elas são o fator #1 para que novos clientes confiem na sua marca. Veja como obter ótimas notas:

                    #### 1. Crie seu Link Direto de Avaliação:
                    No painel do Google, busque pelo botão "Solicitar avaliações" e copie o link direto curto (ex: `https://g.page/r/XYZ/review`). Nunca mande o cliente pesquisar seu nome no Google para depois clicar, facilite o caminho!
                    
                    #### 2. QR Code Físico no Balcão:
                    Gere um código QR apontando para o seu link direto e coloque-o visivelmente em display acrílico no seu ponto de venda (caixa, mesa, recepção). Adicione uma frase chamativa como: "Gostou do atendimento? Escaneie e ganhe um café/desconto na próxima!"
                    
                    #### 3. Abordagem no Momento Certo (Regra dos 5 Minutos):
                    - **Alimentação**: Peça a avaliação imediatamente após o cliente elogiar o prato ou a entrega.
                    - **Serviços/Estética**: Solicite o feedback ao finalizar o serviço, quando o cliente estiver vislumbrando o resultado final no espelho.
                    - **Varejo/E-commerce**: Envie uma mensagem atenciosa via WhatsApp 2 dias após a chegada do produto perguntando se tudo correu bem, seguida do link de avaliação.
                    
                    **Lembre-se:** Responder todas as avaliações — tanto as boas quanto as ruins — com simpatia é essencial para converter visualizadores em leads qualificados.
                """.trimIndent()
            ),
            AcademyArticle(
                id = "regular_posting",
                title = "O poder das fotos frequentes e postagens otimizadas",
                description = "Descubra como colocar fotos no padrão correto e manter ideias criativas de posts semanais que mantêm a conta engajada.",
                category = "Posts Otimizados",
                contentMarkdown = """
                    ### Marketing Visual e Posts de Engajamento no Google Maps
                    
                    Diferente de redes sociais tradicionais como o Instagram, o público no Google Maps está com **intenção de compra imediata**. Eles já estão procurando o seu serviço para decidir se vão comprar hoje.

                    #### O Poder das Imagens Reais:
                    Evite fotos de bancos de imagem (internet). O Google rastreia metadados e prioriza imagens tiradas por smartphones reais no local físico.
                    - Tire fotos com luz natural da fachada externa (ajuda o cliente a identificar o local ao chegar).
                    - Fotografe os principais produtos de ângulos atraentes.
                    - Mostre sua equipe trabalhando (humaniza e gera conexão instantânea).
                    
                    #### Categorias de Posts Eficientes no Google Meu Negócio:
                    1. **Ofertas Especiais**: Perfeito para dar descontos rápidas ou combos com data de expiração (ex: "Sexta-feira do Hamburger em Dobro").
                    2. **Novidades**: Fale sobre novos pratos do cardápio, chegada de novos equipamentos ou serviços inéditos.
                    3. **Eventos**: Anuncie eventos locais presenciais que ocorrerão na sua loja física.
                    
                    Guarde essa métrica: Perfis de negócios que atualizam mídias e criam publicações semanais obtêm até **4x mais cliques** de ligação direta do que perfis estáticos!
                """.trimIndent()
            )
        )
    )

    fun getAllArticles(): Flow<List<AcademyArticle>> = articles

    fun getArticleById(id: String): Flow<AcademyArticle?> = articles.map { list ->
        list.firstOrNull { it.id == id }
    }

    fun markAsRead(id: String) {
        val current = articles.value
        val updated = current.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        articles.value = updated
    }
}
