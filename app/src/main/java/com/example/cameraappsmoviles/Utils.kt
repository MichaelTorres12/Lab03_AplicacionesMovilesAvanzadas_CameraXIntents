package com.example.cameraappsmoviles

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun saveImageToGallery(context: Context, imageUri: String): Boolean {
    val filename = "Imagen_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/CameraApp")
        } else {
            val imagesDir = "/sdcard/DCIM/CameraApp/"
            val file = java.io.File(imagesDir)
            if (!file.exists()) {
                file.mkdirs()
            }
            put(MediaStore.MediaColumns.DATA, imagesDir + filename)
        }
    }

    return try {
        val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream = uri?.let { context.contentResolver.openOutputStream(it) }

        inputStream?.copyTo(outputStream!!)
        inputStream?.close()
        outputStream?.close()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
