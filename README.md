# LocalPulse 📱✨

> **LocalPulse Studio** | MVP de Gestão de Reputação Local com Inteligência Artificial.

O **LocalPulse** é um aplicativo Android nativo inovador desenvolvido para ajudar proprietários de pequenos e médios negócios a gerenciar a sua presença e reputação online de forma simplificada, ágil e contextualizada utilizando Inteligência Artificial (Gemini API) sobre as avaliações do Google Business Profile.

---

## 🚀 Funcionalidades do MVP

1. **Onboarding Deslizável:** Tutorial em 3 etapas para configuração de perfil e conceitos fundamentais do app.
2. **Login Seguro (Simulado):** Arquitetado sob o padrão Google Sign-In, preparado para autenticação direta.
3. **Resumo de Sentimentos por IA:** Conexão direta com a API do Gemini para gerar análises estratégicas semanais no formato: *"Seus clientes amam X, mas Y% reclamaram de Z"*.
4. **Filtros e Gestão de Avaliações:** Organização por estrelas (positivas, negativas, não respondidas) com cache local resiliente.
5. **Respostas Inteligentes (Smart Replies):** Geração instantânea de sugestões de resposta contextuais e empáticas refinadas com IA.
6. **Criação de Posts (Google Meu Negócio):** Histórico local e criador de postagens promocionais recomendadas pela IA.
7. **Notificação de Alerta de Inatividade:** Janela interativa para alertar o comerciante após 5 dias de inatividade de postagens com sugestão de posts prontos no sistema.
8. **Configurações Completas:** Customização de frequência de alertas, idioma do resumo de sentimento e termos de privacidade.
9. **Visualizador de Política de Privacidade:** Totalmente integrado ao app para total conformidade com a LGPD e regras de consentimento da Google Play Store.
10. **Modo Offline Dinâmico:** Indicador visual de conectividade, cache local via Room Database que previne travamentos e permite funcionamento ininterrupto.

---

## 🛠️ Stack Tecnológica Completa

- **Linguagem:** Kotlin Idiomático (100% moderno)
- **UI & Layout:** Jetpack Compose (100% declarativo) com componentes Material Design 3
- **Arquitetura:** MVVM + Clean Architecture (Data -> Domain/Repository -> Presentation)
- **Banco de Dados Local:** Room Database para persistência e suporte offline resiliente
- **Conectividade & Rede:** Retrofit + OkHttp para comunicação externa
- **Inteligência Artificial:** Integração via REST API com modelos generativos **Gemini** do Google AI Studio
- **Plugins Especiais:** Secrets Gradle Plugin para gestão segura de chaves de API locais via `.env`
- **Suíte de Testes:** JUnit + Robolectric (testes locais na JVM) + Roborazzi (testes de captura e regressão visual automatizados)

---

## 📦 Estrutura de Diretórios e Escopo

```
├── app/src/main/java/com/example/
│   ├── MainActivity.kt               # Ponto de entrada (Main Activity)
│   ├── data/
│   │   ├── api/                      # Clientes de integração (Gemini API)
│   │   ├── db/                       # Room Database (AppDatabase, DAO, Schema)
│   │   ├── models/                   # Estruturas de Dados do domínio
│   │   └── repository/               # Repositórios (semeadura de dados offline)
│   └── ui/
│       ├── MainApp.kt                # Scaffold de Navegação e Diálogos de Alerta/Privacidade
│       ├── screens/                  # Todas as Telas (Onboarding, Home, Reviews, Posts, Settings)
│       └── theme/                    # Design System Material 3 (Cores, Tipografia, Temas)
```

---

## 🗝️ Configurando Credenciais da API do Gemini

Para manter a segurança e evitar dados hardcoded no projeto, a chave da API do Gemini deve ser injetada via **Secrets Gradle Plugin**:

1. Crie um arquivo `.env` na raiz do projeto (ou copie o arquivo de modelo `.env.example`).
2. Adicione sua chave de API obtida no Google AI Studio:
   ```env
   GEMINI_API_KEY=AIzaSy...seu_token_aqui
   ```
3. O plugin irá ler esses valores e disponibilizá-los dinamicamente no código do aplicativo através da classe auto-gerada `BuildConfig.GEMINI_API_KEY`.

---

## 📋 Pré-requisitos para a Google Play Console

Para publicar e carregar com sucesso o LocalPulse na sua conta do desenvolvedor do Google Play Console, certifique-se de que os seguintes requisitos estão configurados:

### 1. Unique Application ID (`applicationId`)
O ID do aplicativo já foi refinado de forma exclusiva nas suas configurações do Gradle:
`applicationId = "com.aistudio.localpulse.hswtxb"`

### 2. Versão do Aplicativo (Incremento)
Controle de versão configurado em `./app/build.gradle.kts`:
- `versionCode = 2`
- `versionName = "1.0.1"`
*Lembre-se de incrementar estes valores a cada nova build enviada ao painel.*

### 3. Permissões de Rede
Declarado a permissão básica de internet em `AndroidManifest.xml` para garantir conexões de rede confiáveis:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🔑 Como Gerar e Assinar o AAB de Produção (Android App Bundle)

O Google Play Console exige que os envios de novos aplicativos utilizem o formato **AAB (Android App Bundle)** devidamente assinado.

### Passo 1: Gerando sua Keystore de Produção
No terminal da raiz do projeto, execute o comando a seguir para gerar a sua chave de assinatura segura (JKS):

```bash
keytool -genkey -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```
*Guarde a senha da Keystore e a senha do Alias anotadas de forma segura.*

### Passo 2: Definindo as Variáveis de Ambiente
Defina as credenciais para o Gradle ler as chaves durante o empacotamento. Crie/exporte no seu ambiente de compilação:

```bash
export KEYSTORE_PATH="/caminho/absoluto/para/my-upload-key.jks"
export STORE_PASSWORD="sua_senha_da_keystore"
export KEY_PASSWORD="sua_senha_do_alias"
```

### Passo 3: Compilando o Bundle de Produção (AAB)
Execute a tarefa do Gradle para gerar o arquivo `.aab` otimizado:

```bash
gradle :app:bundleRelease
```

O arquivo compilado, assinado e pronto para upload será gerado no caminho:
`app/build/outputs/bundle/release/app-release.aab`

---

## 🛡️ Termos & Conformidades Importantes

- **LGPD & GDPR:** O aplicativo possui uma tela e pop-up completo de Política de Privacidade, mapeando a transparência na coleta de feedbacks comerciais e uso de provedores externos como soluções de IA.
- **Modo Offline:** O fluxo resiliente via SQLite impede engasgos e falhas na experiência do usuário sob conexões precárias.
