package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TokenEntity
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.components.QrCodeView
import com.example.ui.components.TokenLogo
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrBorderLight
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrDarkSurface
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrGoldDark
import com.example.ui.theme.BgrRed
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary
import com.example.ui.viewmodel.BgrUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    state: BgrUiState,
    onNavigateToSwap: () -> Unit,
    onNavigateToPay: () -> Unit,
    onNavigateToMining: () -> Unit,
    onSendCrypto: (address: String, symbol: String, amount: Double) -> Unit
) {
    var showSendDialog by remember { mutableStateOf(false) }
    var showReceiveDialog by remember { mutableStateOf(false) }
    var selectedSendToken by remember { mutableStateOf("USDT") }
    var sendRecipientAddress by remember { mutableStateOf("") }
    var sendAmountText by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current
    val walletAddress = state.userWallet?.walletAddress ?: "0x7F2e89d1C34B8c42aA4E819b5832a8A8D59E7C01"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("screen_wallet"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Portfolio Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(BgrGold.copy(alpha = 0.6f), BgrBorder)),
                        RoundedCornerShape(22.dp)
                    ),
                color = BgrDarkCard,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BgrEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BGR Chain Mainnet",
                                color = BgrTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        BgrBadge(
                            text = "Cashback: +${"%.1f".format(state.totalCashbackBgr)} BGR",
                            color = BgrEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Solde Total Portefeuille",
                        color = BgrTextMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$${"%,.2f".format(state.totalPortfolioUsd)}",
                        color = BgrTextPrimary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BgrEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = BgrEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+$184.20 (+5.8% 24h)",
                                    color = BgrEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Address Chip with copy
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BgrDarkBackground)
                            .border(1.dp, BgrBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(walletAddress))
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${walletAddress.take(8)}...${walletAddress.takeLast(6)}",
                            color = BgrTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copier l'adresse",
                            tint = BgrGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "Envoyer",
                    color = BgrGold,
                    onClick = { showSendDialog = true },
                    testTag = "action_send"
                )
                ActionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "Recevoir",
                    color = BgrCyanElectric,
                    onClick = { showReceiveDialog = true },
                    testTag = "action_receive"
                )
                ActionButton(
                    icon = Icons.Default.CurrencyExchange,
                    label = "Swapper",
                    color = Color(0xFFA78BFA),
                    onClick = onNavigateToSwap,
                    testTag = "action_swap"
                )
                ActionButton(
                    icon = Icons.Default.Paid,
                    label = "Payer & CB",
                    color = BgrEmerald,
                    onClick = onNavigateToPay,
                    testTag = "action_pay"
                )
            }
        }

        // Tap-to-Mine Banner Widget
        item {
            BgrCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMining() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(BgrGold, BgrGoldDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = BgrDarkBackground,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tap-to-Mine BGR",
                                color = BgrTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            BgrBadge(
                                text = "Niv. ${state.miningState?.minerLevel ?: 1}",
                                color = BgrGold
                            )
                        }
                        Text(
                            text = "Gains en attente : +${"%.2f".format(state.miningState?.unclaimedBgr ?: 0.0)} BGR",
                            color = BgrEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = BgrTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actifs & Tokens",
                    color = BgrTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.tokens.size} Actifs",
                    color = BgrTextMuted,
                    fontSize = 13.sp
                )
            }
        }

        // Tokens List
        items(state.tokens) { token ->
            TokenCard(token = token)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // -------------------------------------------------------------
    // Send Crypto Dialog
    // -------------------------------------------------------------
    if (showSendDialog) {
        Dialog(onDismissRequest = { showSendDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BgrBorder, RoundedCornerShape(20.dp)),
                color = BgrDarkSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Envoyer des Crypto",
                            color = BgrTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showSendDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = BgrTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Sélectionner l'actif", color = BgrTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.tokens.take(4).forEach { token ->
                            val isSelected = token.symbol == selectedSendToken
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BgrGold.copy(alpha = 0.2f) else BgrDarkCard)
                                    .border(
                                        1.dp,
                                        if (isSelected) BgrGold else BgrBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSendToken = token.symbol }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = token.symbol,
                                    color = if (isSelected) BgrGold else BgrTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val selectedTokenEntity = state.tokens.find { it.symbol == selectedSendToken }

                    OutlinedTextField(
                        value = sendRecipientAddress,
                        onValueChange = { sendRecipientAddress = it },
                        label = { Text("Adresse destinataire (0x...)", color = BgrTextSecondary) },
                        placeholder = { Text("0x1234...abcd", color = BgrTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_send_address"),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sendAmountText,
                        onValueChange = { sendAmountText = it },
                        label = { Text("Montant ($selectedSendToken)", color = BgrTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_send_amount"),
                        trailingIcon = {
                            TextButton(onClick = {
                                sendAmountText = selectedTokenEntity?.balance?.toString() ?: "0"
                            }) {
                                Text("MAX", color = BgrGold, fontWeight = FontWeight.Bold)
                            }
                        },
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

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Solde dispo : ${selectedTokenEntity?.balance ?: 0.0} $selectedSendToken",
                        color = BgrTextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gas Fee estimation preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgrDarkCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Frais de réseau estimés", color = BgrTextSecondary, fontSize = 12.sp)
                        Text("0.0002 ETH (~$0.69)", color = BgrTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    BgrButton(
                        text = "Confirmer l'envoi",
                        onClick = {
                            val amt = sendAmountText.toDoubleOrNull() ?: 0.0
                            onSendCrypto(sendRecipientAddress, selectedSendToken, amt)
                            showSendDialog = false
                            sendAmountText = ""
                            sendRecipientAddress = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = sendRecipientAddress.length >= 8 && (sendAmountText.toDoubleOrNull() ?: 0.0) > 0,
                        testTag = "btn_confirm_send"
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Receive Modal (QR Code)
    // -------------------------------------------------------------
    if (showReceiveDialog) {
        Dialog(onDismissRequest = { showReceiveDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, BgrBorder, RoundedCornerShape(22.dp)),
                color = BgrDarkSurface,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recevoir des Tokens",
                            color = BgrTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showReceiveDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = BgrTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    QrCodeView(
                        dataString = walletAddress,
                        sizeDp = 200.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Adresse Publique BGR Chain",
                        color = BgrTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BgrDarkCard)
                            .border(1.dp, BgrBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = walletAddress,
                            color = BgrTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BgrButton(
                        text = "Copier l'Adresse",
                        onClick = {
                            clipboardManager.setText(AnnotatedString(walletAddress))
                            showReceiveDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.ContentCopy,
                        testTag = "btn_copy_receive_address"
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.16f))
                .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = BgrTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TokenCard(token: TokenEntity) {
    val totalVal = token.balance * token.priceUsd
    val isPositive = token.change24h >= 0

    BgrCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TokenLogo(symbol = token.symbol, size = 42.dp)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = token.symbol,
                        color = BgrTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = token.network,
                        color = BgrTextMuted,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$${"%,.2f".format(token.priceUsd)}",
                    color = BgrTextSecondary,
                    fontSize = 13.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${"%,.4f".format(token.balance)} ${token.symbol}",
                    color = BgrTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${"%,.2f".format(totalVal)}",
                        color = BgrTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${"%.1f".format(token.change24h)}%",
                        color = if (isPositive) BgrEmerald else BgrRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
