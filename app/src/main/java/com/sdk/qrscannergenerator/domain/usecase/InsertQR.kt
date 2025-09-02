package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository

class InsertQR(private val repository: QRRepository) {
    suspend operator fun invoke(content: String, type: String, isGenerated: Boolean) {
        repository.insertQr(content, type, isGenerated)
    }
}
