package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoogleGreen
import com.example.ui.viewmodel.BusinessViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: BusinessViewModel,
    onShowPrivacyPolicy: () -> Unit
) {
    val isRegisterMode by viewModel.isRegisterMode.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // Login credentials states
    val loginEmailOrPhoneOrUser by viewModel.loginEmailOrPhoneOrUser.collectAsState()
    val loginPassword by viewModel.loginPassword.collectAsState()

    // Register details states
    val registerEmail by viewModel.registerEmail.collectAsState()
    val registerPhone by viewModel.registerPhone.collectAsState()
    val registerUsername by viewModel.registerUsername.collectAsState()
    val registerPassword by viewModel.registerPassword.collectAsState()
    val registerBusinessName by viewModel.registerBusinessName.collectAsState()

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header Logo
            Image(
                painter = rememberAsyncImagePainter(model = com.example.R.drawable.img_app_logo_1780065100739),
                contentDescription = "Logo PulsePersonal",
                modifier = Modifier
                    .height(65.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Adaptive Card illustration representing the profile kind dynamically!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                contentAlignment = Alignment.Center
            ) {
                if (accountType == "PERSONAL") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Pessoal",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Perfil Pessoal Ativo",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Para criadores, influenciadores e uso pessoal",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Comercial",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Perfil Comercial / Empresa",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                "Para marcas, empresas e negócios locais",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector Tabs (Entrar vs Criar Conta)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isRegisterMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { if (isRegisterMode) viewModel.toggleAuthMode() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Entrar",
                        fontWeight = FontWeight.Bold,
                        color = if (!isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRegisterMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { if (!isRegisterMode) viewModel.toggleAuthMode() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Criar Conta",
                        fontWeight = FontWeight.Bold,
                        color = if (isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Display if present
            if (loginError != null) {
                Text(
                    text = loginError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ACCOUNT TYPE SELECTOR CARDS ("Para você" vs "Para sua empresa")
            Text(
                text = "Selecione o tipo de conta:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Personal Option
                Card(
                    onClick = { viewModel.setAccountType("PERSONAL") },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(82.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (accountType == "PERSONAL") 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (accountType == "PERSONAL") 2.dp else 1.dp,
                        color = if (accountType == "PERSONAL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Pessoal",
                            tint = if (accountType == "PERSONAL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Para Você (Pessoal)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (accountType == "PERSONAL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                // Business Option
                Card(
                    onClick = { viewModel.setAccountType("BUSINESS") },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(82.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (accountType == "BUSINESS") 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (accountType == "BUSINESS") 2.dp else 1.dp,
                        color = if (accountType == "BUSINESS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Comercial / Empresa",
                            tint = if (accountType == "BUSINESS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sua Empresa (Comercial)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (accountType == "BUSINESS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Form Sheet based on Selected Tab
            if (!isRegisterMode) {
                // LOGIN SCREEN FLOW
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = loginEmailOrPhoneOrUser,
                        onValueChange = { viewModel.setLoginEmailOrPhoneOrUser(it) },
                        label = { Text("E-mail, Celular ou Usuário") },
                        placeholder = { Text("Insira suas credenciais") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, "Credenciais") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_credential_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { viewModel.setLoginPassword(it) },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, "Senha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { viewModel.performLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(top = 8.dp)
                            .testTag("login_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Acessar minha conta", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // REGISTRATION / SIGNUP SCREEN FLOW
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = registerUsername,
                        onValueChange = { viewModel.setRegisterUsername(it) },
                        label = { Text("Nome de Usuário") },
                        placeholder = { Text("Ex: dario_pulse") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.PersonOutline, "Usuário") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_user_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = registerEmail,
                        onValueChange = { viewModel.setRegisterEmail(it) },
                        label = { Text("E-mail de Cadastro") },
                        placeholder = { Text("Ex: dario@email.com") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.MailOutline, "E-mail") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = registerPhone,
                        onValueChange = { viewModel.setRegisterPhone(it) },
                        label = { Text("Celular (DDD + Número)") },
                        placeholder = { Text("Ex: 11987654321") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, "Celular") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = registerPassword,
                        onValueChange = { viewModel.setRegisterPassword(it) },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LockOpen, "Senha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Profile Handle or Corporate store name input dynamically styled!
                    val profileLabel = if (accountType == "PERSONAL") "Nome do seu perfil / Identificador" else "Razão Social / Nome da sua Empresa"
                    val profileHint = if (accountType == "PERSONAL") "Ex: @meuperfil" else "Ex: Padaria Bella Vista"

                    OutlinedTextField(
                        value = registerBusinessName,
                        onValueChange = { viewModel.setRegisterBusinessName(it) },
                        label = { Text(profileLabel) },
                        placeholder = { Text(profileHint) },
                        singleLine = true,
                        leadingIcon = { Icon(if (accountType == "PERSONAL") Icons.Default.AccountCircle else Icons.Default.Storefront, "Nome") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_business_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { viewModel.performRegister() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(top = 8.dp)
                            .testTag("register_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Criar minha conta e entrar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Integration divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                Text(
                    text = " ou conecte com redes sociais ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Platforms Login Grid Layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Google Sign In
                Button(
                    onClick = { viewModel.loginWithGoogle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("social_google_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Google", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar com o Google Extendido", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Facebook Sign In
                    Button(
                        onClick = { viewModel.loginWithFacebook() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("social_facebook_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2), contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Facebook", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Facebook", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    }

                    // Instagram Sign In
                    Button(
                        onClick = { viewModel.loginWithInstagram() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("social_instagram_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C), contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.StarBorder, contentDescription = "Instagram", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Instagram", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    }
                }

                // TikTok connection
                Button(
                    onClick = { viewModel.loginWithTikTok() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("social_tiktok_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF010101), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "TikTok", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar com o TikTok (Pessoal)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Privacy Link
            Text(
                text = "Política de Privacidade do PulsePersonal",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .clickable { onShowPrivacyPolicy() }
                    .padding(8.dp)
                    .testTag("privacy_policy_btn")
            )
        }
    }
}

@Composable
fun OnboardingSlideContent(
    step: Int,
    businessNameInput: String,
    onNameChange: (String) -> Unit,
    onShowPrivacyPolicy: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        when (step) {
            0 -> {
                // Slide 1: Welcome
                LocalStoreShowcaseIllustration()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Bem-vindo ao PulsePersonal",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Gerencie sua presença pessoal nas redes sociais de forma inteligente e sem fins comerciais. Acompanhe feedbacks e engajamento com facilidade.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )
            }
            1 -> {
                // Slide 2: Analytics & Sentiment
                WeeklyReportsIllustration()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Acompanhe seu engajamento",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Monitore reações, menções e comentários das suas contas pessoais integradas (TikTok, Instagram, etc) com análises inteligentes semanais via IA.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )
            }
            2 -> {
                // Slide 3: Smart Replying and name configuration
                SmartReplyingIllustration()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Conecte-se com seguidores",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Receba sugestões de respostas inteligentes e ideias criativas de posts de uso não comercial gerados por IA para interagir com sua audiência de forma única.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Personal profile custom identifier
                OutlinedTextField(
                    value = businessNameInput,
                    onValueChange = onNameChange,
                    label = { Text("Nome do seu perfil / Identificador") },
                    singleLine = true,
                    placeholder = { Text("Ex: @cairo.pulse") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("business_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

@Composable
fun LocalStoreShowcaseIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            // Draw a simplified beautiful storefront setup representing a local business
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Shop body
            drawRect(
                color = primaryColor.copy(alpha = 0.2f),
                topLeft = Offset(20.dp.toPx(), 45.dp.toPx()),
                size = Size(80.dp.toPx(), 55.dp.toPx())
            )

            // Door
            drawRect(
                color = primaryColor,
                topLeft = Offset(50.dp.toPx(), 65.dp.toPx()),
                size = Size(20.dp.toPx(), 35.dp.toPx())
            )

            // Window
            drawRect(
                color = accentColor.copy(alpha = 0.4f),
                topLeft = Offset(28.dp.toPx(), 55.dp.toPx()),
                size = Size(15.dp.toPx(), 20.dp.toPx())
            )

            // Roof (Awnings)
            drawRect(
                color = primaryColor,
                topLeft = Offset(12.dp.toPx(), 32.dp.toPx()),
                size = Size(96.dp.toPx(), 15.dp.toPx())
            )

            // Ground base
            drawLine(
                color = primaryColor,
                start = Offset(5.dp.toPx(), 100.dp.toPx()),
                end = Offset(115.dp.toPx(), 100.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )

            // Stars around GMB style
            val starColor = Color(0xFFFFB300)
            drawStar(this, Offset(35.dp.toPx(), 15.dp.toPx()), 8.dp.toPx(), starColor)
            drawStar(this, Offset(60.dp.toPx(), 10.dp.toPx()), 10.dp.toPx(), starColor)
            drawStar(this, Offset(85.dp.toPx(), 15.dp.toPx()), 8.dp.toPx(), starColor)
        }
    }
}

@Composable
fun WeeklyReportsIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Draw a stylish metrics card
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(15.dp.toPx(), 15.dp.toPx()),
                size = Size(90.dp.toPx(), 90.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw progress lines / text representations
            drawLine(
                color = primaryColor,
                start = Offset(25.dp.toPx(), 35.dp.toPx()),
                end = Offset(80.dp.toPx(), 35.dp.toPx()),
                strokeWidth = 5.dp.toPx()
            )

            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(25.dp.toPx(), 48.dp.toPx()),
                end = Offset(65.dp.toPx(), 48.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )

            // Success trend arrow
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(25.dp.toPx(), 85.dp.toPx())
                    lineTo(50.dp.toPx(), 65.dp.toPx())
                    lineTo(65.dp.toPx(), 75.dp.toPx())
                    lineTo(90.dp.toPx(), 55.dp.toPx())
                },
                color = accentColor,
                style = Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Circle endpoint indicator
            drawCircle(
                color = accentColor,
                center = Offset(90.dp.toPx(), 55.dp.toPx()),
                radius = 5.dp.toPx()
            )
        }
    }
}

@Composable
fun SmartReplyingIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val primaryColor = Color(0xFF1E3A5F)
            val accentColor = Color(0xFF34A853)

            // Draw a phone layout or chat bubles
            // Bubble 1 Left (Incoming rating)
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.15f),
                topLeft = Offset(10.dp.toPx(), 20.dp.toPx()),
                size = Size(80.dp.toPx(), 35.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )
            // Stars inside bubble 1
            val starColor = Color(0xFFFFB300)
            drawStar(this, Offset(20.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)
            drawStar(this, Offset(32.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)
            drawStar(this, Offset(44.dp.toPx(), 37.dp.toPx()), 5.dp.toPx(), starColor)

            // Bubble 2 Right (AI suggested response)
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(30.dp.toPx(), 65.dp.toPx()),
                size = Size(80.dp.toPx(), 35.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )

            // AI Sparks icon
            drawCircle(
                color = Color.White,
                center = Offset(45.dp.toPx(), 82.dp.toPx()),
                radius = 4.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(55.dp.toPx(), 82.dp.toPx()),
                end = Offset(95.dp.toPx(), 82.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

// Simple Helper to draw a math-oriented Star shape
fun drawStar(
    scope: androidx.compose.ui.graphics.drawscope.DrawScope,
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = androidx.compose.ui.graphics.Path()
    val numberOfPoints = 5
    val halfPi = Math.PI / 2.0
    val doublePi = Math.PI * 2.0
    val innerRadius = radius * 0.47f // ratio for nice stars

    for (i in 0 until numberOfPoints) {
        val outerAngle = halfPi + i * doublePi / numberOfPoints
        val innerAngle = halfPi + (i + 0.5) * doublePi / numberOfPoints

        val outerX = center.x + radius * Math.cos(outerAngle).toFloat()
        val outerY = center.y - radius * Math.sin(outerAngle).toFloat()
        
        val innerX = center.x + innerRadius * Math.cos(innerAngle).toFloat()
        val innerY = center.y - innerRadius * Math.sin(innerAngle).toFloat()

        if (i == 0) {
            path.moveTo(outerX, outerY)
        } else {
            path.lineTo(outerX, outerY)
        }
        path.lineTo(innerX, innerY)
    }
    path.close()
    scope.drawPath(path = path, color = color)
}
