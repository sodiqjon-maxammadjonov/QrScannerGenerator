package com.sdk.qrscannergenerator.presentation.event

import android.graphics.Bitmap
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.presentation.state.HistoryType

sealed class QREvent {
    // Generation events
    data class GenerateQR(val content: String, val type: String) : QREvent()
    data class GenerateBarcode(val content: String, val type: String) : QREvent()

    // Scan events
    object StartScanning : QREvent()
    object StopScanning : QREvent()
    data class OnQRScanned(val content: String, val type: String) : QREvent()

    // History events
    object LoadHistory : QREvent()
    object LoadGeneratedHistory : QREvent()
    object LoadScannedHistory : QREvent()
    data class SearchHistory(val query: String) : QREvent()
    data class FilterByContentType(val type: String) : QREvent()
    data class FilterByHistoryType(val type: HistoryType) : QREvent()
    data class SelectQR(val qr: QREntity) : QREvent()

    // Delete events
    data class ShowDeleteDialog(val qr: QREntity) : QREvent()
    data class DeleteQR(val id: Int) : QREvent()
    object ShowClearAllDialog : QREvent()
    object ClearAllHistory : QREvent()
    object ClearGeneratedHistory : QREvent()
    object ClearScannedHistory : QREvent()

    // Save and share events
    data class SaveToGallery(val bitmap: Bitmap, val filename: String) : QREvent()
    data class ShareContent(val content: String) : QREvent()
    data class CopyToClipboard(val content: String) : QREvent()

    // Dialog events
    object HideDeleteDialog : QREvent()
    object HideClearAllDialog : QREvent()
    object HideError : QREvent()
    object HideSuccess : QREvent()

    // Permission events
    data class UpdateCameraPermission(val granted: Boolean) : QREvent()

    // Tab events
    data class ChangeTab(val tabIndex: Int) : QREvent()
}
