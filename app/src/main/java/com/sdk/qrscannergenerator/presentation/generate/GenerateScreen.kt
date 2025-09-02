//package com.sdk.qrscannergenerator.presentation.generate
//
//import android.graphics.Bitmap
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.sdk.qrscannergenerator.presentation.viewmodel.QrViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GenerateScreen(
//    viewModel: QrViewModel = hiltViewModel()
//) {
//    var text by remember { mutableStateOf("") }
//    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
//
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("Generate QR") }) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top
//        ) {
//            OutlinedTextField(
//                value = text,
//                onValueChange = { text = it },
//                label = { Text("Enter text") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    qrBitmap = viewModel.generateQrCode(text)
//                    viewModel.saveGeneratedResult(text)
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Generate")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            qrBitmap?.let { bmp ->
//                Image(
//                    bitmap = bmp.asImageBitmap(),
//                    contentDescription = "Generated QR",
//                    modifier = Modifier
//                        .size(200.dp)
//                        .padding(16.dp)
//                )
//
//                Row(
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Button(onClick = { viewModel.shareQr(bmp) }) {
//                        Text("Share")
//                    }
//                    Button(onClick = { viewModel.saveQrToGallery(bmp) }) {
//                        Text("Save")
//                    }
//                }
//            }
//        }
//    }
//}
