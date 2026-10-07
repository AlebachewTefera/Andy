package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.BusinessProfile
import com.example.ui.MainViewModel
import com.example.ui.ReportPeriod
import com.example.ui.ReportSummary
import com.example.ui.components.CuteSummaryCard
import com.example.ui.components.MorphCard
import com.example.ui.components.MorphPillChip
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import com.example.ui.theme.LavenderPurple
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.RosePink
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SyntaxBlue
import com.example.ui.theme.SyntaxCyan
import com.example.ui.theme.SyntaxCyanSoft
import com.example.ui.theme.SyntaxGreen
import com.example.ui.theme.SyntaxGreenLight
import com.example.ui.theme.SyntaxGold
import com.example.ui.theme.SyntaxGoldLight
import com.example.ui.util.AppStrings
import com.example.ui.util.ExcelExporter
import com.example.ui.util.LocalStrings
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: MainViewModel,
    onQuickAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report by viewModel.reportSummary.collectAsStateWithLifecycle()
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val currency = profile.currencySymbol
    val strings = LocalStrings.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val periods = listOf(
        ReportPeriod.DAILY to strings.daily,
        ReportPeriod.WEEKLY to strings.weekly,
        ReportPeriod.MONTHLY to strings.monthly
    )

    // Dialog states
    var showEmailDialog by remember { mutableStateOf(false) }
    var showExcelSavedDialog by remember { mutableStateOf(false) }
    var generatedExcelFile by remember { mutableStateOf<File?>(null) }

    // SAF Document creator launcher
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    ExcelExporter.writeExcelToStream(os, report, profile, strings)
                }
                Toast.makeText(context, "Excel report saved successfully! 📊", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Period Selector Tabs (Morph Card)
        item {
            MorphCard(
                shape = RoundedCornerShape(22.dp),
                accentColor = RosePink,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    periods.forEach { (period, label) ->
                        val isSelected = report.period == period
                        Button(
                            onClick = { viewModel.setReportPeriod(period) },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("tab_report_${period.name.lowercase()}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) SyntaxBlue else Color.Transparent,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null
                        ) {
                            Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Action Banner: Email, Save Excel & Auto-Report Status (Morph Card)
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxBlue,
                elevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_report_actions")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SyntaxBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = SyntaxBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = strings.exportAndShare,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Excel (.xlsx) • Email Reports",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Auto-Report Pill Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (profile.autoReportEnabled) SyntaxGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (profile.autoReportEnabled) SyntaxGreen else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (profile.autoReportEnabled) SyntaxGreen else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (profile.autoReportEnabled) "Auto: ${profile.autoReportFrequency}" else "Auto: OFF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (profile.autoReportEnabled) SyntaxGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Auto-Report Shortcut if enabled
                    if (profile.autoReportEnabled) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "⚡ Auto-Report is Active (${profile.autoReportFrequency})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = if (profile.autoReportEmail.isNotBlank()) "Recipient: ${profile.autoReportEmail}" else "Configure email in Settings",
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Email Report Button
                        Button(
                            onClick = { showEmailDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .shadow(3.dp, RoundedCornerShape(16.dp))
                                .testTag("btn_email_report"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SyntaxBlue)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.emailReport, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Save Excel Button
                        Button(
                            onClick = {
                                val file = ExcelExporter.createExcelFile(context, report, profile, strings)
                                generatedExcelFile = file
                                showExcelSavedDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .testTag("btn_save_excel"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SyntaxCyanSoft)
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = SyntaxBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                strings.saveExcel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SyntaxBlue
                            )
                        }
                    }
                }
            }
        }

        // Summary Cards 2x2
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CuteSummaryCard(
                        title = strings.totalSales,
                        value = formatCurrency(report.totalSales, currency),
                        subtitle = strings.transactionsCount(report.recentTransactions.size),
                        icon = Icons.Default.PointOfSale,
                        primaryColor = SyntaxBlue,
                        bgColor = SyntaxCyanSoft,
                        modifier = Modifier.weight(1f)
                    )
                    CuteSummaryCard(
                        title = strings.totalExpenses,
                        value = formatCurrency(report.totalExpenses, currency),
                        subtitle = strings.expensesCount(report.recentExpenses.size),
                        icon = Icons.Default.TrendingDown,
                        primaryColor = SyntaxGoldDark,
                        bgColor = SyntaxGoldLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val profitColor = if (report.totalProfit >= 0) SyntaxGreen else Color(0xFFE53E3E)
                    val profitBg = if (report.totalProfit >= 0) SyntaxGreenLight else Color(0xFFFFECEC)

                    CuteSummaryCard(
                        title = strings.netProfit,
                        value = formatCurrency(report.totalProfit, currency),
                        subtitle = if (report.totalProfit >= 0) "Profitable! 🏆" else "Loss",
                        icon = Icons.Default.TrendingUp,
                        primaryColor = profitColor,
                        bgColor = profitBg,
                        modifier = Modifier.weight(1f)
                    )
                    CuteSummaryCard(
                        title = strings.productsSold,
                        value = "${report.totalUnitsSold} units",
                        subtitle = "${report.totalStockRemaining} ${strings.remainingStock}",
                        icon = Icons.Default.Inventory,
                        primaryColor = SyntaxCyan,
                        bgColor = Color(0xFFF0F9FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Visual Comparison Chart (Sales vs Expenses)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.salesVsExpenses,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = onQuickAddExpense,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.logExpense, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxVal = maxOf(report.totalSales, report.totalExpenses, 1.0)
                    val salesRatio = (report.totalSales / maxVal).toFloat().coerceIn(0f, 1f)
                    val expenseRatio = (report.totalExpenses / maxVal).toFloat().coerceIn(0f, 1f)

                    // Sales bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalSales, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text(formatCurrency(report.totalSales, currency), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = RosePink)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { salesRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = RosePink,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expenses bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalExpenses, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text(formatCurrency(report.totalExpenses, currency), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SunnyYellow)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { expenseRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = SunnyYellow,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Top Selling Products breakdown
        if (report.topSellingProducts.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.topSellingProducts,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val topCount = report.topSellingProducts.maxOfOrNull { it.second } ?: 1
                        report.topSellingProducts.forEachIndexed { index, (productName, qtySold) ->
                            val ratio = (qtySold.toFloat() / topCount.toFloat()).coerceIn(0f, 1f)
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${index + 1}. $productName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$qtySold sold",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = when (index) {
                                        0 -> RosePink
                                        1 -> PastelBlue
                                        2 -> MintGreen
                                        else -> LavenderPurple
                                    },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Transactions in this period
        item {
            Text(
                text = strings.salesInPeriod,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (report.recentTransactions.isEmpty()) {
            item {
                Text(
                    text = strings.noSalesInPeriod,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(report.recentTransactions) { sale ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(sale.productName, fontWeight = FontWeight.Bold)
                            Text(
                                "${sale.quantity}x • " + formatDate(sale.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            formatCurrency(sale.totalAmount, currency),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Email Dialog
    if (showEmailDialog) {
        EmailReportDialog(
            report = report,
            profile = profile,
            strings = strings,
            onDismiss = { showEmailDialog = false },
            onSend = { recipient, subject, body, attachExcel ->
                val excelFile = if (attachExcel) {
                    ExcelExporter.createExcelFile(context, report, profile, strings)
                } else null
                ExcelExporter.sendEmail(context, recipient, subject, body, excelFile)
                showEmailDialog = false
            }
        )
    }

    // Excel Saved Confirmation Dialog
    if (showExcelSavedDialog && generatedExcelFile != null) {
        ExcelSavedDialog(
            file = generatedExcelFile!!,
            report = report,
            profile = profile,
            strings = strings,
            onDismiss = { showExcelSavedDialog = false },
            onOpenExcel = {
                ExcelExporter.openExcelFile(context, generatedExcelFile!!)
                showExcelSavedDialog = false
            },
            onShare = {
                ExcelExporter.shareExcelFile(
                    context,
                    generatedExcelFile!!,
                    "${profile.businessName} - ${report.period.name} Excel Report"
                )
                showExcelSavedDialog = false
            },
            onSaveToFolder = {
                val cleanBiz = profile.businessName.replace(Regex("[^a-zA-Z0-9_]"), "_").take(15)
                val defaultFilename = "TinyBiz_${cleanBiz}_${report.period.name.lowercase()}.xlsx"
                createDocLauncher.launch(defaultFilename)
                showExcelSavedDialog = false
            }
        )
    }
}

@Composable
fun EmailReportDialog(
    report: ReportSummary,
    profile: BusinessProfile,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSend: (recipient: String, subject: String, body: String, attachExcel: Boolean) -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    val periodName = when (report.period) {
        ReportPeriod.DAILY -> strings.daily
        ReportPeriod.WEEKLY -> strings.weekly
        ReportPeriod.MONTHLY -> strings.monthly
    }

    var recipient by remember(profile.autoReportEmail) {
        mutableStateOf(profile.autoReportEmail)
    }
    var subject by remember {
        mutableStateOf("[${profile.businessName}] $periodName Business Report - $dateStr")
    }
    var attachExcel by remember { mutableStateOf(true) }
    val emailBody = remember(report, profile, strings) {
        ExcelExporter.buildEmailBody(report, profile, strings)
    }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = RosePink)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.emailReport)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text(strings.recipientEmail) },
                    placeholder = { Text("e.g. manager@shop.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email_recipient"),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(strings.emailSubject) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email_subject"),
                    shape = RoundedCornerShape(14.dp)
                )

                // Attach Excel Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { attachExcel = !attachExcel }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = attachExcel,
                        onCheckedChange = { attachExcel = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = strings.attachExcel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Includes full transaction & expense worksheets",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Email Body Preview
                Text(
                    text = "Email Preview:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = emailBody,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Copy Text Button
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(emailBody))
                        Toast.makeText(context, "Summary copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.copySummary, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(recipient, subject, emailBody, attachExcel) },
                colors = ButtonDefaults.buttonColors(containerColor = RosePink),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("btn_send_email_confirm")
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.sendEmail, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
fun ExcelSavedDialog(
    file: File,
    report: ReportSummary,
    profile: BusinessProfile,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onOpenExcel: () -> Unit,
    onShare: () -> Unit,
    onSaveToFolder: () -> Unit
) {
    val fileSizeKb = (file.length() / 1024.0).let { "%.1f KB".format(it) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.excelReportReady)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = RosePink, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(file.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Format: Excel OpenXML (.xlsx) • Size: $fileSizeKb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Text(
                    text = "The file contains financial summaries, product sales ranking, individual sales transactions, and expense logs.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Actions
                Button(
                    onClick = onOpenExcel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_open_excel_action"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePink)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.openInExcel, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("btn_share_excel_action"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.shareExcel, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onSaveToFolder,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("btn_save_folder_action"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Folder", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
fun AddExpenseDialog(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Supplies") }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Supplies", "Ingredients", "Packaging", "Rent", "Utilities", "Marketing", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = SunnyYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Business Expense")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Description *") },
                    placeholder = { Text("e.g. Flour & Butter, Ribbon boxes") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_title")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($currency) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_amount")
                )

                // Category selector
                Column {
                    Text("Category:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { showCategoryMenu = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(category, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                errorText?.let {
                    Text(it, color = Color(0xFFE53E3E), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Please enter an expense title"
                        return@Button
                    }
                    val amount = amountStr.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorText = "Please enter a valid amount"
                        return@Button
                    }
                    onSave(title.trim(), amount, category)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SunnyYellow),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Expense", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

