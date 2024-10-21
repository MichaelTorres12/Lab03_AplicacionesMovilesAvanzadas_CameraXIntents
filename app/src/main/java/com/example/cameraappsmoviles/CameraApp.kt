package com.example.cameraappsmoviles

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.*
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraApp() {
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showCamera) {
            // Mostrar la vista de la cámara
            CameraView(
                onImageCaptured = { uri ->
                    imageUri = uri
                    showCamera = false
                },
                onError = { exception ->
                    Toast.makeText(context, "Error al capturar imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                    showCamera = false
                }
            )
        } else {
            // Mostrar botones y vista previa de la imagen
            if (imageUri == null) {
                Button(onClick = {
                    // Comprobar y solicitar permisos
                    if (cameraPermissionState.status.isGranted) {
                        showCamera = true
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text("Tomar Foto")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar imagen capturada
            imageUri?.let {
                ImagePreview(imageUri = it.toString())
            }

            // Botones adicionales
            if (imageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Guardar imagen en galería
                    val saved = saveImageToGallery(context, imageUri.toString())
                    if (saved) {
                        Toast.makeText(context, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Guardar Imagen")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    // Compartir imagen
                    val imageFile = File(imageUri?.path ?: "")
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        imageFile
                    )

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        type = "image/jpeg"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartir imagen"))
                }) {
                    Text("Compartir Imagen")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    // Volver a tomar foto
                    imageUri = null
                }) {
                    Text("Volver a Tomar Foto")
                }
            }
        }
    }
}
