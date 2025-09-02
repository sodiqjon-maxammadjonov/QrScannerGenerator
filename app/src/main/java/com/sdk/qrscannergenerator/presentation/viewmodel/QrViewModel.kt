package com.sdk.qrscannergenerator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class QrUiState(
    val history: List<QREntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class QrEvent {
    data class Insert(val content: String, val type: String, val isGenerated: Boolean) : QrEvent()
    data class Delete(val id: Int) : QrEvent()
    object ClearAll : QrEvent()
    object LoadHistory : QrEvent()
}

class QrViewModel(
    private val insertQr: InsertQR,
    private val getHistory: GetHistory,
    private val deleteQr: DeleteQr,
    private val clearHistory: ClearHistory
) : ViewModel() {

    private val _state = MutableStateFlow(QrUiState())
    val state: StateFlow<QrUiState> = _state.asStateFlow()
    private val _scannedResults = MutableStateFlow<List<String>>(emptyList())
    val scannedResults: StateFlow<List<String>> = _scannedResults

    init {
        onEvent(QrEvent.LoadHistory)
    }

    fun onEvent(event: QrEvent) {
        when (event) {
            is QrEvent.Insert -> {
                viewModelScope.launch {
                    insertQr(event.content, event.type, event.isGenerated)
                }
            }

            is QrEvent.Delete -> {
                viewModelScope.launch {
                    deleteQr(event.id)
                }
            }

            is QrEvent.ClearAll -> {
                viewModelScope.launch {
                    clearHistory()
                }
            }

            is QrEvent.LoadHistory -> {
                getHistory()
                    .onEach { list ->
                        _state.value = _state.value.copy(history = list, isLoading = false)
                    }
                    .launchIn(viewModelScope)
            }
        }

    }
    fun saveScannedResult(result: String) {
        viewModelScope.launch {
            val updated = _scannedResults.value.toMutableList()
            if (!updated.contains(result)) {
                updated.add(0, result)
            }
            _scannedResults.value = updated
        }
    }
}
