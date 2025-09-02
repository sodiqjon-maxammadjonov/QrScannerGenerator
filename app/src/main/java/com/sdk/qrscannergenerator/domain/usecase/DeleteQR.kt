package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository
import javax.inject.Inject

class DeleteQr @Inject constructor(
    private val repository: QRRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteQr(id)
    }
}