package com.sdk.qrscannergenerator.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.R
import com.sdk.qrscannergenerator.ui.screen.generator.GeneratorScreen
import com.sdk.qrscannergenerator.ui.screen.history.HistoryScreen
import com.sdk.qrscannergenerator.ui.screen.scanner.ScannerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: QRViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Scanner") },
                actions = {
                    // History count
                    if (uiState.historyCount > 0) {
                        AssistChip(
                            onClick = { viewModel.onEvent(QREvent.ChangeTab(2)) },
                            label = { Text("${uiState.historyCount}") },
                            leadingIcon = { Icon(painterResource(R.drawable.ic_history), null) }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = uiState.currentTab == 0,
                    onClick = { viewModel.onEvent(QREvent.ChangeTab(0)) },
                    icon = { Icon(painterResource(R.drawable.ic_qr), null) },
                    label = { Text("Yaratish") }
                )
                NavigationBarItem(
                    selected = uiState.currentTab == 1,
                    onClick = { viewModel.onEvent(QREvent.ChangeTab(1)) },
                    icon = { Icon(painterResource(R.drawable.ic_scan), null) },
                    label = { Text("Skanerlash") }
                )
                NavigationBarItem(
                    selected = uiState.currentTab == 2,
                    onClick = { viewModel.onEvent(QREvent.ChangeTab(2)) },
                    icon = { Icon(painterResource(R.drawable.ic_scan), null) },
                    label = { Text("Tarix") }
                )
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState.currentTab) {
                0 -> GeneratorScreen(viewModel = viewModel)
                1 -> ScannerScreen(viewModel = viewModel)
                2 -> HistoryScreen(viewModel = viewModel)
            }
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        // Error snackbar
        if (uiState.showError) {
            LaunchedEffect(uiState.errorMessage) {
                // Show snackbar
                viewModel.onEvent(QREvent.HideError)
            }
        }

        // Success snackbar
        if (uiState.showSuccess) {
            LaunchedEffect(uiState.successMessage) {
                // Show snackbar
                viewModel.onEvent(QREvent.HideSuccess)
            }
        }
    }
}