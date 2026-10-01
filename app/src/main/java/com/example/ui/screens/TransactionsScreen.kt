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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    selectedFilter: String,
    searchQuery: String,
    onFilterChange: (String) -> Unit,
    onSearchChange: (String) -> Unit
) {
    var selectedTx by remember { mutableStateOf<TransactionEntity?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val filters = listOf(
        Pair("ALL", "Tous"),
        Pair(TransactionType.PAYMENT_MERCHANT.name, "Paiements"),
        Pair(TransactionType.SWAP.name, "Swaps"),
        Pair(TransactionType.MINING_REWARD.name, "Minage"),
        Pair(TransactionType.SEND.name, "Envois"),
        Pair(TransactionType.RECEIVE.name, "Réceptions")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("screen_transactions")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Historique des Transactions",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BgrTextPrimary
        )
        Text(
            text = "Suivi complet de vos paiements, swaps et cashback",
            fontSize = 12.sp,
            color = BgrTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Rechercher un commerçant, swap ou adresse...", color = BgrTextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = BgrTextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_transactions"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BgrGold,
                unfocusedBorderColor = BgrBorder,
                focusedTextColor = BgrTextPrimary,
                unfocusedTextColor = BgrTextPrimary,
                focusedContainerColor = BgrDarkCard,
                unfocusedContainerColor = BgrDarkCard
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { (key, label) ->
                val isSelected = selectedFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BgrGold else BgrDarkCard)
                        .border(1.dp, if (isSelected) BgrGold else BgrBorder, RoundedCornerShape(8.dp))
                        .clickable { onFilterChange(key) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BgrDarkBackground else BgrTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transaction List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucune transaction correspondante.",
                    color = BgrTextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transactions) { tx ->
                    TransactionRow(
                        tx = tx,
                        onClick = { selectedTx = tx }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Transaction Detail Sheet / Dialog
    // -------------------------------------------------------------
    selectedTx?.let { tx ->
        Dialog(onDismissRequest = { selectedTx = null }) {
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
                            text = "Détail de la Transaction",
                            color = BgrTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { selectedTx = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = BgrTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = tx.title, color = BgrTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy • HH:mm:ss", Locale.FRENCH).format(Date(tx.timestamp)),
                        color = BgrTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BgrCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            DetailRow("Montant", "${tx.amount} ${tx.symbol}")
                            DetailRow("Valeur Fiat", "$${"%.2f".format(tx.fiatAmount)} USD")
                            if (tx.cashbackBgr > 0) {
                                DetailRow("Cashback BGR Récolté", "+${"%.2f".format(tx.cashbackBgr)} BGR", BgrEmerald)
                            }
                            DetailRow("Statut Réseau", tx.status, if (tx.status == "CONFIRMED") BgrEmerald else BgrGold)
                            DetailRow("Catégorie", tx.category)
                            DetailRow("Contrepartie", "${tx.counterparty.take(8)}...${tx.counterparty.takeLast(6)}")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Hash Blockchain (TxID)", color = BgrTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgrDarkCard)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${tx.txHash.take(16)}...${tx.txHash.takeLast(8)}",
                            color = BgrTextPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(tx.txHash))
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = BgrGold, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BgrButton(
                        text = "Fermer",
                        onClick = { selectedTx = null },
                        modifier = Modifier.fillMaxWidth(),
                        isSecondary = true
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    tx: TransactionEntity,
    onClick: () -> Unit
) {
    val (icon, iconColor) = when (tx.type) {
        TransactionType.PAYMENT_MERCHANT.name -> Pair(Icons.Default.Storefront, BgrEmerald)
        TransactionType.SWAP.name -> Pair(Icons.Default.CurrencyExchange, Color(0xFFA78BFA))
        TransactionType.MINING_REWARD.name -> Pair(Icons.Default.Bolt, BgrGold)
        TransactionType.SEND.name -> Pair(Icons.Default.ArrowUpward, BgrRed)
        TransactionType.RECEIVE.name -> Pair(Icons.Default.ArrowDownward, BgrCyanElectric)
        else -> Pair(Icons.Default.Storefront, BgrGold)
    }

    val isPositive = tx.type in listOf(TransactionType.RECEIVE.name, TransactionType.MINING_REWARD.name)
    val formattedDate = SimpleDateFormat("dd MMM, HH:mm", Locale.FRENCH).format(Date(tx.timestamp))

    BgrCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    color = BgrTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        color = BgrTextMuted,
                        fontSize = 11.sp
                    )
                    if (tx.cashbackBgr > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        BgrBadge(
                            text = "+${"%.2f".format(tx.cashbackBgr)} BGR",
                            color = BgrEmerald
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isPositive) "+" else "-"}${tx.amount} ${tx.symbol}",
                    color = if (isPositive) BgrEmerald else BgrTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$${"%.2f".format(tx.fiatAmount)}",
                    color = BgrTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = BgrTextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = BgrTextSecondary, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
