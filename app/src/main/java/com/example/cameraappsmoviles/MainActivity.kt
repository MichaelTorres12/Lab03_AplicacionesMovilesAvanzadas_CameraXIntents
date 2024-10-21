package com.example.cameraappsmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cameraappsmoviles.ui.theme.CameraAppsMovilesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraAppsMovilesTheme {
                CameraApp()
            }
        }
    }
}
