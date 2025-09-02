package com.sdk.qrscannergenerator.domain.repository

import com.sdk.qrscannergenerator.data.local.entity.QREntity
import kotlinx.coroutines.flow.Flow

interface QRRepository {

    // Insert operations
    suspend fun insertQr(
        content: String,
        type: String,
        qrType: String,
        isGenerated: Boolean,
        imagePath: String? = null
    )

    // Get operations
    fun getAllHistory(): Flow<List<QREntity>>
    fun getGeneratedHistory(): Flow<List<QREntity>>
    fun getScannedHistory(): Flow<List<QREntity>>
    fun getHistoryByContentType(type: String): Flow<List<QREntity>>
    fun searchHistory(query: String): Flow<List<QREntity>>
    suspend fun getQRById(id: Int): QREntity?
    suspend fun getHistoryCount(): Int

    // Delete operations
    suspend fun deleteQr(id: Int)
    suspend fun clearAll()
    suspend fun clearGeneratedHistory()
    suspend fun clearScannedHistory()
}