package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.BusinessViewModel

@Composable
fun SettingsScreen(
    viewModel: BusinessViewModel,
    onShowPrivacyPolicy: () -> Unit
) {
    val businessNameInput by viewModel.businessNameInput.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val alertFrequency by viewModel.alertFrequency.collectAsState()
    val summaryLanguage by viewModel.summaryLanguage.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isSimulationModeActive by viewModel.isSimulationModeActive.collectAsState()

    // Connection states
    val isFacebookConnected by viewModel.isFacebookConnected.collectAsState()
    val isInstagramConnected by viewModel.isInstagramConnected.collectAsState()
    val isWhatsAppConnected by viewModel.isWhatsAppConnected.collectAsState()
    val isTikTokConnected by viewModel.isTikTokConnected.collectAsState()

    // Notification toggles
    val notificationGmb by viewModel.notificationGmb.collectAsState()
    val notificationFacebook by viewModel.notificationFacebook.collectAsState()
    val notificationInstagram by viewModel.notificationInstagram.collectAsState()
    val notificationWhatsApp by viewModel.notificationWhatsApp.collectAsState()
    val notificationTikTok by viewModel.notificationTikTok.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen_container")
    ) {
        if (isOffline) {
            OfflineIndicatorBanner()
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Details Card
        Text(
            text = "Perfil Conectado",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Personal",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = businessNameInput,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("settings_business_name_text")
                    )

                    Text(
                        text = "ID: pulse_personal_usr",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Type Toggle Card (Saves instantly, no layout buttons)
        Text(
            text = "Tipo de Conta",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                setupPreferenceRow(
                    label = "Pessoal",
                    description = "Para criadores de conteúdo, influenciadores e uso pessoal",
                    isSelected = accountType == "PERSONAL",
                    onSelect = { viewModel.setAccountType("PERSONAL") },
                    testTag = "settings_account_type_personal"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "Comercial / Empresa",
                    description = "Para marcas, negócios locais, comércio e corporações",
                    isSelected = accountType == "BUSINESS",
                    onSelect = { viewModel.setAccountType("BUSINESS") },
                    testTag = "settings_account_type_business"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Plano de Assinatura Selector Card
        Text(
            text = "Plano de Assinatura",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                setupPreferenceRow(
                    label = "Versão Gratuita Limitada",
                    description = "Limite de no máximo 2 contas simultâneas, com propagandas",
                    isSelected = userPlan == "FREE",
                    onSelect = { viewModel.setUserPlan("FREE") },
                    testTag = "settings_plan_free"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "Versão PRO – Grátis (Liberado)",
                    description = "Sem propagandas, com sincronização de todas as contas disponíveis",
                    isSelected = userPlan == "PRO",
                    onSelect = { viewModel.setUserPlan("PRO") },
                    testTag = "settings_plan_pro"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "Versão Expert+ – Grátis (Completo, IA e Piloto Automático)",
                    description = "Diferencial de agendar posts, piloto automático e o app faz tudo!",
                    isSelected = userPlan == "EXPERT_PLUS",
                    onSelect = { viewModel.setUserPlan("EXPERT_PLUS") },
                    testTag = "settings_plan_expert"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Multiplatform Integrations Card
        Text(
            text = "Integração Multiplataforma",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Google connection (primary, always active)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Google",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Google/YouTube (Pessoal)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Conectado como $businessNameInput",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    Text(
                        text = "Ativo",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 2. Facebook connection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1877F2).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Facebook",
                                tint = Color(0xFF1877F2),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Facebook (Perfil)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isFacebookConnected) "Conectado como Perfil Pessoal" else "Não integrado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (isFacebookConnected) viewModel.disconnectFacebook()
                            else viewModel.connectFacebook()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFacebookConnected) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFF1877F2),
                            contentColor = if (isFacebookConnected) MaterialTheme.colorScheme.error else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("facebook_integration_toggle_btn")
                    ) {
                        Text(
                            text = if (isFacebookConnected) "Desconectar" else "Conectar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 3. Instagram connection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE1306C).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = "Instagram",
                                tint = Color(0xFFE1306C),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Instagram (Pessoal)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isInstagramConnected) "Conectado como @${businessNameInput.replace(" ", "").lowercase()}" else "Não integrado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (isInstagramConnected) viewModel.disconnectInstagram()
                            else viewModel.connectInstagram()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInstagramConnected) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFFE1306C),
                            contentColor = if (isInstagramConnected) MaterialTheme.colorScheme.error else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("instagram_integration_toggle_btn")
                    ) {
                        Text(
                            text = if (isInstagramConnected) "Desconectar" else "Conectar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 4. WhatsApp (Pessoal - pronto para futuro)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF25D366).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WhatsApp (Pessoal)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Futuro",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            Text(
                                text = if (isWhatsAppConnected) "Mensagens via WhatsApp" else "Não integrado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (isWhatsAppConnected) viewModel.disconnectWhatsApp()
                            else viewModel.connectWhatsApp()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWhatsAppConnected) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFF25D366),
                            contentColor = if (isWhatsAppConnected) MaterialTheme.colorScheme.error else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("whatsapp_integration_toggle_btn")
                    ) {
                        Text(
                            text = if (isWhatsAppConnected) "Remover" else "Configurar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 5. TikTok connection (Personal/Non-commercial)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF010101).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "TikTok",
                                tint = Color(0xFF010101),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TikTok (Pessoal)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isTikTokConnected) "Conectado como @${businessNameInput.replace(" ", "").lowercase()}" else "Não integrado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (isTikTokConnected) viewModel.disconnectTikTok()
                            else viewModel.connectTikTok()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTikTokConnected) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFF010101),
                            contentColor = if (isTikTokConnected) MaterialTheme.colorScheme.error else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("tiktok_integration_toggle_btn")
                    ) {
                        Text(
                            text = if (isTikTokConnected) "Desconectar" else "Conectar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Alerta Frequency Setup
        Text(
            text = "Preferências de Alertas de Reputação",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                setupPreferenceRow(
                    label = "Diário",
                    description = "Verificação de novas notas a cada 24 horas",
                    isSelected = alertFrequency == "diário",
                    onSelect = { viewModel.setAlertFrequency("diário") },
                    testTag = "alert_freq_daily"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "Semanal",
                    description = "Alerta resumido com as notas consolidadas do período",
                    isSelected = alertFrequency == "semanal",
                    onSelect = { viewModel.setAlertFrequency("semanal") },
                    testTag = "alert_freq_weekly"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Multiplatform Notification Channels Toggles
        Text(
            text = "Lembretes de Postagem Multiplataforma",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Google/YouTube Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lembrar no Google/YouTube (Pessoal)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Notificar se ficar mais de 5 dias sem publicar um shorts/conteúdo",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Switch(
                        checked = notificationGmb,
                        onCheckedChange = { viewModel.toggleNotificationGmb() },
                        modifier = Modifier.testTag("notification_toggle_gmb")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 2. Facebook Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lembrar no Facebook Connect",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Alerta para postar na página oficial conectada",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Switch(
                        checked = notificationFacebook,
                        onCheckedChange = { viewModel.toggleNotificationFacebook() },
                        modifier = Modifier.testTag("notification_toggle_fb")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 3. Instagram Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lembrar no Instagram Feed/Stories",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Avisar para manter o engajamento visual ativo",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Switch(
                        checked = notificationInstagram,
                        onCheckedChange = { viewModel.toggleNotificationInstagram() },
                        modifier = Modifier.testTag("notification_toggle_ig")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 4. WhatsApp Reminder (Pessoal)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lembrar no WhatsApp (Pessoal)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Lembrete para manter contato com listas e interações",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Switch(
                        checked = notificationWhatsApp,
                        onCheckedChange = { viewModel.toggleNotificationWhatsApp() },
                        modifier = Modifier.testTag("notification_toggle_wa")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 5. TikTok Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lembrar no TikTok (Pessoal)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Notificar se ficar inativo por muito tempo no perfil TikTok",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Switch(
                        checked = notificationTikTok,
                        onCheckedChange = { viewModel.toggleNotificationTikTok() },
                        modifier = Modifier.testTag("notification_toggle_tt")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary AI Language Preferred
        Text(
            text = "Idioma dos Resumos de IA",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                setupPreferenceRow(
                    label = "Português (pt-BR)",
                    description = "Padrão de análise local do Brasil",
                    isSelected = summaryLanguage == "pt-BR",
                    onSelect = { viewModel.setSummaryLanguage("pt-BR") },
                    testTag = "lang_pt_br"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "Español (es)",
                    description = "Análisis e respuestas en español",
                    isSelected = summaryLanguage == "es",
                    onSelect = { viewModel.setSummaryLanguage("es") },
                    testTag = "lang_es"
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                setupPreferenceRow(
                    label = "English (en)",
                    description = "Analysis and auto replying in english",
                    isSelected = summaryLanguage == "en",
                    onSelect = { viewModel.setSummaryLanguage("en") },
                    testTag = "lang_en"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ajuda e Aprendizado
        Text(
            text = "Ajuda e Aprendizado",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.triggerTutorial() }
                        .padding(vertical = 4.dp)
                        .testTag("settings_relaunch_tutorial_row"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tutorial do Aplicativo",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Reveja o guia passo a passo explicativo das principais ferramentas do app.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "Ver Tutorial",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Testing sandbox operations
        Text(
            text = "Área de testes do AI Studio",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Simulation Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Simulação (Dados Simulados)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ative para preencher gráficos, feedbacks e sugestões com dados de demonstração. Desative para exibir apenas a realidade do seu canal.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Switch(
                        checked = isSimulationModeActive,
                        onCheckedChange = { viewModel.setSimulationMode(it) },
                        modifier = Modifier.testTag("simulation_mode_toggle_switch")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Sem Conexão / Offline",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Habilite para testar a interface de cache local sem internet",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Switch(
                        checked = isOffline,
                        onCheckedChange = { viewModel.toggleOffline() },
                        modifier = Modifier.testTag("offline_toggle_switch")
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Privacy Policy row click
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowPrivacyPolicy() }
                        .padding(vertical = 4.dp)
                        .testTag("privacy_policy_settings_row"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Política de Privacidade",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Confira nossos termos de segurança de dados e API",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = "Visualizar Política",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sair Action Button
        Button(
            onClick = { viewModel.handleLogout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("logout_pulse_action_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sair")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desconectar Conta (Sair)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun setupPreferenceRow(
    label: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
    }
}
