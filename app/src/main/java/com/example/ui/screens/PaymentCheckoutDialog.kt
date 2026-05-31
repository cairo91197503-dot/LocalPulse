package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.BusinessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutDialog(
    plan: String,
    onDismiss: () -> Unit,
    viewModel: BusinessViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("PIX") } // "PIX" or "CARD"
    var paymentLoading by remember { mutableStateOf(false) }
    var paymentLoadingText by remember { mutableStateOf("") }
    var paymentSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val planName = if (plan == "EXPERT_PLUS") "Expert+" else "PRO"
    val planPrice = if (plan == "EXPERT_PLUS") "R$ 19,90" else "R$ 9,90"
    val planPeriod = if (plan == "EXPERT_PLUS") "/ mês" else " parcela única"

    // PIX states
    val pixKey = remember(plan) {
        val rad = (1000..9999).random()
        "00020101021226810014br.gov.bcb.pix2559pix-qr.mercadopago.com/emv/v2/570400005303986540${if (plan == "EXPERT_PLUS") "19.90" else "9.90"}5802BR5924PulsePersonal Ltda6009SAO PAULO62070503***$rad"
    }

    // Credit Card states
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVV by remember { mutableStateOf("") }

    // Brand detection
    val cardBrand = remember(cardNumber) {
        val clean = cardNumber.replace(" ", "")
        when {
            clean.startsWith("4") -> "Visa"
            clean.startsWith("5") -> "MasterCard"
            clean.startsWith("3") -> "Amex"
            clean.startsWith("6") -> "Elo"
            else -> "Desconhecido"
        }
    }

    Dialog(
        onDismissRequest = { if (!paymentLoading && !paymentSuccess) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !paymentLoading && !paymentSuccess,
            dismissOnClickOutside = !paymentLoading && !paymentSuccess,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                if (paymentSuccess) {
                    // Success View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Success Animation Visual
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sucesso",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Assinatura Ativada!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Parabéns! Sua conta agora possui status $planName. Todos os recursos adicionais foram desbloqueados instantaneamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("checkout_success_done_button")
                        ) {
                            Text("Acessar Recursos Premium", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else if (paymentLoading) {
                    // Loader View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 5.dp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = paymentLoadingText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por favor, não feche o aplicativo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    // Checkout Normal Interface
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Finalizar Assinatura",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(onClick = { onDismiss() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar checkout"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Product summary card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Plano $planName",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (plan == "EXPERT_PLUS") "Autopilot inteligente ilimitado" else "Sincronização total ilimitada",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = planPrice,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = planPeriod,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { selectedTab = "PIX" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("payment_pix_tab"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTab == "PIX") MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (selectedTab == "PIX") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PIX instantâneo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { selectedTab = "CARD" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("payment_card_tab"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTab == "CARD") MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (selectedTab == "CARD") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cartão Crédito", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Selected tab layout
                        if (selectedTab == "PIX") {
                            // PIX FLOW
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Escaneie o QR Code ou copie a chave PIX abaixo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // QR Code Drawing Visual
                                Box(
                                    modifier = Modifier
                                        .size(170.dp)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom pixel-grid designed on canvas resembling a real QR Code!
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val pixelSize = size.width / 15f
                                        val strokeWidth = 3f
                                        // Draw 3 square target anchors of real QR Code
                                        // Top-left
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(0f, 0f),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 4, pixelSize * 4)
                                        )
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(pixelSize, pixelSize),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 2, pixelSize * 2)
                                        )
                                        // Top-right
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(size.width - pixelSize * 4, 0f),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 4, pixelSize * 4)
                                        )
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(size.width - pixelSize * 3, pixelSize),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 2, pixelSize * 2)
                                        )
                                        // Bottom-left
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(0f, size.height - pixelSize * 4),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 4, pixelSize * 4)
                                        )
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(pixelSize, size.height - pixelSize * 3),
                                            size = androidx.compose.ui.geometry.Size(pixelSize * 2, pixelSize * 2)
                                        )

                                        // Draw a central logo placeholder
                                        drawCircle(
                                            color = Color(0xFF1E3A5F),
                                            radius = pixelSize * 1.5f,
                                            center = Offset(size.width / 2f, size.height / 2f)
                                        )

                                        // Draw modular pixel grids mimicking QR bits
                                        val randomPoints = listOf(
                                            // coords in (x,y) index 0..14
                                            4 to 1, 5 to 0, 7 to 2, 8 to 0, 9 to 1, 10 to 3,
                                            1 to 5, 2 to 6, 4 to 4, 5 to 5, 6 to 6, 8 to 5, 9 to 7, 10 to 6,
                                            0 to 8, 1 to 9, 3 to 10, 4 to 8, 5 to 9, 7 to 9, 8 to 10, 10 to 9, 13 to 8, 12 to 10,
                                            9 to 12, 11 to 13, 14 to 11, 13 to 14, 10 to 14, 8 to 11,
                                            5 to 11, 6 to 12, 4 to 13, 5 to 14
                                        )
                                        for (pt in randomPoints) {
                                            drawRect(
                                                color = Color.Black,
                                                topLeft = Offset(pt.first * pixelSize, pt.second * pixelSize),
                                                size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize)
                                            )
                                        }
                                    }
                                    // Soft overlay of pix logo
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("pix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Copia e Cola field
                                OutlinedTextField(
                                    value = pixKey,
                                    onValueChange = {},
                                    readOnly = true,
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(pixKey))
                                                Toast.makeText(context, "Código Pix Copiado!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.testTag("pix_copy_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copiar Código Pix",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Button to check pay
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            paymentLoading = true
                                            paymentLoadingText = "Verificando recebimento do Pix..."
                                            delay(1500)
                                            viewModel.setUserPlan(plan)
                                            paymentSuccess = true
                                            paymentLoading = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("confirm_pix_payment_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Confirmar Pagamento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "O sistema compensa automaticamente após a transferência ser concluída no banco.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // CREDIT CARD FLOW
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Beautiful Interactive Card representation
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .padding(bottom = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.secondary
                                                    )
                                                )
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Memory,
                                                    contentDescription = "Chip",
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                
                                                Text(
                                                    text = cardBrand,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }

                                            Text(
                                                text = if (cardNumber.isEmpty()) "•••• •••• •••• ••••" else cardNumber,
                                                color = Color.White,
                                                letterSpacing = 2.sp,
                                                style = MaterialTheme.typography.titleLarge
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "TITULAR",
                                                        fontSize = 9.sp,
                                                        color = Color.White.copy(alpha = 0.6f)
                                                    )
                                                    Text(
                                                        text = if (cardName.isEmpty()) "NOME NO CARTÃO" else cardName.uppercase(),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = "VALIDADE",
                                                        fontSize = 9.sp,
                                                        color = Color.White.copy(alpha = 0.6f)
                                                    )
                                                    Text(
                                                        text = if (cardExpiry.isEmpty()) "MM/YY" else cardExpiry,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Interactive input fields
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { newVal ->
                                        // Remove non-digits and limit length to 16
                                        val digits = newVal.filter { it.isDigit() }
                                        if (digits.length <= 16) {
                                            // Insert space every 4 digits
                                            val formatted = digits.chunked(4).joinToString(" ")
                                            cardNumber = formatted
                                        }
                                    },
                                    label = { Text("Número do Cartão") },
                                    placeholder = { Text("4000 1234 5678 9010") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = cardName,
                                    onValueChange = { if (it.length <= 40) cardName = it },
                                    label = { Text("Nome do Titular (idêntico ao cartão)") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = { newVal ->
                                            val digits = newVal.filter { it.isDigit() }
                                            if (digits.length <= 4) {
                                                // Format as MM/YY
                                                if (digits.length >= 3) {
                                                    cardExpiry = "${digits.substring(0, 2)}/${digits.substring(2)}"
                                                } else {
                                                    cardExpiry = digits
                                                }
                                            }
                                        },
                                        label = { Text("Validade") },
                                        placeholder = { Text("12/30") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    OutlinedTextField(
                                        value = cardCVV,
                                        onValueChange = { newVal ->
                                            val digits = newVal.filter { it.isDigit() }
                                            if (digits.length <= 4) {
                                                cardCVV = digits
                                            }
                                        },
                                        label = { Text("CVV") },
                                        placeholder = { Text("123") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                val isInputValid = remember(cardNumber, cardName, cardExpiry, cardCVV) {
                                    val cleanNum = cardNumber.replace(" ", "")
                                    val hasMinParts = cardName.trim().split(" ").size >= 2
                                    val cleanExpiry = cardExpiry.replace("/", "")
                                    cleanNum.length >= 15 && hasMinParts && cleanExpiry.length == 4 && cardCVV.length >= 3
                                }

                                Button(
                                    onClick = {
                                        if (isInputValid) {
                                            coroutineScope.launch {
                                                paymentLoading = true
                                                paymentLoadingText = "Processando transação e autorizando..."
                                                delay(1600)
                                                viewModel.setUserPlan(plan)
                                                paymentSuccess = true
                                                paymentLoading = false
                                            }
                                        }
                                    },
                                    enabled = isInputValid,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("credit_card_submit_button")
                                ) {
                                    Text("Assinar com Cartão", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                
                                if (!isInputValid) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Preencha todos os campos corretamente para habilitar o pagamento.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
