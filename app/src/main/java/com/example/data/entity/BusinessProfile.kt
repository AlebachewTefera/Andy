package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey
    val id: Int = 1,
    val businessName: String = "Cute Corner Shop",
    val logoPreset: String = "store",
    val currencySymbol: String = "$",
    val themeMode: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"
    val language: String = "en", // "en" for English, "am" for Amharic (አማርኛ)
    val autoReportEnabled: Boolean = false, // Auto Report ON/OFF
    val autoReportFrequency: String = "DAILY", // "DAILY", "WEEKLY", "MONTHLY"
    val autoReportEmail: String = "", // Recipient email for automated report dispatch
    val autoReportFormat: String = "EXCEL" // "EXCEL" or "SUMMARY"
)
