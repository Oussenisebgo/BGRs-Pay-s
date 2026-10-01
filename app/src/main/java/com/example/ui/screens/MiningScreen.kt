package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.MiningEntity
import com.example.ui.components.BgrBadge
import com.example.ui.components.BgrButton
import com.example.ui.components.BgrCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrEmeraldDark
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrGoldDark
import com.example.ui.theme.BgrGoldLight
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary
import kotlinx.coroutines.launch

@Composable
fun MiningScreen(
    miningState: MiningEntity?,
    onTap: () -> Unit,
    onClaimRewards: () -> Unit,
    onUpgradeMiner: () -> Unit,
    isLoading: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val coinScale = remember { Animatable(1f) }
    var tapCounter by remember { mutableStateOf(0) }

    val currentEnergy = miningState?.currentEnergy ?: 1000
    val maxEnergy = miningState?.maxEnergy ?: 1000
    val energyProgress = if (maxEnergy > 0) currentEnergy.toFloat() / maxEnergy.toFloat() else 1f
    val unclaimed = miningState?.unclaimedBgr ?: 0.0
    val minerLevel = miningState?.minerLevel ?: 1
    val bgrPerTap = (miningState?.bgrPerTap ?: 0.25) * (miningState?.multiplier ?: 1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgrDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("screen_mining"),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = "Tap-to-Mine BGR",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgrTextPrimary
                )
                Text(
                    text = "Touchez la pièce pour miner du token BGR",
                    fontSize = 12.sp,
                    color = BgrTextSecondary
                )
            }
            BgrBadge(text = "Multiplicateur ${miningState?.multiplier ?: 1.0}X", color = BgrGold)
        }

        // Unclaimed Earnings Display Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, BgrGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
            color = BgrDarkCard,
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jetons Minés Non-Réclamés",
                    color = BgrTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+${"%.2f".format(unclaimed)} BGR",
                    color = BgrGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "≈ $${"%.2f".format(unclaimed * 0.485)} USD",
                    color = BgrEmerald,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                BgrButton(
                    text = "Transférer vers le Portefeuille",
                    onClick = onClaimRewards,
                    modifier = Modifier.fillMaxWidth(),
                    isEmerald = true,
                    enabled = unclaimed > 0.0 && !isLoading,
                    isLoading = isLoading,
                    icon = Icons.Default.AccountBalanceWallet,
                    testTag = "btn_claim_mining"
                )
            }
        }

        // Tap Power & Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Gain par Tap",
                value = "+${"%.2f".format(bgrPerTap)} BGR",
                subtitle = "Niv. $minerLevel",
                highlightColor = BgrGold,
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Déjà Miné",
                value = "${"%.1f".format(miningState?.totalMinedBgr ?: 0.0)} BGR",
                subtitle = "Cumul à vie",
                highlightColor = BgrCyanElectric,
                icon = Icons.Default.Stars,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // INTERACTIVE BOUNCING BGR COIN
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(coinScale.value)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BgrGoldLight, BgrGold, BgrGoldDark, Color(0xFF6B4B00))
                    )
                )
                .border(4.dp, BgrGoldLight, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    coroutineScope.launch {
                        coinScale.animateTo(0.88f, animationSpec = tween(60, easing = FastOutSlowInEasing))
                        coinScale.animateTo(1f, animationSpec = tween(90, easing = FastOutSlowInEasing))
                    }
                    tapCounter++
                    onTap()
                }
                .testTag("tap_coin_target"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bgr_app_logo),
                    contentDescription = "Coin Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "TOUCHER !",
                    color = BgrDarkBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ENERGY BAR
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = BgrGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Énergie de Minage",
                        color = BgrTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "$currentEnergy / $maxEnergy",
                    color = BgrTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { energyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = if (energyProgress > 0.2f) BgrGold else Color(0xFFEF4444),
                trackColor = BgrDarkCard
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⚡ Se recharge automatiquement : +1 énergie / 2s",
                color = BgrTextMuted,
                fontSize = 11.sp
            )
        }

        // UPGRADE MINER CARD
        BgrCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BgrDarkBackground)
                        .border(1.dp, BgrGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardDoubleArrowUp,
                        contentDescription = null,
                        tint = BgrGold,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Améliorer vers Niv. ${minerLevel + 1}",
                        color = BgrTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+250 Énergie Max • Gains x${"%.2f".format(1.0 + ((minerLevel + 1) * 0.25))}",
                        color = BgrTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Coût : ${minerLevel * 50} BGR",
                        color = BgrGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                BgrButton(
                    text = "Booster",
                    onClick = onUpgradeMiner,
                    isSecondary = true,
                    enabled = !isLoading,
                    testTag = "btn_upgrade_miner"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
