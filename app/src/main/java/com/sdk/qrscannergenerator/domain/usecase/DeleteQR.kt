package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository

class DeleteQr(private val repository: QRRepository) {
    suspend operator fun invoke(id: Int) {
        repository.deleteQr(id)
    }
}
