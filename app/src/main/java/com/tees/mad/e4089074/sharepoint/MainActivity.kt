package com.tees.mad.e4089074.sharepoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.tees.mad.e4089074.sharepoint.ui.theme.SharepointTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.White.toArgb(), // White background
                Color.White.toArgb(), // White background
            )
        )

        WindowCompat
            .getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars =
            true

        setContent {
            SharepointTheme {
                SharePoint()
            }
        }
    }
}