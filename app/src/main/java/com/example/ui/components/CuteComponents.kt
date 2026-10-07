package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.ui.theme.SyntaxBlue
import com.example.ui.theme.SyntaxCyan
import com.example.ui.theme.SyntaxCyanSoft
import com.example.ui.theme.SyntaxGreen
import com.example.ui.theme.SyntaxGreenLight
import com.example.ui.theme.SyntaxGold
import com.example.ui.theme.SyntaxGoldLight
import com.example.ui.theme.SyntaxNavy
import com.example.ui.theme.HoneyPeach
import com.example.ui.theme.LavenderPurple
import com.example.ui.theme.LightRose
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.RosePink
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SoftYellow
import com.example.ui.theme.SunnyYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatCurrency(amount: Double, symbol: String = "$"): String {
    return String.format(Locale.US, "%s%.2f", symbol, amount)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getIconVectorFor(iconKey: String): ImageVector {
    return when (iconKey.lowercase()) {
        "cupcake", "bakery", "cake" -> Icons.Default.Cake
        "coffee", "drink", "cafe" -> Icons.Default.Coffee
        "candle", "light" -> Icons.Default.Lightbulb
        "tote", "bag", "boutique" -> Icons.Default.ShoppingBag
        "sparkle", "beauty" -> Icons.Default.AutoAwesome
        "flower", "plants", "spa" -> Icons.Default.Spa
        "book", "stationery" -> Icons.Default.MenuBook
        "star", "popular" -> Icons.Default.Star
        "tag", "discount" -> Icons.Default.LocalOffer
        else -> Icons.Default.Inventory2
    }
}

fun getIconColorFor(iconKey: String): Pair<Color, Color> {
    return when (iconKey.lowercase()) {
        "syntax", "technology", "tech", "wifi" -> Pair(SyntaxBlue, SyntaxCyanSoft)
        "cupcake", "bakery", "cake" -> Pair(SyntaxBlue, SyntaxCyanSoft)
        "coffee", "drink", "cafe" -> Pair(SyntaxGold, SyntaxGoldLight)
        "candle", "light" -> Pair(SyntaxGold, SyntaxGoldLight)
        "tote", "bag", "boutique" -> Pair(SyntaxBlue, SyntaxCyanSoft)
        "sparkle", "beauty" -> Pair(SyntaxCyan, SyntaxCyanSoft)
        "flower", "plants", "spa" -> Pair(SyntaxGreen, SyntaxGreenLight)
        "store" -> Pair(SyntaxBlue, SyntaxCyanSoft)
        else -> Pair(SyntaxBlue, SyntaxCyanSoft)
    }
}

/**
 * High-fidelity morphic card with dual-depth lighting, frosted border highlight,
 * and squircle curvature.
 */
@Composable
fun MorphCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    accentColor: Color = RosePink,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 3.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.15f),
                spotColor = accentColor.copy(alpha = 0.25f)
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color.White.copy(alpha = 0.2f),
                    accentColor.copy(alpha = 0.25f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            containerColor.copy(alpha = 0.98f),
                            containerColor.copy(alpha = 0.94f)
                        )
                    )
                )
        ) {
            content()
        }
    }
}

/**
 * Tactile Morphic Switch with debossed track and sliding glowing knob.
 */
@Composable
fun MorphSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = RosePink,
    testTag: String = "morph_switch"
) {
    val trackWidth = 72.dp
    val trackHeight = 36.dp
    val knobSize = 28.dp
    val padding = 4.dp

    val knobOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - knobSize - padding else padding,
        animationSpec = spring(stiffness = 500f),
        label = "knob_offset"
    )

    val trackBgColor by animateColorAsState(
        targetValue = if (checked) activeColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
        label = "track_bg"
    )

    val knobColor by animateColorAsState(
        targetValue = if (checked) activeColor else Color.White,
        label = "knob_color"
    )

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(CircleShape)
            .background(trackBgColor)
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.8f),
                        if (checked) activeColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        // Label inside track
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = if (checked) Arrangement.Start else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (checked) "ON" else "OFF",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (checked) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        // Sliding Knob
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(knobSize)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            if (checked) activeColor.copy(alpha = 0.85f) else Color.White,
                            knobColor
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.9f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (checked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Tactile Morphic Pill Chip for filters and options.
 */
@Composable
fun MorphPillChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = SyntaxBlue,
    testTag: String = ""
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            brush = if (isSelected) {
                Brush.linearGradient(listOf(accentColor, SyntaxCyan))
            } else {
                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.7f), Color.Transparent))
            }
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Branded Asymmetrical Morph Stat Card with debossed icon pod.
 */
@Composable
fun CuteSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    primaryColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "summary_card"
) {
    val asymmetricShape = RoundedCornerShape(topStart = 26.dp, bottomEnd = 26.dp, topEnd = 14.dp, bottomStart = 14.dp)

    Card(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = 3.dp,
                shape = asymmetricShape,
                ambientColor = primaryColor.copy(alpha = 0.12f),
                spotColor = primaryColor.copy(alpha = 0.2f)
            ),
        shape = asymmetricShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.9f),
                    bgColor.copy(alpha = 0.6f),
                    primaryColor.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            bgColor.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Debossed Icon Pod with glowing halo
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color.White,
                                        bgColor
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(primaryColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CuteProductIcon(
    iconKey: String,
    size: Dp = 48.dp,
    iconSize: Dp = 26.dp,
    modifier: Modifier = Modifier
) {
    if (iconKey.lowercase() in listOf("syntax", "technology", "tech", "wifi")) {
        Box(
            modifier = modifier
                .size(size)
                .shadow(3.dp, RoundedCornerShape(size / 2.6f))
                .clip(RoundedCornerShape(size / 2.6f))
                .background(Color.White)
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(SyntaxBlue.copy(alpha = 0.5f), SyntaxGreen.copy(alpha = 0.5f), SyntaxGold.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(size / 2.6f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.syntax_logo),
                contentDescription = "Syntax Technology Logo",
                modifier = Modifier
                    .size(iconSize * 1.3f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        val (iconColor, bgColor) = getIconColorFor(iconKey)
        Box(
            modifier = modifier
                .size(size)
                .shadow(2.dp, RoundedCornerShape(size / 2.6f))
                .clip(RoundedCornerShape(size / 2.6f))
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            bgColor
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(size / 2.6f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconVectorFor(iconKey),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun LowStockBadge(
    remaining: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFF2EC),
        border = BorderStroke(1.dp, Color(0xFFFFCCBA)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFE53E3E),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = if (remaining == 0) "Out of stock!" else "Low: $remaining left",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC53030),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CuteEmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            androidx.compose.material3.Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(buttonText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
