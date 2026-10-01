package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM tokens ORDER BY balance * priceUsd DESC")
    fun getAllTokens(): Flow<List<TokenEntity>>

    @Query("SELECT * FROM tokens WHERE symbol = :symbol LIMIT 1")
    suspend fun getToken(symbol: String): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokens(tokens: List<TokenEntity>)

    @Update
    suspend fun updateToken(token: TokenEntity)

    @Query("UPDATE tokens SET balance = :newBalance WHERE symbol = :symbol")
    suspend fun updateBalance(symbol: String, newBalance: Double)
}
