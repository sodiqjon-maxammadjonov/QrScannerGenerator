package com.sdk.qrscannergenerator.presentation.viewmodel

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.domain.usecase.*
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.state.QRUiState
import com.sdk.qrscannergenerator.presentation.state.HistoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val insertQR: InsertQR,
    private val getHistory: GetHistory,
    private val getGeneratedHistory: GetGeneratedHistory,
    private val getScannedHistory: GetScannedHistory,
    private val searchHistory: SearchHistory,
    private val getQRById: GetQRById,
    private val deleteQr: DeleteQr,
    private val clearHistory: ClearHistory,
    private val clearGeneratedHistory: ClearGeneratedHistory,
    private val clearScannedHistory: ClearScannedHistory,
    private val getHistoryCount: GetHistoryCount
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRUiState())
    val uiState: StateFlow<QRUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: QREvent) {
        when (event) {
            // Generation events
            is QREvent.GenerateQR -> generateQR(event.content, event.type)
            is QREvent.GenerateBarcode -> generateBarcode(event.content, event.type)

            // Scan events
            QREvent.StartScanning -> startScanning()
            QREvent.StopScanning -> stopScanning()
            is QREvent.OnQRScanned -> onQRScanned(event.content, event.type)

            // History events
            QREvent.LoadHistory -> loadAllHistory()
            QREvent.LoadGeneratedHistory -> loadGeneratedHistory()
            QREvent.LoadScannedHistory -> loadScannedHistory()
            is QREvent.SearchHistory -> searchHistoryByQuery(event.query)
            is QREvent.FilterByContentType -> filterByContentType(event.type)
            is QREvent.FilterByHistoryType -> filterByHistoryType(event.type)
            is QREvent.SelectQR -> selectQR(event.qr)

            // Delete events
            is QREvent.ShowDeleteDialog -> showDeleteDialog(event.qr)
            is QREvent.DeleteQR -> deleteQRItem(event.id)
            QREvent.ShowClearAllDialog -> showClearAllDialog()
            QREvent.ClearAllHistory -> clearAllHistory()
            QREvent.ClearGeneratedHistory -> clearGeneratedHistoryData()
            QREvent.ClearScannedHistory -> clearScannedHistoryData()

            // Save and share events
            is QREvent.SaveToGallery -> saveToGallery(event.bitmap, event.filename)
            is QREvent.ShareContent -> shareContent(event.content)
            is QREvent.CopyToClipboard -> copyToClipboard(event.content)

            // Dialog events
            QREvent.HideDeleteDialog -> hideDeleteDialog()
            QREvent.HideClearAllDialog -> hideClearAllDialog()
            QREvent.HideError -> hideError()
            QREvent.HideSuccess -> hideSuccess()

            // Permission events
            is QREvent.UpdateCameraPermission -> updateCameraPermission(event.granted)

            // Tab events
            is QREvent.ChangeTab -> changeTab(event.tabIndex)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Load history count
                val count = getHistoryCount()
                _uiState.update {
                    it.copy(
                        historyCount = count,
                        isLoading = false
                    )
                }
                // Load default history
                loadAllHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Ma'lumotlarni yuklashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    // ==================== GENERATION METHODS ====================
    private fun generateQR(content: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            try {
                // TODO: QR generation logic here (using ZXing library)
                // val bitmap = generateQRBitmap(content)

                // Save to database
                insertQR(
                    content = content,
                    type = type,
                    qrType = "QR_CODE",
                    isGenerated = true
                )

                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        lastGeneratedContent = content,
                        // generatedBitmap = bitmap,
                        successMessage = "QR kod muvaffaqiyatli yaratildi!",
                        showSuccess = true
                    )
                }

                // Refresh history
                loadAllHistory()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = "QR kod yaratishda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun generateBarcode(content: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            try {
                // TODO: Barcode generation logic here
                // val bitmap = generateBarcodeBitmap(content)

                insertQR(
                    content = content,
                    type = type,
                    qrType = "BARCODE",
                    isGenerated = true
                )

                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        lastGeneratedContent = content,
                        successMessage = "Barcode muvaffaqiyatli yaratildi!",
                        showSuccess = true
                    )
                }

                loadAllHistory()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = "Barcode yaratishda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    // ==================== SCANNING METHODS ====================
    private fun startScanning() {
        _uiState.update {
            it.copy(
                isScanning = true,
                isCameraOpen = true,
                scannedContent = "",
                scannedType = ""
            )
        }
    }

    private fun stopScanning() {
        _uiState.update {
            it.copy(
                isScanning = false,
                isCameraOpen = false
            )
        }
    }

    private fun onQRScanned(content: String, type: String) {
        viewModelScope.launch {
            try {
                // Save scanned result
                insertQR(
                    content = content,
                    type = type,
                    qrType = if (type.contains("BARCODE", true)) "BARCODE" else "QR_CODE",
                    isGenerated = false
                )

                _uiState.update {
                    it.copy(
                        isScanning = false,
                        isCameraOpen = false,
                        scannedContent = content,
                        scannedType = type,
                        successMessage = "Muvaffaqiyatli scan qilindi!",
                        showSuccess = true
                    )
                }

                // Refresh history
                loadAllHistory()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        isCameraOpen = false,
                        errorMessage = "Scan natijasini saqlashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    // ==================== HISTORY METHODS ====================
    private fun loadAllHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getHistory().collect { historyList ->
                    val count = getHistoryCount()
                    _uiState.update {
                        it.copy(
                            allHistory = historyList,
                            filteredHistory = filterHistoryByCurrentFilters(historyList),
                            historyCount = count,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tarixni yuklashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun loadGeneratedHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getGeneratedHistory().collect { historyList ->
                    _uiState.update {
                        it.copy(
                            generatedHistory = historyList,
                            filteredHistory = filterHistoryByCurrentFilters(historyList),
                            selectedHistoryType = HistoryType.GENERATED,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Generated tarixni yuklashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun loadScannedHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getScannedHistory().collect { historyList ->
                    _uiState.update {
                        it.copy(
                            scannedHistory = historyList,
                            filteredHistory = filterHistoryByCurrentFilters(historyList),
                            selectedHistoryType = HistoryType.SCANNED,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Scanned tarixni yuklashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun searchHistoryByQuery(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true) }
            try {
                if (query.isBlank()) {
                    // Show all based on current filter
                    when (_uiState.value.selectedHistoryType) {
                        HistoryType.ALL -> loadAllHistory()
                        HistoryType.GENERATED -> loadGeneratedHistory()
                        HistoryType.SCANNED -> loadScannedHistory()
                    }
                } else {
                    searchHistory(query).collect { searchResults ->
                        _uiState.update {
                            it.copy(
                                filteredHistory = filterHistoryByCurrentFilters(searchResults),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Qidirishda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun filterByContentType(type: String) {
        _uiState.update {
            it.copy(
                selectedContentType = type,
                filteredHistory = filterHistoryByCurrentFilters(getCurrentHistoryList())
            )
        }
    }

    private fun filterByHistoryType(type: HistoryType) {
        when (type) {
            HistoryType.ALL -> loadAllHistory()
            HistoryType.GENERATED -> loadGeneratedHistory()
            HistoryType.SCANNED -> loadScannedHistory()
        }
    }

    private fun selectQR(qr: QREntity) {
        _uiState.update { it.copy(selectedQR = qr) }
    }

    // ==================== DELETE METHODS ====================
    private fun showDeleteDialog(qr: QREntity) {
        _uiState.update {
            it.copy(
                showDeleteDialog = true,
                itemToDelete = qr
            )
        }
    }

    private fun deleteQRItem(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            try {
                deleteQr(id)
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        showDeleteDialog = false,
                        itemToDelete = null,
                        successMessage = "Element muvaffaqiyatli o'chirildi!",
                        showSuccess = true
                    )
                }

                // Refresh current view
                refreshCurrentHistory()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = "O'chirishda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun showClearAllDialog() {
        _uiState.update { it.copy(showClearAllDialog = true) }
    }

    private fun clearAllHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                clearHistory()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showClearAllDialog = false,
                        allHistory = emptyList(),
                        filteredHistory = emptyList(),
                        historyCount = 0,
                        successMessage = "Barcha tarix tozalandi!",
                        showSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tarixni tozalashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun clearGeneratedHistoryData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                clearGeneratedHistory()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generatedHistory = emptyList(),
                        successMessage = "Yaratilgan QR kodlar tarixi tozalandi!",
                        showSuccess = true
                    )
                }
                refreshCurrentHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Generated tarixni tozalashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun clearScannedHistoryData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                clearScannedHistory()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        scannedHistory = emptyList(),
                        successMessage = "Scan qilingan kodlar tarixi tozalandi!",
                        showSuccess = true
                    )
                }
                refreshCurrentHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Scanned tarixni tozalashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    // ==================== SAVE AND SHARE METHODS ====================
    private fun saveToGallery(bitmap: Bitmap, filename: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "${filename}_$timestamp.png"

                // Save to Pictures/QRScanner folder
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val qrDir = File(picturesDir, "QRScanner")
                if (!qrDir.exists()) {
                    qrDir.mkdirs()
                }

                val file = File(qrDir, fileName)
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()

                // Notify media scanner
                MediaScannerConnection.scanFile(
                    /* context = */ null, // You'll need to pass context from UI
                    arrayOf(file.absolutePath),
                    arrayOf("image/png"),
                    null
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Rasm galereyaga saqlandi!",
                        showSuccess = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Rasmni saqlashda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    private fun shareContent(content: String) {
        try {
            // This will be handled in the UI layer with Intent
            _uiState.update {
                it.copy(
                    successMessage = "Share oynasi ochilmoqda...",
                    showSuccess = true
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = "Share qilishda xatolik: ${e.message}",
                    showError = true
                )
            }
        }
    }

    private fun copyToClipboard(content: String) {
        try {
            // This will be handled in the UI layer with ClipboardManager
            _uiState.update {
                it.copy(
                    successMessage = "Clipboard'ga nusxalandi!",
                    showSuccess = true
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = "Clipboard'ga nusxalashda xatolik: ${e.message}",
                    showError = true
                )
            }
        }
    }

    // ==================== HELPER METHODS ====================
    private fun filterHistoryByCurrentFilters(historyList: List<QREntity>): List<QREntity> {
        val currentState = _uiState.value
        var filtered = historyList

        // Filter by content type
        if (currentState.selectedContentType != "ALL") {
            filtered = filtered.filter { it.type == currentState.selectedContentType }
        }

        return filtered
    }

    private fun getCurrentHistoryList(): List<QREntity> {
        return when (_uiState.value.selectedHistoryType) {
            HistoryType.ALL -> _uiState.value.allHistory
            HistoryType.GENERATED -> _uiState.value.generatedHistory
            HistoryType.SCANNED -> _uiState.value.scannedHistory
        }
    }

    private fun refreshCurrentHistory() {
        when (_uiState.value.selectedHistoryType) {
            HistoryType.ALL -> loadAllHistory()
            HistoryType.GENERATED -> loadGeneratedHistory()
            HistoryType.SCANNED -> loadScannedHistory()
        }
    }

    // ==================== DIALOG METHODS ====================
    private fun hideDeleteDialog() {
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                itemToDelete = null
            )
        }
    }

    private fun hideClearAllDialog() {
        _uiState.update { it.copy(showClearAllDialog = false) }
    }

    private fun hideError() {
        _uiState.update {
            it.copy(
                showError = false,
                errorMessage = null
            )
        }
    }

    private fun hideSuccess() {
        _uiState.update {
            it.copy(
                showSuccess = false,
                successMessage = null
            )
        }
    }

    // ==================== PERMISSION METHODS ====================
    private fun updateCameraPermission(granted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = granted) }
    }

    // ==================== TAB METHODS ====================
    private fun changeTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }

        // Load appropriate data for the tab
        when (tabIndex) {
            2 -> { // History tab
                if (_uiState.value.allHistory.isEmpty()) {
                    loadAllHistory()
                }
            }
        }
    }

    // ==================== UTILITY METHODS ====================
    fun getQRDetails(id: Int) {
        viewModelScope.launch {
            try {
                val qr = getQRById(id)
                _uiState.update { it.copy(selectedQR = qr) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "QR ma'lumotlarini olishda xatolik: ${e.message}",
                        showError = true
                    )
                }
            }
        }
    }

    // Format date for UI
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Get QR type icon or description
    fun getQRTypeDescription(qrType: String): String {
        return when (qrType) {
            "QR_CODE" -> "QR Kod"
            "BARCODE" -> "Barcode"
            else -> "Noma'lum"
        }
    }

    // Get content type description
    fun getContentTypeDescription(type: String): String {
        return when (type.uppercase()) {
            "URL" -> "Veb sahifa"
            "TEXT" -> "Matn"
            "WIFI" -> "Wi-Fi"
            "EMAIL" -> "Email"
            "PHONE" -> "Telefon"
            "SMS" -> "SMS"
            "CONTACT" -> "Kontakt"
            else -> type
        }
    }

    // Check if content is URL
    fun isUrl(content: String): Boolean {
        return content.startsWith("http://") || content.startsWith("https://")
    }

    // Reset all filters
    fun resetFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedContentType = "ALL",
                selectedHistoryType = HistoryType.ALL
            )
        }
        loadAllHistory()
    }
}