package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getUserWallet(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getUserWalletOnce(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWallet(wallet: UserWalletEntity)

    @Update
    suspend fun updateUserWallet(wallet: UserWalletEntity)

    @Query("UPDATE user_wallet SET currentRole = :role WHERE id = 1")
    suspend fun updateRole(role: String)
}
