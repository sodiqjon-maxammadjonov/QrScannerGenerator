package com.sdk.qrscannergenerator.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
    val dir = File(context.filesDir, "qr_images")
    if (!dir.exists()) dir.mkdirs()

    val file = File(dir, "$fileName.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file.absolutePath
}
