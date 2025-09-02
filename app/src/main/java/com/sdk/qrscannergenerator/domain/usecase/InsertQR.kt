package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository
import jakarta.inject.Inject

class InsertQR @Inject constructor(
    private val repository: QRRepository
) {
    suspend operator fun invoke(
        content: String,
        type: String,
        qrType: String,
        isGenerated: Boolean,
        imagePath: String? = null
    ) {
        repository.insertQr(content, type, qrType, isGenerated, imagePath)
    }
}