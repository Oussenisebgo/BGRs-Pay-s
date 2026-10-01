package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val change24h: Double,
    val network: String
)
