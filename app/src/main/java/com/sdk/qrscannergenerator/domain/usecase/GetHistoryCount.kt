package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.domain.repository.QRRepository
import javax.inject.Inject

class GetHistoryCount @Inject constructor(
    private val repository: QRRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getHistoryCount()
    }
}