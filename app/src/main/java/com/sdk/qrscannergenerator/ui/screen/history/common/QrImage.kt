package com.sdk.qrscannergenerator.ui.screen.history.common

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun QRImageFromPath(imagePath: String) {
    val bitmap = remember(imagePath) {
        val file = File(imagePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else null
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
