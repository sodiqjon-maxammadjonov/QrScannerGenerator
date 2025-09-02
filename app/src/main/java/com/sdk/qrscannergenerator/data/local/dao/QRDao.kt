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

    @Query("SELECT * FROM qr_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<QREntity>>

    @Query("SELECT * FROM qr_history WHERE isGenerated = :isGenerated ORDER BY createdAt DESC")
    fun getHistoryByType(isGenerated: Boolean): Flow<List<QREntity>>

    @Query("SELECT * FROM qr_history WHERE type = :type ORDER BY createdAt DESC")
    fun getHistoryByContentType(type: String): Flow<List<QREntity>>

    @Query("SELECT * FROM qr_history WHERE content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchHistory(query: String): Flow<List<QREntity>>

    @Query("SELECT * FROM qr_history WHERE id = :id")
    suspend fun getQRById(id: Int): QREntity?

    @Query("DELETE FROM qr_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM qr_history")
    suspend fun clearAll()

    @Query("DELETE FROM qr_history WHERE isGenerated = :isGenerated")
    suspend fun clearByType(isGenerated: Boolean)

    @Query("SELECT COUNT(*) FROM qr_history")
    suspend fun getHistoryCount(): Int
}