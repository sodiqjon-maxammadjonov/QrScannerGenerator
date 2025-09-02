package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository

class ClearHistory(private val repository: QRRepository) {
    suspend operator fun invoke() {
        repository.clearAll()
    }
}
