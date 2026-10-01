package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.TokenEntity
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrDarkSurface
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrEmeraldDark
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrGoldDark
import com.example.ui.theme.BgrRed
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary
import com.example.ui.theme.BgrViolet

@Composable
fun PayScreen(
    merchants: List<MerchantEntity>,
    tokens: List<TokenEntity>,
    totalCashbackBgr: Double,
    onPayMerchant: (merchantId: String, tokenSymbol: String, amount: Double, pin: String) -> Unit,
    isLoading: Boolean = false
) {
    var selectedMerchantId by remember { mutableStateOf(merchants.firstOrNull()?.id ?: "MCH-01") }
    var selectedPayToken by remember { mutableStateOf("USDT") }
    var payAmountText by remember { mutableStateOf("45.00") }
    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var showScannerMock by remember { mutableStateOf(false) }

    val activeMerchant = merchants.find { it.id == selectedMerchantId } ?: merchants.firstOrNull()
    val payingToken = tokens.find { it.symbol == selectedPayToken } ?: tokens.firstOrNull()
    val bgrToken = tokens.find { it.symbol == "BGR" } ?: tokens.firstOrNull()

    val payAmount = payAmountText.toDoubleOrNull() ?: 0.0
    val tokenPrice = payingToken?.priceUsd ?: 1.0
    val fiatAmount = payAmount * tokenPrice
    val cashbackRate = activeMerchant?.cashbackRate ?: 5.0
    val cashbackUsd = fiatAmount * (cashbackRate / 100.0)
    val bgrPrice = bgrToken?.priceUsd ?: 0.485
    val estimatedCashbackBgr = if (bgrPrice > 0) cashbackUsd / bgrPrice else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_pay"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Paiements & Cashback",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Réglez vos achats et gagnez du BGR à chaque transaction",
                    fontSize = 12.sp,
                    color = BgrTextSecondary
                )
            }
            IconButton(
                onClick = { showScannerMock = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BgrDarkCard)
                    .border(1.dp, BgrGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scanner QR Commerçant",
                    tint = BgrGold
                )
            }
        }

        // Cumulative Cashback Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Cashback Gagné",
                value = "+${"%.2f".format(totalCashbackBgr)} BGR",
                subtitle = "≈ $${"%.2f".format(totalCashbackBgr * bgrPrice)} économisés",
                highlightColor = BgrEmerald,
                icon = Icons.Default.Paid,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Taux Privilège BGR",
                value = "Jusqu'à 7.5%",
                subtitle = "Rang Or Actif",
                highlightColor = BgrGold,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        // Section: Select Partner Merchant
        Text(
            text = "Commerçants Partenaires Disponibles",
            color = BgrTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(merchants) { merchant ->
                val isSelected = merchant.id == selectedMerchantId
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) BgrDarkCard else BgrDarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) BgrGold else BgrBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedMerchantId = merchant.id }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = if (isSelected) BgrGold else BgrTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            BgrBadge(
                                text = "+${merchant.cashbackRate}%",
                                color = BgrEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = merchant.name,
                            color = BgrTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = merchant.city,
                            color = BgrTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Payment Terminal Card
        BgrCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeMerchant?.name ?: "Commerçant",
                            color = BgrTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (activeMerchant?.isVerified == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Vérifié",
                                tint = BgrEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    BgrBadge(
                        text = "Cashback +${cashbackRate}%",
                        color = BgrEmerald
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Token Selector
                Text("Monnaie de paiement", color = BgrTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("USDT", "BGR", "ETH").forEach { sym ->
                        val isSelected = sym == selectedPayToken
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BgrGold.copy(alpha = 0.2f) else BgrDarkBackground)
                                .border(1.dp, if (isSelected) BgrGold else BgrBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedPayToken = sym }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sym,
                                color = if (isSelected) BgrGold else BgrTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = payAmountText,
                    onValueChange = { payAmountText = it },
                    label = { Text("Montant à régler ($selectedPayToken)", color = BgrTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_merchant_pay_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BgrGold,
                        unfocusedBorderColor = BgrBorder,
                        focusedTextColor = BgrTextPrimary,
                        unfocusedTextColor = BgrTextPrimary,
                        focusedContainerColor = BgrDarkBackground,
                        unfocusedContainerColor = BgrDarkBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // LIVE CASHBACK SIMULATOR HIGHLIGHT
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BgrEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    color = BgrEmerald.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BgrEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = null,
                                tint = BgrDarkBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Récompense Immédiate en BGR",
                                color = BgrEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${"%.2f".format(estimatedCashbackBgr)} BGR (~$${"%.2f".format(cashbackUsd)}) crédités",
                                color = BgrTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BgrButton(
                    text = "Payer et recevoir mon Cashback",
                    onClick = { showPinDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    isEmerald = true,
                    enabled = payAmount > 0 && !isLoading,
                    isLoading = isLoading,
                    testTag = "btn_pay_merchant"
                )
            }
        }

        // Financial Analytics Chart
        Text(
            text = "Analyses Financières & Répartition des Dépenses",
            color = BgrTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        BgrCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dépenses Mensuelles par Catégorie",
                    color = BgrTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Custom Visual Bar Representation
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    val w = size.width
                    val h = size.height

                    // 50% Tech, 30% Food, 15% Shopping, 5% Others
                    val techW = w * 0.50f
                    val foodW = w * 0.30f
                    val shopW = w * 0.15f
                    val otherW = w * 0.05f

                    drawRect(color = Color(0xFF6366F1), topLeft = Offset(0f, 0f), size = Size(techW, h))
                    drawRect(color = Color(0xFF10B981), topLeft = Offset(techW, 0f), size = Size(foodW, h))
                    drawRect(color = Color(0xFFFFB800), topLeft = Offset(techW + foodW, 0f), size = Size(shopW, h))
                    drawRect(color = Color(0xFF8B5CF6), topLeft = Offset(techW + foodW + shopW, 0f), size = Size(otherW, h))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryLegendItem(name = "High-Tech (50%)", color = Color(0xFF6366F1))
                    CategoryLegendItem(name = "Alimentation (30%)", color = Color(0xFF10B981))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryLegendItem(name = "Shopping (15%)", color = Color(0xFFFFB800))
                    CategoryLegendItem(name = "Crypto (5%)", color = Color(0xFF8B5CF6))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // -------------------------------------------------------------
    // Security PIN Confirmation Modal
    // -------------------------------------------------------------
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BgrBorder, RoundedCornerShape(20.dp)),
                color = BgrDarkSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BgrGold,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Autorisation Sécurisée",
                        color = BgrTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Confirmez le débit de $payAmountText $selectedPayToken au commerçant ${activeMerchant?.name}.",
                        color = BgrTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { if (it.length <= 4) enteredPin = it },
                        label = { Text("Code PIN (1234 par défaut)", color = BgrTextSecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_payment_pin"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BgrGold,
                            unfocusedBorderColor = BgrBorder,
                            focusedTextColor = BgrTextPrimary,
                            unfocusedTextColor = BgrTextPrimary,
                            focusedContainerColor = BgrDarkCard,
                            unfocusedContainerColor = BgrDarkCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BgrButton(
                        text = "Valider le Paiement",
                        onClick = {
                            activeMerchant?.let { mch ->
                                onPayMerchant(mch.id, selectedPayToken, payAmount, enteredPin)
                            }
                            showPinDialog = false
                            enteredPin = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isEmerald = true,
                        enabled = enteredPin.length >= 4,
                        testTag = "btn_confirm_pin_payment"
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Mock QR Scanner Dialog
    // -------------------------------------------------------------
    if (showScannerMock) {
        Dialog(onDismissRequest = { showScannerMock = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BgrBorder, RoundedCornerShape(20.dp)),
                color = BgrDarkSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Scanner QR Commerçant",
                            color = BgrTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showScannerMock = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BgrTextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .border(2.dp, BgrCyanElectric, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = BgrCyanElectric,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Visez le terminal de caisse marchand BGR Pay pour régler instantanément.",
                        color = BgrTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BgrButton(
                        text = "Simuler Scan : CyberCafé Paris",
                        onClick = {
                            selectedMerchantId = "MCH-01"
                            payAmountText = "18.50"
                            showScannerMock = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryLegendItem(name: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(name, color = BgrTextSecondary, fontSize = 11.sp)
    }
}
