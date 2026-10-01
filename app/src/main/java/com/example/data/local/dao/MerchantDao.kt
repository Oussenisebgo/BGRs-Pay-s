package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MerchantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchants ORDER BY totalVolumeUsd DESC")
    fun getAllMerchants(): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE isVerified = 1 ORDER BY cashbackRate DESC")
    fun getVerifiedMerchants(): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE id = :id LIMIT 1")
    suspend fun getMerchantById(id: String): MerchantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchants(merchants: List<MerchantEntity>)

    @Update
    suspend fun updateMerchant(merchant: MerchantEntity)

    @Query("UPDATE merchants SET isVerified = :isVerified WHERE id = :id")
    suspend fun updateVerificationStatus(id: String, isVerified: Boolean)

    @Query("UPDATE merchants SET cashbackRate = :rate WHERE id = :id")
    suspend fun updateCashbackRate(id: String, rate: Double)
}
