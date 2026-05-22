# Política de Privacidade — LocalPulse 🛡️

**Última atualização:** 22 de maio de 2026.

Esta Política de Privacidade descreve as práticas adotadas pelo **LocalPulse** com relação à coleta, uso, processamento e proteção das suas informações pessoais e dados comerciais de seu estabelecimento no contexto do aplicativo para dispositivos móveis Android.

Ao utilizar o aplicativo LocalPulse, você concorda com os termos estabelecidos a seguir em plena conformidade com a Lei Geral de Proteção de Dados Pessoais (LGPD - Lei nº 13.709/2018) no Brasil e as diretrizes regulatórias estipuladas pela Google Play Store.

---

## 1. Coleta e Uso de Informações

### A. Autenticação e Credenciais
O **LocalPulse** utiliza serviços de login baseados no ecossistema Google para autenticação. 
- **O que fazemos:** Facilitamos uma experiência de acesso sob demanda baseada nos padrões Google OAuth / Firebase Auth para que a conexão seja feita diretamente junto aos servidores do Google de forma protegida.
- **O que não fazemos:** O aplicativo não coleta, não processa e não arquiva quaisquer senhas ou acessos proprietários do usuário em infraestruturas privadas do aplicativo.

### B. Integração Google Business Profile API
Ao consentir com o escopo de permissão `'business.manage'`, o LocalPulse realiza consultas seguras estritamente para:
- Listar as avaliações do seu estabelecimento escritas de forma pública por clientes.
- Exibir nome, nota de estrelas e fotos do feedback recebido.
- Permitir a publicação de respostas diretas às avaliações cadastrando-as no perfil da sua loja.
- Criar e gerenciar ofertas e atualizações de postagens públicas no Google Meu Negócio.

### C. Inteligência Artificial (Google Gemini API / AI Studio)
Para as funcionalidades geradoras ("Análise de Sentimento" e "Sugerir Respostas com IA"), trechos públicos de avaliações (como texto da crítica, nome visível do autor e classificação) são enviados à infraestrutura segura da **API do Gemini** hospedada em servidores do ecossistema Google.
- **Nenhum** dado pessoal confidencial do empresário, dados financeiros ou chaves privadas do negócio são transmitidos a serviços de inteligência artificial generativa.

### D. Armazenamento Local e Cache Offline (Room Database)
A fim de fornecer um funcionamento ágil e confiável mesmo sob conexões desfavoráveis de rede, as seguintes informações são arquivadas de forma local, interna e isolada utilizando o banco de dados Android Room:
- Lista das avaliações do estabelecimento.
- Rascunhos de postagens semanais sugeridos e novos posts criados.
- Relatório consolidado do sentimento semanal gerado por IA.

Você poderá apagar voluntariamente esse cache de forma integral em seu celular no menu de preferências do sistema Android acessando `Configurações > Aplicativos > LocalPulse > Armazenamento > Limpar Dados`.

---

## 2. Compartilhamento de Informações com Terceiros

Não realizamos a comercialização, aluguel ou troca de informações do seu negócio ou de seus clientes locais com ad networks ou prestadores de serviços secundários. As transferências necessárias ocorrem estritamente com os seguintes provedores homologados do ecossistema Google para assegurar o funcionamento dos recursos:
1. **Google AI Studio (Gemini NLP API):** Envio anônimo de trechos de avaliações para refinar as sugestões de respostas.
2. **Google Business Service Platform:** Postagem autenticada de notas informativas e respostas locais solicitadas de forma voluntária pelo comerciante.

---

## 3. Segurança de Dados

Todas as requisições enviadas e recebidas pelo LocalPulse utilizam canais criptografados de transmissão **HTTPS (SSL/TLS)** que Blindam o tráfego de dados sensíveis na internet de acessos externos ilícitos.

---

## 4. Seus Direitos de Privacidade

Como usuário e controlador do seu negócio, você mantém plena soberania sobre as interações do LocalPulse:
- **Revogação de Acesso:** Você pode desvincular o aplicativo do seu perfil comercial a qualquer instante alterando as permissões na barra de Conta de Usuário do Google ou deslogando do aplicativo no painel de configurações.
- **Esquecimento:** Toda preferência e cache são removidos do dispositivo físico instantaneamente se você apagar o armazenamento interno do aplicativo nas configurações nativas do Android.

---

## 5. Alterações Nesta Política

Reservamos o direito de atualizar esta Política de Privacidade de forma periódica à medida que adicionamos novas integrações comerciais ao LocalPulse. Recomendamos a visita frequente a esta página para manter-se a par de eventuais alterações.

---

## 6. Canais de Contato

Para maiores esclarecimentos fiscais, operacionais ou dúvidas relativas aos procedimentos de privacidade adotados por nossa engenharia, envie uma mensagem ao nosso suporte técnico:

- **E-mail de Contato:** cairo91197503@gmail.com
