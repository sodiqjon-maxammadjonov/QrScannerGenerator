package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository
import javax.inject.Inject

class ClearScannedHistory @Inject constructor(
    private val repository: QRRepository
) {
    suspend operator fun invoke() {
        repository.clearScannedHistory()
    }
}