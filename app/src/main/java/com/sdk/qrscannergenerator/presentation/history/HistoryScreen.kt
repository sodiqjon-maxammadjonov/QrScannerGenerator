//package com.sdk.qrscannergenerator.presentation.history
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.sdk.qrscannergenerator.presentation.viewmodel.QrViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HistoryScreen(
//    viewModel: QrViewModel = hiltViewModel()
//) {
//    val list by viewModel.scannedResults.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("History") })
//        }
//    ) { padding ->
//        if (list.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = androidx.compose.ui.Alignment.Center
//            ) {
//                Text("No history yet")
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding)
//            ) {
//                items(list) { item ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                            .clickable { /* Maybe open details */ },
//                        elevation = CardDefaults.cardElevation(4.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(12.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text(text = item)
//
//                            Row {
//                                IconButton(onClick = { viewModel.copyToClipboard(item) }) {
//                                    Icon(Icons.Default.Check, contentDescription = "Copy")
//                                }
//                                IconButton(onClick = { viewModel.shareText(item) }) {
//                                    Icon(Icons.Default.Share, contentDescription = "Share")
//                                }
//                                IconButton(onClick = { viewModel.deleteScannedResult(item) }) {
//                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
