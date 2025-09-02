package com.sdk.qrscannergenerator.ui.screen.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.state.HistoryType
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.R
import com.sdk.qrscannergenerator.ui.screen.history.common.HistoryItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: QRViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        
        // Search and filters
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.onEvent(QREvent.SearchHistory(it))
                    },
                    label = { Text("Qidirish") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = { 
                                    searchQuery = ""
                                    viewModel.onEvent(QREvent.SearchHistory(""))
                                }
                            ) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Filter chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedHistoryType == HistoryType.ALL,
                        onClick = { viewModel.onEvent(QREvent.FilterByHistoryType(HistoryType.ALL)) },
                        label = { Text("Barchasi") }
                    )
                    FilterChip(
                        selected = uiState.selectedHistoryType == HistoryType.GENERATED,
                        onClick = { viewModel.onEvent(QREvent.FilterByHistoryType(HistoryType.GENERATED)) },
                        label = { Text("Yaratilgan") }
                    )
                    FilterChip(
                        selected = uiState.selectedHistoryType == HistoryType.SCANNED,
                        onClick = { viewModel.onEvent(QREvent.FilterByHistoryType(HistoryType.SCANNED)) },
                        label = { Text("Scan qilingan") }
                    )
                }
            }
        }
        
        // Action buttons
        if (uiState.filteredHistory.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(QREvent.ShowClearAllDialog) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete",
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Barchasini o'chirish")
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painterResource(R.drawable.ic_history),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tarix bo'sh")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredHistory) { qr ->
                    HistoryItemCard(
                        qr = qr,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(QREvent.HideDeleteDialog) },
            title = { Text("O'chirish") },
            text = { Text("Bu elementni o'chirishni xohlaysizmi?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        uiState.itemToDelete?.let {
                            viewModel.onEvent(QREvent.DeleteQR(it.id))
                        }
                    },
                    enabled = !uiState.isDeleting
                ) {
                    if (uiState.isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("O'chirish")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(QREvent.HideDeleteDialog) }
                ) {
                    Text("Bekor qilish")
                }
            }
        )
    }
    
    // Clear all confirmation dialog
    if (uiState.showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(QREvent.HideClearAllDialog) },
            title = { Text("Barchasini o'chirish") },
            text = { Text("Barcha tarixni o'chirishni xohlaysizmi? Bu amalni qaytarib bo'lmaydi.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(QREvent.ClearAllHistory) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("O'chirish")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(QREvent.HideClearAllDialog) }
                ) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}
