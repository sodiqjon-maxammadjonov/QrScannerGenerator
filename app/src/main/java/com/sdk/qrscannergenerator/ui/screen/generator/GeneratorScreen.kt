package com.sdk.qrscannergenerator.ui.screen.generator

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(viewModel: QRViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("TEXT") }
    var isQR by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // QR vs Barcode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = isQR,
                onClick = { isQR = true },
                label = { Text("QR Code") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isQR,
                onClick = { isQR = false },
                label = { Text("Barcode") },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Content type selector
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = { },
                label = { Text("Turi") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Content input
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Mazmun") },
            placeholder = { Text("QR kod yoki barcode uchun mazmun kiriting") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = when (selectedType) {
                "PHONE" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                "EMAIL" -> KeyboardOptions(keyboardType = KeyboardType.Email)
                "URL" -> KeyboardOptions(keyboardType = KeyboardType.Uri)
                else -> KeyboardOptions(keyboardType = KeyboardType.Text)
            }
        )
        
        // Generate button
        Button(
            onClick = {
                if (content.isNotBlank()) {
                    if (isQR) {
                        viewModel.onEvent(QREvent.GenerateQR(content, selectedType))
                    } else {
                        viewModel.onEvent(QREvent.GenerateBarcode(content, selectedType))
                    }
                    content = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = content.isNotBlank() && !uiState.isGenerating
        ) {
            if (uiState.isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isQR) "QR Kod Yaratish" else "Barcode Yaratish")
        }
        
        // Generated QR display area
        if (uiState.generatedBitmap != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Yaratilgan ${if (isQR) "QR" else "Barcode"}")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { 
                                uiState.generatedBitmap?.let { bitmap ->
                                    viewModel.onEvent(QREvent.SaveToGallery(bitmap, "QR_${System.currentTimeMillis()}"))
                                }
                            },
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_save),
                                    contentDescription = "Save",
                                    modifier = Modifier
                                )
                            }
                        }
                        
                        Button(
                            onClick = { viewModel.onEvent(QREvent.ShareContent(uiState.lastGeneratedContent)) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "Save",
                                modifier = Modifier
                            )
                        }
                        
                        Button(
                            onClick = { viewModel.onEvent(QREvent.CopyToClipboard(uiState.lastGeneratedContent)) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy),
                                contentDescription = "Copy",
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}