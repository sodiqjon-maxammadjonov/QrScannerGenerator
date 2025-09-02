package com.sdk.qrscannergenerator.ui.screen.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.R

@Composable
fun ScannerScreen(viewModel: QRViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Camera permission check
        if (!uiState.hasCameraPermission) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kamera ruxsati kerak")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { 
                            // TODO: Request camera permission in UI layer
                        }
                    ) {
                        Text("Ruxsat berish")
                    }
                }
            }
        } else {
            
            // Camera preview area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isCameraOpen) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (uiState.isScanning) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Scanning...")
                            }
                            Text("Kamera ko'rinishi bu yerda bo'ladi")
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_scan),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Scanning uchun kamerani yoqing")
                        }
                    }
                }
            }
            
            // Scan controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        if (uiState.isCameraOpen) {
                            viewModel.onEvent(QREvent.StopScanning)
                        } else {
                            viewModel.onEvent(QREvent.StartScanning)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isScanning
                ) {
                    Icon(
                        painter = if (uiState.isCameraOpen) {
                            painterResource(id = R.drawable.ic_stop)
                        } else {
                            rememberVectorPainter(Icons.Default.PlayArrow)
                        },
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.isCameraOpen) "To'xtatish" else "Boshlash")
                }
            }
            
            // Last scanned result
            if (uiState.scannedContent.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Scan natijasi:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.scannedContent,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Action buttons for scanned content
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (viewModel.isUrl(uiState.scannedContent)) {
                                Button(
                                    onClick = { 
                                        // TODO: Open URL in browser
                                    }
                                ) {
                                    Icon(painterResource(R.drawable.ic_chrome), null)
                                }
                            }
                            
                            Button(
                                onClick = { 
                                    viewModel.onEvent(QREvent.ShareContent(uiState.scannedContent)) 
                                }
                            ) {
                                Icon(Icons.Default.Share, null)
                            }
                            
                            Button(
                                onClick = { 
                                    viewModel.onEvent(QREvent.CopyToClipboard(uiState.scannedContent)) 
                                }
                            ) {
                                Icon(painterResource(R.drawable.ic_copy), null)
                            }
                        }
                    }
                }
            }
        }
    }
}