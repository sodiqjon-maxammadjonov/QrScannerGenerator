package com.sdk.qrscannergenerator.presentation.state

import com.sdk.qrscannergenerator.data.local.entity.QREntity

data class QRUiState(
    // Loading states
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val isScanning: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,

    // Data states
    val allHistory: List<QREntity> = emptyList(),
    val generatedHistory: List<QREntity> = emptyList(),
    val scannedHistory: List<QREntity> = emptyList(),
    val filteredHistory: List<QREntity> = emptyList(),
    val selectedQR: QREntity? = null,
    val historyCount: Int = 0,

    // Search and filter states
    val searchQuery: String = "",
    val selectedContentType: String = "ALL",
    val selectedHistoryType: HistoryType = HistoryType.ALL,

    // Generated QR/Barcode states
    val generatedBitmap: android.graphics.Bitmap? = null,
    val lastGeneratedContent: String = "",

    // Scanned result states
    val scannedContent: String = "",
    val scannedType: String = "",

    // Error and success states
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showError: Boolean = false,
    val showSuccess: Boolean = false,

    // Permission and camera states
    val hasCameraPermission: Boolean = false,
    val isCameraOpen: Boolean = false,

    // UI states
    val showDeleteDialog: Boolean = false,
    val showClearAllDialog: Boolean = false,
    val itemToDelete: QREntity? = null,
    val currentTab: Int = 0 // 0=Generator, 1=Scanner, 2=History
)

enum class HistoryType {
    ALL, GENERATED, SCANNED
}