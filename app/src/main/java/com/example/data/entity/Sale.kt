package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val totalAmount: Double,
    val customerId: Long? = null,
    val customerName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
