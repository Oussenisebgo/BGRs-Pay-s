package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val amount: Double,
    val symbol: String,
    val fiatAmount: Double,
    val counterparty: String,
    val cashbackBgr: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String,
    val category: String,
    val txHash: String
)
