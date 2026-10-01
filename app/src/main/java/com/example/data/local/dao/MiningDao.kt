package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MiningEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MiningDao {
    @Query("SELECT * FROM mining_state WHERE id = 1 LIMIT 1")
    fun getMiningState(): Flow<MiningEntity?>

    @Query("SELECT * FROM mining_state WHERE id = 1 LIMIT 1")
    suspend fun getMiningStateOnce(): MiningEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMiningState(miningEntity: MiningEntity)

    @Update
    suspend fun updateMiningState(miningEntity: MiningEntity)
}
