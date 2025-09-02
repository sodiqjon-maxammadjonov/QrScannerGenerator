package com.sdk.qrscannergenerator.domain.repository

import com.sdk.qrscannergenerator.data.local.entity.QREntity
import kotlinx.coroutines.flow.Flow

interface QRRepository {
    suspend fun insertQr(content: String, type: String, isGenerated: Boolean)
    fun getAllHistory(): Flow<List<QREntity>>
    suspend fun deleteQr(id: Int)
    suspend fun clearAll()
}
