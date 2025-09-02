package com.sdk.qrscannergenerator.domain.usecase

import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchHistory @Inject constructor(
    private val repository: QRRepository
) {
    operator fun invoke(query: String): Flow<List<QREntity>> {
        return repository.searchHistory(query)
    }
}