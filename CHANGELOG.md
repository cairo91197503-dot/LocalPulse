# Changelog 📋

Todas as alterações notáveis, correções e marcos de desenvolvimento do projeto **LocalPulse** serão indexados neste histórico de versões.

---

## [1.0.1] - 2026-05-22
###Adicionado 🚀
- **Políticas e Termos:** Implementação do diálogo interativo brasileiro da Política de Privacidade LGPD acessível nas abas de Entrada e Configurações de forma integrada.
- **Acessibilidade:** Suporte refinado para Contraste do Material Theme 3, tags testTag estruturadas para testes Roborazzi e descrições semânticas adequadas às diretrizes WCAG AA.
- **Permissões de Produção:** Declaração estruturada do filtro de permissão `<uses-permission android:name="android.permission.INTERNET" />` no manifest para canais de API.
- **Modo Offline Dinâmico:** Seeding local de banco de dados via SQLite (Room Database) contendo 50 exemplos de avaliações, análises automáticas e posts iniciais prontos.

###Modificado 🔧
- **UI & UX:** Atualização de ícones obsoletos do Material Icons para variantes auto-mirrored e seguras (`Icons.AutoMirrored.Filled.Send` e `Icons.AutoMirrored.Filled.ExitToApp`).
- **Nomenclatura do Pacote de Produção:** Definição final e exclusiva da propriedade `applicationId` para `com.aistudio.localpulse.hswtxb`.
- **Informações do Aplicativo:** Alinhamento do nome amigável para exibição no sistema e no inicializador (`app_name`) para "LocalPulse" em strings.xml.

###Corrigido 🛡️
- **Robolectric & Roborazzi Visual Testing:** Correção de referências ausentes (ex: `MyApplicationTheme` e `Greeting`) em `GreetingScreenshotTest.kt`, substituídas pela montagem estrutural do `OnboardingSlideContent` sob o tema `LocalPulseTheme`.
- **Erros do InputDispatcher:** Detecção de encerramentos abruptos de canais de diálogo solucionados através de controle seguro de requisições de renderização sob o ciclo estrito do Compose.

---

## [1.0.0] - 2026-05-20 (Internal Release)
- Estruturação inicial do MVP abrangendo Onboarding, Login Simulado, Dashboard de Sentimento por IA, Visualizador de Feedbacks e Gestor de Posts do Google Meu Negócio.
- Acoplagem de biblioteca de Banco de Dados Room, modelos de dados para profiles, posts, reviews e integrações de API de NLP local via Gemini.
