package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val walletAddress: String = "0x7F2e89d1C34B8c42aA4E819b5832a8A8D59E7C01",
    val seedPhrase: String = "alpha shield orbit velvet cyber quantum pulse matrix beacon vault galaxy neon",
    val currentRole: String = "USER",
    val securityPin: String = "1234",
    val isBiometricActive: Boolean = true,
    val isWalletCreated: Boolean = true,
    val preferredCurrency: String = "USD"
)
