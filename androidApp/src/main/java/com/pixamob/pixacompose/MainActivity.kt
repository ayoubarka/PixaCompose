package com.pixamob.pixacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pixamob.pixacompose.theme.PixaTheme
import com.pixamob.pixacompose.demo.PixaComposeDemo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PixaComposeDemo()
                }
            }
        }
    }
}