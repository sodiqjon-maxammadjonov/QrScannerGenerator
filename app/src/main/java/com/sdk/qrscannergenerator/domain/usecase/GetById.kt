package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import javax.inject.Inject

class GetQRById @Inject constructor(
    private val repository: QRRepository
) {
    suspend operator fun invoke(id: Int): QREntity? {
        return repository.getQRById(id)
    }
}