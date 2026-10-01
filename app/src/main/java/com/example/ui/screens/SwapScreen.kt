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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TokenEntity
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.components.TokenLogo
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrDarkSurface
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary
import com.example.ui.theme.BgrViolet

@Composable
fun SwapScreen(
    tokens: List<TokenEntity>,
    onExecuteSwap: (from: String, to: String, amount: Double, slippage: Double) -> Unit,
    isLoading: Boolean = false
) {
    var fromSymbol by remember { mutableStateOf("USDT") }
    var toSymbol by remember { mutableStateOf("BGR") }
    var amountInText by remember { mutableStateOf("100") }
    var slippagePercent by remember { mutableStateOf(0.5) }

    val fromToken = tokens.find { it.symbol == fromSymbol } ?: tokens.firstOrNull()
    val toToken = tokens.find { it.symbol == toSymbol } ?: tokens.getOrNull(1)

    val amountIn = amountInText.toDoubleOrNull() ?: 0.0
    val fromPrice = fromToken?.priceUsd ?: 1.0
    val toPrice = toToken?.priceUsd ?: 0.485

    // Calculation with 0.3% DEX fee
    val grossUsd = amountIn * fromPrice
    val feeUsd = grossUsd * 0.003
    val netUsd = grossUsd - feeUsd
    val estimatedAmountOut = if (toPrice > 0) netUsd / toPrice else 0.0
    val rate = if (toPrice > 0) fromPrice / toPrice else 0.0
    val minReceived = estimatedAmountOut * (1.0 - (slippagePercent / 100.0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_swap"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEX Instant Swap",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Convertissez vos crypto sans frais cachés",
                    fontSize = 13.sp,
                    color = BgrTextSecondary
                )
            }
            BgrBadge(text = "Uniswap V3 Router", color = BgrCyanElectric)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FROM TOKEN CARD
        SwapTokenBox(
            label = "Vous payez (De)",
            selectedSymbol = fromSymbol,
            tokenEntity = fromToken,
            amountText = amountInText,
            onAmountChange = { amountInText = it },
            availableTokens = tokens,
            onSelectToken = { fromSymbol = it },
            onMaxClick = {
                amountInText = (fromToken?.balance ?: 0.0).toString()
            },
            isEditable = true,
            testTag = "input_swap_from"
        )

        // SWAP FLIP BUTTON
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(BgrDarkCard)
                .border(1.dp, BgrGold, CircleShape)
                .clickable {
                    val temp = fromSymbol
                    fromSymbol = toSymbol
                    toSymbol = temp
                }
                .testTag("btn_flip_swap"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Inverser",
                tint = BgrGold,
                modifier = Modifier.size(24.dp)
            )
        }

        // TO TOKEN CARD
        SwapTokenBox(
            label = "Vous recevez (Estimation)",
            selectedSymbol = toSymbol,
            tokenEntity = toToken,
            amountText = if (amountIn > 0) "%.4f".format(estimatedAmountOut) else "0.0",
            onAmountChange = {},
            availableTokens = tokens,
            onSelectToken = { toSymbol = it },
            onMaxClick = {},
            isEditable = false,
            testTag = "output_swap_to"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Slippage Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tolérance de slippage",
                color = BgrTextSecondary,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0.1, 0.5, 1.0).forEach { slip ->
                    val isSelected = slippagePercent == slip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BgrGold else BgrDarkCard)
                            .border(1.dp, if (isSelected) BgrGold else BgrBorder, RoundedCornerShape(8.dp))
                            .clickable { slippagePercent = slip }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$slip%",
                            color = if (isSelected) BgrDarkBackground else BgrTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Swap Breakdown Card
        BgrCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Taux de change", color = BgrTextSecondary, fontSize = 12.sp)
                    Text("1 $fromSymbol ≈ ${"%.4f".format(rate)} $toSymbol", color = BgrTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Impact sur le prix", color = BgrTextSecondary, fontSize = 12.sp)
                    Text("< 0.02%", color = BgrEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Frais réseau (Gas)", color = BgrTextSecondary, fontSize = 12.sp)
                    Text("0.0003 ETH (~$1.04)", color = BgrTextPrimary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Minimum garanti reçu", color = BgrTextSecondary, fontSize = 12.sp)
                    Text("${"%.4f".format(minReceived)} $toSymbol", color = BgrGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val hasEnoughBalance = (fromToken?.balance ?: 0.0) >= amountIn && amountIn > 0

        BgrButton(
            text = if (!hasEnoughBalance && amountIn > 0) "Solde insuffisant en $fromSymbol" else "Confirmer l'Échange (Swap)",
            onClick = {
                onExecuteSwap(fromSymbol, toSymbol, amountIn, slippagePercent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasEnoughBalance && fromSymbol != toSymbol && !isLoading,
            isLoading = isLoading,
            testTag = "btn_confirm_swap"
        )
    }
}

@Composable
private fun SwapTokenBox(
    label: String,
    selectedSymbol: String,
    tokenEntity: TokenEntity?,
    amountText: String,
    onAmountChange: (String) -> Unit,
    availableTokens: List<TokenEntity>,
    onSelectToken: (String) -> Unit,
    onMaxClick: () -> Unit,
    isEditable: Boolean,
    testTag: String
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    BgrCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, color = BgrTextSecondary, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Solde : ${"%.4f".format(tokenEntity?.balance ?: 0.0)}",
                        color = BgrTextMuted,
                        fontSize = 12.sp
                    )
                    if (isEditable) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MAX",
                            color = BgrGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BgrGold.copy(alpha = 0.15f))
                                .clickable(onClick = onMaxClick)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Token Selector Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgrDarkBackground)
                            .border(1.dp, BgrBorder, RoundedCornerShape(12.dp))
                            .clickable { expandedDropdown = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TokenLogo(symbol = selectedSymbol, size = 26.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedSymbol,
                            color = BgrTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = BgrTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.background(BgrDarkSurface)
                    ) {
                        availableTokens.forEach { token ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        TokenLogo(symbol = token.symbol, size = 22.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(token.symbol, color = BgrTextPrimary, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("($${token.priceUsd})", color = BgrTextMuted, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    onSelectToken(token.symbol)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Amount Text Field
                if (isEditable) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = onAmountChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(testTag),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = BgrTextPrimary,
                            unfocusedTextColor = BgrTextPrimary
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    )
                } else {
                    Text(
                        text = amountText,
                        color = BgrTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(testTag)
                    )
                }
            }
        }
    }
}
