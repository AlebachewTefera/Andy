package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val costPrice: Double = 0.0,
    val stockQuantity: Int,
    val initialQuantity: Int = stockQuantity,
    val lowStockThreshold: Int = 5,
    val category: String = "General",
    val iconKey: String = "box",
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
