package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrGoldDark
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary

@Composable
fun OnboardingScreen(
    onCreateWallet: (pin: String) -> Unit,
    onImportWallet: (seed: String, pin: String) -> Unit,
    onComplete: () -> Unit
) {
    var mode by remember { mutableStateOf<String>("WELCOME") } // WELCOME, CREATE_SEED, IMPORT, ENTER_PIN
    var pin by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var seedInput by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val sampleSeed = "alpha shield orbit velvet cyber quantum pulse matrix beacon vault galaxy neon"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (mode) {
            "WELCOME" -> {
                // Top App Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_bgr_app_logo),
                    contentDescription = "BGR Pay Logo",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BGR PAY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = BgrGold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "L'Écosystème Web3 Fintech & Cashback",
                    fontSize = 14.sp,
                    color = BgrTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Illustration
                BgrCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_bgr_hero),
                        contentDescription = "Web3 Fintech Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Value propositions
                FeatureRow(
                    icon = Icons.Default.Paid,
                    title = "Cashback Garanti en \$BGR",
                    subtitle = "Jusqu'à 7.5% de cashback instantané sur tous vos achats marchands."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(
                    icon = Icons.Default.CurrencyExchange,
                    title = "DEX Swaps Ultra-Rapides",
                    subtitle = "Échangez BGR, ETH, USDT, BTC et SOL sans intermédiaire."
                )
                Spacer(modifier = Modifier.height(12.dp))
                FeatureRow(
                    icon = Icons.Default.Bolt,
                    title = "Tap-to-Mine Interactif",
                    subtitle = "Minez des tokens BGR gratuits chaque jour directement depuis l'app."
                )

                Spacer(modifier = Modifier.height(32.dp))

                BgrButton(
                    text = "Créer un nouveau portefeuille",
                    onClick = { mode = "CREATE_SEED" },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.AccountBalanceWallet,
                    testTag = "btn_create_wallet"
                )

                Spacer(modifier = Modifier.height(12.dp))

                BgrButton(
                    text = "Importer via Seed Phrase",
                    onClick = { mode = "IMPORT" },
                    modifier = Modifier.fillMaxWidth(),
                    isSecondary = true,
                    icon = Icons.Default.Key,
                    testTag = "btn_import_wallet"
                )
            }

            "CREATE_SEED" -> {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BgrGold,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Votre Phrase de Récupération",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Notez ces 12 mots dans un endroit sécurisé. Ils permettent de restaurer vos fonds.",
                    fontSize = 13.sp,
                    color = BgrTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                BgrCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val words = sampleSeed.split(" ")
                        words.chunked(3).forEachIndexed { rowIndex, chunk ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                chunk.forEachIndexed { colIndex, word ->
                                    val index = rowIndex * 3 + colIndex + 1
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BgrDarkBackground)
                                            .border(1.dp, BgrBorder, RoundedCornerShape(8.dp))
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$index. $word",
                                            color = BgrGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(sampleSeed))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = BgrTextSecondary
                                )
                            }
                            Text(
                                text = "Copier la phrase dans le presse-papier",
                                color = BgrTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                BgrButton(
                    text = "J'ai sauvegardé ma phrase ➔",
                    onClick = { mode = "ENTER_PIN" },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_seed_confirmed"
                )

                Spacer(modifier = Modifier.height(12.dp))

                BgrButton(
                    text = "Retour",
                    onClick = { mode = "WELCOME" },
                    modifier = Modifier.fillMaxWidth(),
                    isSecondary = true
                )
            }

            "IMPORT" -> {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = BgrGold,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Importer un Portefeuille",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Entrez vos 12 mots séparés par des espaces.",
                    fontSize = 13.sp,
                    color = BgrTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = seedInput,
                    onValueChange = { seedInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("input_seed_phrase"),
                    placeholder = {
                        Text(
                            "alpha shield orbit velvet cyber quantum pulse matrix beacon vault galaxy neon",
                            color = BgrTextMuted,
                            fontSize = 13.sp
                        )
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

                Spacer(modifier = Modifier.height(12.dp))

                BgrButton(
                    text = "Remplir avec la seed de démo",
                    onClick = { seedInput = sampleSeed },
                    modifier = Modifier.fillMaxWidth(),
                    isSecondary = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                BgrButton(
                    text = "Continuer",
                    onClick = { mode = "ENTER_PIN" },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = seedInput.trim().split("\\s+".toRegex()).size >= 12,
                    testTag = "btn_continue_import"
                )

                Spacer(modifier = Modifier.height(12.dp))

                BgrButton(
                    text = "Retour",
                    onClick = { mode = "WELCOME" },
                    modifier = Modifier.fillMaxWidth(),
                    isSecondary = true
                )
            }

            "ENTER_PIN" -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BgrEmerald,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Définir votre Code PIN",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Ce code à 4 chiffres sécurisera vos transactions et paiements marchands.",
                    fontSize = 13.sp,
                    color = BgrTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("Code PIN (4 chiffres)", color = BgrTextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_pin"),
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pinConfirm,
                    onValueChange = { if (it.length <= 4) pinConfirm = it },
                    label = { Text("Confirmer le Code PIN", color = BgrTextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_pin_confirm"),
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

                Spacer(modifier = Modifier.height(24.dp))

                val isPinValid = pin.length == 4 && pin == pinConfirm

                BgrButton(
                    text = "Finaliser & Accéder au Portefeuille",
                    onClick = {
                        if (seedInput.isNotBlank()) {
                            onImportWallet(seedInput, pin)
                        } else {
                            onCreateWallet(pin)
                        }
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isPinValid,
                    isEmerald = true,
                    testTag = "btn_finalize_pin"
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BgrDarkCard)
                .border(1.dp, BgrBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BgrGold,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BgrTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = BgrTextSecondary
            )
        }
    }
}
