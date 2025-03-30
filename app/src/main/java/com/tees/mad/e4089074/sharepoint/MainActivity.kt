package com.tees.mad.e4089074.sharepoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.tees.mad.e4089074.sharepoint.ui.theme.SharepointTheme
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel

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
        val authViewModel: AuthViewModel by viewModels<AuthViewModel>()

        setContent {
            SharepointTheme {
                SharePoint(authViewModel = authViewModel)
            }
        }
    }
}