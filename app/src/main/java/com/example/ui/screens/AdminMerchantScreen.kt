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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.entity.MerchantEntity
import com.example.data.model.UserRole
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.components.MetricCard
import com.example.ui.components.QrCodeView
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrDarkSurface
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrRed
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary

@Composable
fun AdminMerchantScreen(
    currentRole: UserRole,
    merchants: List<MerchantEntity>,
    onSwitchRole: (UserRole) -> Unit,
    onToggleVerification: (merchantId: String, currentStatus: Boolean) -> Unit,
    onUpdateCashbackRate: (merchantId: String, newRate: Double) -> Unit
) {
    var posAmountText by remember { mutableStateOf("32.50") }
    var selectedRateToEdit by remember { mutableStateOf<MerchantEntity?>(null) }
    var newRateInput by remember { mutableStateOf("5.0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_admin_merchant"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Role Switcher Bar
        Text(
            text = "Espace Professionnel & Administration",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BgrTextPrimary
        )
        Text(
            text = "Basculez entre vos profils Particulier, Commerçant POS et Super Admin.",
            fontSize = 12.sp,
            color = BgrTextSecondary
        )

        // Segmented Role Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BgrDarkCard)
                .border(1.dp, BgrBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RoleTabItem(
                title = "Client",
                icon = Icons.Default.Person,
                isSelected = currentRole == UserRole.USER,
                onClick = { onSwitchRole(UserRole.USER) },
                modifier = Modifier.weight(1f),
                testTag = "role_tab_user"
            )
            RoleTabItem(
                title = "Commerçant",
                icon = Icons.Default.PointOfSale,
                isSelected = currentRole == UserRole.MERCHANT,
                onClick = { onSwitchRole(UserRole.MERCHANT) },
                modifier = Modifier.weight(1f),
                testTag = "role_tab_merchant"
            )
            RoleTabItem(
                title = "Admin BGR",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = currentRole == UserRole.ADMIN,
                onClick = { onSwitchRole(UserRole.ADMIN) },
                modifier = Modifier.weight(1f),
                testTag = "role_tab_admin"
            )
        }

        when (currentRole) {
            UserRole.USER -> {
                BgrCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BgrGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Mode Utilisateur Particulier Actif",
                            color = BgrTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Vous profitez actuellement du portefeuille personnel, des swaps et du cashback marchand de 5% à 7.5%.",
                            color = BgrTextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BgrButton(
                            text = "Activer le Terminal Marchand POS ➔",
                            onClick = { onSwitchRole(UserRole.MERCHANT) },
                            modifier = Modifier.fillMaxWidth(),
                            isSecondary = true
                        )
                    }
                }
            }

            UserRole.MERCHANT -> {
                // MERCHANT POS PORTAL
                Text(
                    text = "Terminal d'Encaissement Marchand (POS)",
                    color = BgrTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Volume Encaissé (Mois)",
                        value = "$48,500",
                        subtitle = "+14% vs M-1",
                        highlightColor = BgrEmerald,
                        icon = Icons.Default.PointOfSale,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Clients Fidélisés",
                        value = "312",
                        subtitle = "Taux retour 68%",
                        highlightColor = BgrCyanElectric,
                        icon = Icons.Default.Storefront,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dynamic POS Invoice Generator
                BgrCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Facturer un Client en Caisse",
                            color = BgrTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = posAmountText,
                            onValueChange = { posAmountText = it },
                            label = { Text("Montant de la note ($)", color = BgrTextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pos_amount"),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        val invoiceString = "bgrpay://invoice?amount=$posAmountText&merchant=CyberCafeParis&cashback=5"
                        QrCodeView(dataString = invoiceString, sizeDp = 180.dp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Faites scanner ce QR par le client pour encaisser",
                            color = BgrTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            UserRole.ADMIN -> {
                // SUPER ADMIN PORTAL
                Text(
                    text = "Tableau de Bord Administrateur BGR",
                    color = BgrTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Volume Global Traité",
                        value = "$2.4M",
                        subtitle = "5 Partenaires Actifs",
                        highlightColor = BgrGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Cashback BGR Emis",
                        value = "124,500",
                        subtitle = "≈ $60.3K USD",
                        highlightColor = BgrEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Gestion & Vérification des Commerçants",
                    color = BgrTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                // List of merchants with quick action toggles
                merchants.forEach { mch ->
                    BgrCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mch.name,
                                        color = BgrTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (mch.isVerified) {
                                        BgrBadge(text = "Vérifié", color = BgrEmerald)
                                    } else {
                                        BgrBadge(text = "En attente", color = BgrGold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${mch.city} • Vol: $${"%,.0f".format(mch.totalVolumeUsd)}",
                                    color = BgrTextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Cashback : ${mch.cashbackRate}%",
                                    color = BgrEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Rate edit button
                            IconButton(
                                onClick = {
                                    selectedRateToEdit = mch
                                    newRateInput = mch.cashbackRate.toString()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Changer Taux",
                                    tint = BgrGold
                                )
                            }

                            // Toggle Verification switch
                            Switch(
                                checked = mch.isVerified,
                                onCheckedChange = {
                                    onToggleVerification(mch.id, mch.isVerified)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BgrDarkBackground,
                                    checkedTrackColor = BgrEmerald,
                                    uncheckedThumbColor = BgrTextMuted,
                                    uncheckedTrackColor = BgrDarkCard
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Rate Edit Modal
    selectedRateToEdit?.let { merchant ->
        AlertDialog(
            onDismissRequest = { selectedRateToEdit = null },
            title = {
                Text("Modifier Taux Cashback", color = BgrTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Commerçant : ${merchant.name}",
                        color = BgrTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newRateInput,
                        onValueChange = { newRateInput = it },
                        label = { Text("Taux en % (ex: 6.5)", color = BgrTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BgrTextPrimary,
                            unfocusedTextColor = BgrTextPrimary,
                            focusedBorderColor = BgrGold
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val rate = newRateInput.toDoubleOrNull() ?: merchant.cashbackRate
                        onUpdateCashbackRate(merchant.id, rate)
                        selectedRateToEdit = null
                    }
                ) {
                    Text("Enregistrer", color = BgrGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRateToEdit = null }) {
                    Text("Annuler", color = BgrTextSecondary)
                }
            },
            containerColor = BgrDarkSurface
        )
    }
}

@Composable
private fun RoleTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BgrGold else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) BgrDarkBackground else BgrTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) BgrDarkBackground else BgrTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
