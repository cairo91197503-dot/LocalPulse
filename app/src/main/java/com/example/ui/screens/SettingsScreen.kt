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
    val alertFrequency by viewModel.alertFrequency.collectAsState()
    val summaryLanguage by viewModel.summaryLanguage.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

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
            text = "Negócio Conectado",
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
                        imageVector = Icons.Default.BusinessCenter,
                        contentDescription = "Business",
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
                        text = "ID: gmb_pub_47819203",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    )
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
