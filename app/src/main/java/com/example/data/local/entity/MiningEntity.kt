package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mining_state")
data class MiningEntity(
    @PrimaryKey val id: Int = 1,
    val currentEnergy: Int = 1000,
    val maxEnergy: Int = 1000,
    val energyPerTap: Int = 2,
    val bgrPerTap: Double = 0.25,
    val multiplier: Double = 1.0,
    val minerLevel: Int = 1,
    val totalMinedBgr: Double = 0.0,
    val unclaimedBgr: Double = 0.0,
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
)
