package com.tees.mad.e4089074.sharepoint.util

import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import com.tees.mad.e4089074.sharepoint.ui.components.AppCustomToast

fun showToast(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun ShowToast(context: android.content.Context, message: String, isError: Boolean = false) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val view = LocalView.current

    if (message.isNotEmpty()) {
        toastMessage = message
    }

    if (toastMessage != null) {
        AppCustomToast(
            message = toastMessage!!,
            isVisible = true,
            isError = isError,
            onDismiss = { toastMessage = null },
//            modifier = Modifier
        )
    }
}

fun formatNumber(number: Float): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.UK)
    return formatter.format(number)
}

fun isValidateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}