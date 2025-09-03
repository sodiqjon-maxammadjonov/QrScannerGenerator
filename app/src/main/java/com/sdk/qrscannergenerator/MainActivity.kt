package com.sdk.qrscannergenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.sdk.qrscannergenerator.ui.screen.main.MainScreen
import com.sdk.qrscannergenerator.ui.theme.QrScannerGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrScannerGeneratorTheme {
                Surface(

                ) {
                    MainScreen()
                }
            }
        }
    }
}
