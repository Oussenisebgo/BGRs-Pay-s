package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
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
fun BgrCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BgrDarkCard,
    borderColor: Color = BgrBorder,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier.clip(shape)
    }

    Surface(
        modifier = clickableModifier.border(1.dp, borderColor, shape),
        color = backgroundColor,
        shape = shape
    ) {
        content()
    }
}

@Composable
fun BgrButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    isEmerald: Boolean = false,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "bgr_button"
) {
    val containerColor = when {
        isEmerald -> BgrEmerald
        isSecondary -> Color.Transparent
        else -> BgrGold
    }
    val contentColor = when {
        isSecondary -> BgrGold
        else -> BgrDarkBackground
    }

    if (isSecondary) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag(testTag),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BgrGold
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(BgrGold, BgrGoldDark))
            )
        ) {
            ButtonInnerContent(text, icon, contentColor, isLoading)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag(testTag),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = BgrBorder,
                disabledContentColor = BgrTextMuted
            )
        ) {
            ButtonInnerContent(text, icon, contentColor, isLoading)
        }
    }
}

@Composable
private fun ButtonInnerContent(
    text: String,
    icon: ImageVector?,
    contentColor: Color,
    isLoading: Boolean
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = contentColor,
            strokeWidth = 2.dp
        )
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun BgrBadge(
    text: String,
    color: Color = BgrGold,
    backgroundColor: Color = color.copy(alpha = 0.15f),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TokenLogo(
    symbol: String,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (symbol.uppercase()) {
        "BGR" -> Pair(Brush.linearGradient(listOf(BgrGold, Color(0xFFE5A100))), BgrDarkBackground)
        "ETH" -> Pair(Brush.linearGradient(listOf(Color(0xFF627EEA), Color(0xFF3B5998))), Color.White)
        "USDT" -> Pair(Brush.linearGradient(listOf(Color(0xFF26A17B), Color(0xFF13805D))), Color.White)
        "BTC" -> Pair(Brush.linearGradient(listOf(Color(0xFFF7931A), Color(0xFFD67300))), Color.White)
        "SOL" -> Pair(Brush.linearGradient(listOf(Color(0xFF9945FF), Color(0xFF14F195))), Color.White)
        else -> Pair(Brush.linearGradient(listOf(BgrViolet, BgrCyanElectric)), Color.White)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (symbol.length > 3) symbol.take(3) else symbol,
            color = textColor,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.32).sp
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    highlightColor: Color = BgrGold,
    icon: ImageVector? = null
) {
    BgrCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = BgrTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = highlightColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = BgrTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = highlightColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Procedural stylized 2D Matrix (QR-code representation)
 * Guaranteed crisp and rendered without external web dependencies.
 */
@Composable
fun QrCodeView(
    dataString: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 180.dp
) {
    val hash = dataString.hashCode()
    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val grid = 17
            val cellSize = size.width / grid

            for (r in 0 until grid) {
                for (c in 0 until grid) {
                    val isCornerTopLeft = (r in 0..4 && c in 0..4)
                    val isCornerTopRight = (r in 0..4 && c in (grid - 5) until grid)
                    val isCornerBottomLeft = (r in (grid - 5) until grid && c in 0..4)
                    val isFinderPattern = isCornerTopLeft || isCornerTopRight || isCornerBottomLeft

                    val isFinderBorder = isFinderPattern && (
                            r == 0 || r == 4 || c == 0 || c == 4 ||
                                    r == grid - 1 || r == grid - 5 || c == grid - 1 || c == grid - 5 ||
                                    (r == 2 && c == 2) ||
                                    (r == 2 && c == grid - 3) ||
                                    (r == grid - 3 && c == 2)
                            )

                    val pseudoRandomBit = ((hash * (r + 1) * 31 + c * 17 + r * c) and 0x7FFF) % 2 == 0

                    if (isFinderBorder || (!isFinderPattern && pseudoRandomBit)) {
                        drawRect(
                            color = Color(0xFF0F172A),
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 0.9f, cellSize * 0.9f)
                        )
                    }
                }
            }
        }

        // Center BGR Mini Badge
        Box(
            modifier = Modifier
                .size(sizeDp * 0.22f)
                .clip(CircleShape)
                .background(BgrGold)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "BGR",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
            )
        }
    }
}
