package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val cashbackRate: Double, // in percent, e.g. 5.0 = 5%
    val walletAddress: String,
    val isVerified: Boolean = true,
    val totalVolumeUsd: Double = 0.0,
    val city: String = "Paris"
)
