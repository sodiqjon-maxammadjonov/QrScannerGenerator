package com.sdk.qrscannergenerator.ui.screen.scanner

import android.Manifest
import android.media.MediaScannerConnection
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sdk.qrscannergenerator.R
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.ui.screen.scanner.common.CameraPreview
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(viewModel: QRViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!cameraPermissionState.status.isGranted) {
            // Kamera ruxsat so'rov
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(painterResource(R.drawable.ic_camera), contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kamera ruxsati kerak")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Ruxsat berish")
                    }
                }
            }
        } else {
            // Kamera preview
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (uiState.isCameraOpen) {
                        CameraPreview(
                            modifier = Modifier.fillMaxSize(),
                            lifecycleOwner = lifecycleOwner,
                            onScanResult = { result ->
                                viewModel.onEvent(QREvent.OnQRScanned(result, ""))
                            }
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painterResource(R.drawable.ic_scan), contentDescription = null, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Scanning uchun kamerani yoqing")
                        }
                    }
                }
            }

            // Boshlash / To'xtatish tugmasi
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (uiState.isCameraOpen) viewModel.onEvent(QREvent.StopScanning)
                        else viewModel.onEvent(QREvent.StartScanning)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isScanning
                ) {
                    Icon(
                        painter = if (uiState.isCameraOpen) painterResource(R.drawable.ic_stop) else rememberVectorPainter(Icons.Default.PlayArrow),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.isCameraOpen) "To'xtatish" else "Boshlash")
                }
            }

            // Oxirgi skan natija
            if (uiState.scannedContent.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Scan natijasi:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = uiState.scannedContent, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (viewModel.isUrl(uiState.scannedContent)) {
                                Button(onClick = {
                                    // TODO: Open URL in browser
                                }) { Icon(painterResource(R.drawable.ic_chrome), contentDescription = null) }
                            }
                            Button(onClick = { viewModel.onEvent(QREvent.ShareContent(uiState.scannedContent)) }) {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                            Button(onClick = { copyToClipboard(context, uiState.scannedContent) }) {
                                Icon(painterResource(R.drawable.ic_copy), contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Faylni tizimga qo'shish / MediaScanner
fun scanFile(context: android.content.Context, file: File) {
    MediaScannerConnection.scanFile(
        context,
        arrayOf(file.absolutePath),
        null
    ) { path, uri ->
        Log.d("ScannerScreen", "Scanned $path -> $uri")
    }
}

// Clipboardga nusxalash
fun copyToClipboard(context: android.content.Context, text: String) {
    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
    val clip = android.content.ClipData.newPlainText("QR Content", text)
    clipboard.setPrimaryClip(clip)
}
