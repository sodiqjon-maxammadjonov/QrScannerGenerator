package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import kotlinx.coroutines.flow.Flow

class GetHistory(private val repository: QRRepository) {
    operator fun invoke(): Flow<List<QREntity>> {
        return repository.getAllHistory()
    }
}
