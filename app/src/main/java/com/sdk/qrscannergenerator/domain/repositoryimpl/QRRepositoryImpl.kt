package com.sdk.qrscannergenerator.domain.repositoryimpl

import com.sdk.qrscannergenerator.data.local.dao.QRDao
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import kotlinx.coroutines.flow.Flow

class QrRepositoryImpl(
    private val dao: QRDao
) : QRRepository {

    override suspend fun insertQr(content: String, type: String, isGenerated: Boolean) {
        val entity = QREntity(
            content = content,
            type = type,
            createdAt = System.currentTimeMillis(),
            isGenerated = isGenerated
        )
        dao.insertQR(entity)
    }

    override fun getAllHistory(): Flow<List<QREntity>> {
        return dao.getAllHistory()
    }

    override suspend fun deleteQr(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
