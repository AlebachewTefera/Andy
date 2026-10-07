package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ScheduleSend
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.CuteProductIcon
import com.example.ui.components.MorphCard
import com.example.ui.components.MorphPillChip
import com.example.ui.components.MorphSwitch
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.RosePink
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SyntaxBlue
import com.example.ui.theme.SyntaxCyan
import com.example.ui.theme.SyntaxCyanSoft
import com.example.ui.theme.SyntaxGreen
import com.example.ui.theme.SyntaxGreenLight
import com.example.ui.theme.SyntaxGold
import com.example.ui.theme.SyntaxGoldLight
import com.example.ui.theme.SyntaxNavy
import com.example.ui.util.ExcelExporter
import com.example.ui.util.LocalStrings

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onQuickAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val reportSummary by viewModel.reportSummary.collectAsStateWithLifecycle()
    val strings = LocalStrings.current
    val context = LocalContext.current

    var businessName by remember(profile.businessName) { mutableStateOf(profile.businessName) }
    var selectedLogo by remember(profile.logoPreset) { mutableStateOf(profile.logoPreset) }
    var selectedCurrency by remember(profile.currencySymbol) { mutableStateOf(profile.currencySymbol) }
    var selectedThemeMode by remember(profile.themeMode) { mutableStateOf(profile.themeMode) }
    var selectedLanguage by remember(profile.language) { mutableStateOf(profile.language) }

    // Auto Report States
    var autoReportEnabled by remember(profile.autoReportEnabled) { mutableStateOf(profile.autoReportEnabled) }
    var autoReportFrequency by remember(profile.autoReportFrequency) { mutableStateOf(profile.autoReportFrequency) }
    var autoReportEmail by remember(profile.autoReportEmail) { mutableStateOf(profile.autoReportEmail) }
    var autoReportFormat by remember(profile.autoReportFormat) { mutableStateOf(profile.autoReportFormat) }

    val logoPresets = listOf(
        "syntax" to "Syntax Tech ⚡",
        "store" to "Storefront",
        "cupcake" to "Bakery",
        "coffee" to "Cafe",
        "flower" to "Boutique",
        "tote" to "Fashion",
        "sparkle" to "Beauty",
        "star" to "Studio"
    )

    val currencies = listOf("$", "€", "£", "Br", "ብር", "ETB", "¥", "₹", "₩", "CA$", "A$")

    val languageOptions = listOf(
        Triple("en", "English", "English 🌐"),
        Triple("am", "አማርኛ", "Amharic (አማርኛ) 🇪🇹")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded Morph Header Banner
        item {
            MorphCard(
                shape = RoundedCornerShape(28.dp),
                accentColor = SyntaxBlue,
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SyntaxBlue.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "SYNTAX ENTERPRISE SYSTEM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SyntaxBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = profile.businessName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Customize branding, auto-reporting & languages",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .shadow(4.dp, RoundedCornerShape(18.dp))
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White)
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(listOf(SyntaxBlue, SyntaxGreen, SyntaxGold)),
                                    RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CuteProductIcon(
                                iconKey = if (profile.logoPreset.lowercase() in listOf("store", "syntax")) "syntax" else profile.logoPreset,
                                size = 52.dp,
                                iconSize = 30.dp
                            )
                        }
                    }
                }
            }
        }

        // ==================== AUTO REPORT (ON / OFF) CARD ====================
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = if (autoReportEnabled) SyntaxGreen else SyntaxBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_auto_report_settings")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Title and Morph Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(3.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (autoReportEnabled) SyntaxGreen.copy(alpha = 0.2f) else SyntaxBlue.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (autoReportEnabled) SyntaxGreen.copy(alpha = 0.6f) else SyntaxBlue.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ScheduleSend,
                                    contentDescription = null,
                                    tint = if (autoReportEnabled) SyntaxGreen else SyntaxBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.autoReport,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strings.autoReportSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Tactile Morph ON/OFF Switch
                        MorphSwitch(
                            checked = autoReportEnabled,
                            onCheckedChange = { newState ->
                                autoReportEnabled = newState
                                viewModel.setAutoReport(
                                    enabled = newState,
                                    frequency = autoReportFrequency,
                                    email = autoReportEmail,
                                    format = autoReportFormat
                                )
                            },
                            activeColor = SyntaxGreen,
                            testTag = "switch_auto_report"
                        )
                    }

                    // Always Accessible Report Email Field
                    OutlinedTextField(
                        value = autoReportEmail,
                        onValueChange = {
                            autoReportEmail = it
                            viewModel.setAutoReport(
                                enabled = autoReportEnabled,
                                frequency = autoReportFrequency,
                                email = it,
                                format = autoReportFormat
                            )
                        },
                        label = { Text(strings.autoReportEmail) },
                        placeholder = { Text("e.g. reports@syntaxtech.com or alebachew071@gmail.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_autoreport_email"),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SyntaxBlue)
                        }
                    )

                    // Status Indicator Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (autoReportEnabled) Color(0xFFEDFDF5) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(
                            1.dp,
                            if (autoReportEnabled) Color(0xFFB7F4D8) else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (autoReportEnabled) SyntaxGreen else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (autoReportEnabled) strings.autoReportStatusOn else strings.autoReportStatusOff,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (autoReportEnabled) SyntaxGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Configurable Options when Auto Report is ON
                    AnimatedVisibility(
                        visible = autoReportEnabled,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Frequency Selector
                            Column {
                                Text(
                                    text = strings.frequency + ":",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "DAILY" to strings.daily,
                                        "WEEKLY" to strings.weekly,
                                        "MONTHLY" to strings.monthly
                                    ).forEach { (freqKey, label) ->
                                        val isSelected = autoReportFrequency == freqKey
                                        MorphPillChip(
                                            text = label,
                                            isSelected = isSelected,
                                            onClick = {
                                                autoReportFrequency = freqKey
                                                viewModel.setAutoReport(
                                                    enabled = true,
                                                    frequency = freqKey,
                                                    email = autoReportEmail,
                                                    format = autoReportFormat
                                                )
                                            },
                                            accentColor = SyntaxGreen,
                                            modifier = Modifier.weight(1f),
                                            testTag = "chip_freq_${freqKey.lowercase()}"
                                        )
                                    }
                                }
                            }

                            // Format Selector
                            Column {
                                Text(
                                    text = strings.reportFormat + ":",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "EXCEL" to "Excel (.xlsx) 📊",
                                        "SUMMARY" to "Email Summary ✉️"
                                    ).forEach { (formatKey, label) ->
                                        val isSelected = autoReportFormat == formatKey
                                        MorphPillChip(
                                            text = label,
                                            isSelected = isSelected,
                                            onClick = {
                                                autoReportFormat = formatKey
                                                viewModel.setAutoReport(
                                                    enabled = true,
                                                    frequency = autoReportFrequency,
                                                    email = autoReportEmail,
                                                    format = formatKey
                                                )
                                            },
                                            accentColor = SyntaxBlue,
                                            modifier = Modifier.weight(1f),
                                            testTag = "chip_format_${formatKey.lowercase()}"
                                        )
                                    }
                                }
                            }

                            // Quick Dispatch Test Button
                            OutlinedButton(
                                onClick = {
                                    val excelFile = ExcelExporter.createExcelFile(context, reportSummary, profile, strings)
                                    val body = ExcelExporter.buildEmailBody(reportSummary, profile, strings)
                                    val subject = "[SYNTAX REPORT] ${profile.businessName} - $autoReportFrequency Summary"
                                    ExcelExporter.sendEmail(
                                        context = context,
                                        recipient = autoReportEmail.ifBlank { "accountant@syntaxtech.com" },
                                        subject = subject,
                                        body = body,
                                        excelFile = if (autoReportFormat == "EXCEL") excelFile else null
                                    )
                                    Toast.makeText(context, "Testing automated dispatch! 🚀", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_auto_report"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.2.dp, SyntaxBlue)
                            ) {
                                Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(16.dp), tint = SyntaxBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.testAutoReport, fontWeight = FontWeight.Bold, color = SyntaxBlue)
                            }
                        }
                    }
                }
            }
        }

        // ==================== LANGUAGE SETTINGS (AMHARIC & ENGLISH) ====================
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_language_settings")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = SyntaxBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.languageSetting,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.languageSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        languageOptions.forEach { (code, nativeName, desc) ->
                            val isSelected = selectedLanguage == code
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) SyntaxBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SyntaxBlue else Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedLanguage = code
                                        viewModel.setLanguage(code)
                                    }
                                    .testTag("btn_lang_$code")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                selectedLanguage = code
                                                viewModel.setLanguage(code)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = nativeName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (isSelected) SyntaxBlue else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Surface(
                                            shape = CircleShape,
                                            color = SyntaxBlue,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== BUSINESS PROFILE CARD ====================
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            Icon(Icons.Default.Store, contentDescription = null, tint = SyntaxBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.businessIdentity,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text(strings.businessName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_settings_business_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Logo Preset Picker
                    Text(
                        text = strings.businessLogo,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(logoPresets) { (key, label) ->
                            val isSelected = selectedLogo == key
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) SyntaxBlue else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedLogo = key }
                                    .padding(8.dp)
                            ) {
                                CuteProductIcon(iconKey = key, size = 46.dp, iconSize = 26.dp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SyntaxBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==================== CURRENCY CARD ====================
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(SyntaxGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = SyntaxGoldDark, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.currencySymbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Selected: $selectedCurrency (e.g. ${selectedCurrency}12.50)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(currencies) { curr ->
                            val isSelected = selectedCurrency == curr
                            MorphPillChip(
                                text = curr,
                                isSelected = isSelected,
                                onClick = { selectedCurrency = curr },
                                accentColor = SyntaxGoldDark
                            )
                        }
                    }
                }
            }
        }

        // ==================== APPEARANCE THEME CARD ====================
        item {
            MorphCard(
                shape = RoundedCornerShape(26.dp),
                accentColor = SyntaxBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            Icon(Icons.Default.SettingsBrightness, contentDescription = null, tint = SyntaxBlue, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.appearanceTheme,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val options = listOf(
                        Triple("SYSTEM", strings.systemDefault, Icons.Default.SettingsBrightness),
                        Triple("LIGHT", strings.lightMode, Icons.Default.LightMode),
                        Triple("DARK", strings.darkMode, Icons.Default.DarkMode)
                    )

                    options.forEach { (mode, label, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedThemeMode = mode }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedThemeMode == mode,
                                onClick = { selectedThemeMode = mode }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Save Button with morphic elevation
        item {
            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = businessName,
                        logoPreset = selectedLogo,
                        currency = selectedCurrency,
                        themeMode = selectedThemeMode,
                        language = selectedLanguage,
                        autoReportEnabled = autoReportEnabled,
                        autoReportFrequency = autoReportFrequency,
                        autoReportEmail = autoReportEmail,
                        autoReportFormat = autoReportFormat
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .testTag("btn_save_settings"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SyntaxBlue)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.saveSettings, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        // Quick Expense logger button
        item {
            OutlinedButton(
                onClick = onQuickAddExpense,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.logExpense, fontWeight = FontWeight.SemiBold)
            }
        }

        // App Info card
        item {
            MorphCard(
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                elevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TinyBiz Pro • Morphic Edition",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedLanguage == "am")
                                "ሁሉም መረጃዎች፣ ምርቶች፣ ራስ-ሰር ሪፖርቶች እና ቅንብሮች በስልክዎ ላይ በደህና ተቀምጠዋል።"
                            else
                                "All your products, auto-reporting schedules, and reports are saved safely on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
