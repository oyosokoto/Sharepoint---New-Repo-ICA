package com.tees.mad.e4089074.sharepoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tees.mad.e4089074.sharepoint.ui.theme.SharepointTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharepointTheme {
                SharePoint()
            }
        }
    }
}
