package com.sdk.qrscannergenerator.domain.repositoryimpl

import com.sdk.qrscannergenerator.data.local.dao.QRDao
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class QrRepositoryImpl @Inject constructor(
    private val dao: QRDao
) : QRRepository {

    override suspend fun insertQr(
        content: String,
        type: String,
        qrType: String,
        isGenerated: Boolean,
        imagePath: String?
    ) {
        val entity = QREntity(
            content = content,
            type = type,
            qrType = qrType,
            createdAt = System.currentTimeMillis(),
            isGenerated = isGenerated,
            imagePath = imagePath
        )
        dao.insertQR(entity)
    }

    override fun getAllHistory(): Flow<List<QREntity>> {
        return dao.getAllHistory()
    }

    override fun getGeneratedHistory(): Flow<List<QREntity>> {
        return dao.getHistoryByType(isGenerated = true)
    }

    override fun getScannedHistory(): Flow<List<QREntity>> {
        return dao.getHistoryByType(isGenerated = false)
    }

    override fun getHistoryByContentType(type: String): Flow<List<QREntity>> {
        return dao.getHistoryByContentType(type)
    }

    override fun searchHistory(query: String): Flow<List<QREntity>> {
        return dao.searchHistory(query)
    }

    override suspend fun getQRById(id: Int): QREntity? {
        return dao.getQRById(id)
    }

    override suspend fun getHistoryCount(): Int {
        return dao.getHistoryCount()
    }

    override suspend fun deleteQr(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    override suspend fun clearGeneratedHistory() {
        dao.clearByType(isGenerated = true)
    }

    override suspend fun clearScannedHistory() {
        dao.clearByType(isGenerated = false)
    }
}