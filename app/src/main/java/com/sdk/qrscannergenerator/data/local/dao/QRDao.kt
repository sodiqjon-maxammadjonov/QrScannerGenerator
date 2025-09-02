package com.sdk.qrscannergenerator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QRDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQR(qrEntity: QREntity)

    @Query("Select * from qr_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<QREntity>>

    @Query("DELETE FROM qr_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM qr_history")
    suspend fun clearAll()
}