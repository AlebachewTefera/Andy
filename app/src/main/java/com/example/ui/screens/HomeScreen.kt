package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Product
import com.example.ui.MainViewModel
import com.example.ui.NavSection
import com.example.ui.components.CuteEmptyState
import com.example.ui.components.CuteProductIcon
import com.example.ui.components.CuteSummaryCard
import com.example.ui.components.LowStockBadge
import com.example.ui.components.MorphCard
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.components.formatTime
import com.example.ui.theme.HoneyPeach
import com.example.ui.theme.LavenderPurple
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.RosePink
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SunnyYellow
import com.example.ui.util.LocalStrings

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateTo: (NavSection) -> Unit,
    onQuickAddSale: () -> Unit,
    onQuickAddProduct: () -> Unit,
    onQuickAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val currency = profile.currencySymbol
    val strings = LocalStrings.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded Morph Header Greeting
        item {
            MorphCard(
                shape = RoundedCornerShape(28.dp),
                accentColor = SyntaxBlue,
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SyntaxBlue.copy(alpha = 0.12f),
                                    SyntaxCyan.copy(alpha = 0.08f),
                                    SyntaxGold.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SyntaxBlue.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "SYNTAX ENTERPRISE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SyntaxBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        letterSpacing = 1.sp
                                    )
                                }
                                if (profile.autoReportEnabled) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SyntaxGreen.copy(alpha = 0.18f)
                                    ) {
                                        Text(
                                            text = "⚡ AUTO-REPORT ON",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SyntaxGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.welcomeBack,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile.businessName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Today • " + formatDate(System.currentTimeMillis()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(4.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(SyntaxBlue, SyntaxGreen, SyntaxGold)
                                    ),
                                    RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CuteProductIcon(
                                iconKey = if (profile.logoPreset.lowercase() in listOf("store", "syntax")) "syntax" else profile.logoPreset,
                                size = 58.dp,
                                iconSize = 34.dp
                            )
                        }
                    }
                }
            }
        }

        // Quick Low-Stock Warning Banner (if any)
        if (summary.lowStockCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTo(NavSection.PRODUCTS) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4EC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD5C2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFDFD3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDD6B20),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Low Stock Alert ⚠️",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9C4221)
                                )
                                Text(
                                    text = "${summary.lowStockCount} product(s) are running out soon!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB75E38)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View products",
                            tint = Color(0xFFDD6B20),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Cute Summary Cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CuteSummaryCard(
                        title = strings.todaySales,
                        value = formatCurrency(summary.todaySales, currency),
                        subtitle = "Gross revenue",
                        icon = Icons.Default.PointOfSale,
                        primaryColor = SyntaxBlue,
                        bgColor = SyntaxCyanSoft,
                        modifier = Modifier.weight(1f),
                        testTag = "card_today_sales"
                    )
                    CuteSummaryCard(
                        title = strings.todayExpenses,
                        value = formatCurrency(summary.todayExpenses, currency),
                        subtitle = "Supplies & costs",
                        icon = Icons.Default.TrendingDown,
                        primaryColor = SyntaxGoldDark,
                        bgColor = SyntaxGoldLight,
                        modifier = Modifier.weight(1f),
                        testTag = "card_today_expenses"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val profitColor = if (summary.todayProfit >= 0) SyntaxGreen else Color(0xFFE53E3E)
                    val profitBg = if (summary.todayProfit >= 0) SyntaxGreenLight else Color(0xFFFFECEC)

                    CuteSummaryCard(
                        title = strings.todayProfit,
                        value = formatCurrency(summary.todayProfit, currency),
                        subtitle = if (summary.todayProfit >= 0) "Net profit 📈" else "Net loss",
                        icon = Icons.Default.TrendingUp,
                        primaryColor = profitColor,
                        bgColor = profitBg,
                        modifier = Modifier.weight(1f),
                        testTag = "card_today_profit"
                    )
                    CuteSummaryCard(
                        title = "Remaining Stock",
                        value = "${summary.totalRemainingStock} units",
                        subtitle = "${summary.totalProductsCount} products total",
                        icon = Icons.Default.Inventory,
                        primaryColor = SyntaxCyan,
                        bgColor = Color(0xFFF0F9FF),
                        modifier = Modifier.weight(1f),
                        testTag = "card_remaining_stock"
                    )
                }
            }
        }

        // Quick Action Buttons
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxBlue,
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.quickActions,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onQuickAddSale,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SyntaxBlue),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .shadow(2.dp, RoundedCornerShape(18.dp))
                                .testTag("btn_quick_new_sale")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.newSale, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onQuickAddProduct,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_quick_add_product")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.addProduct, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onQuickAddExpense,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_quick_add_expense")
                        ) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.logExpense, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Recent Sales Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Sales 🛍️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateTo(NavSection.SALES) }
                )
            }
        }

        // Recent Sales List or Empty
        if (summary.recentSales.isEmpty()) {
            item {
                CuteEmptyState(
                    title = "No sales recorded yet",
                    message = "Tap 'New Sale' above to record your first sale! 🌸",
                    icon = Icons.Default.ReceiptLong,
                    buttonText = "Record First Sale",
                    onButtonClick = onQuickAddSale
                )
            }
        } else {
            items(summary.recentSales) { sale ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFEDF1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = RosePink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = sale.productName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${sale.quantity}x sold • " + (sale.customerName ?: "Walk-in guest"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(sale.totalAmount, currency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatTime(sale.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
