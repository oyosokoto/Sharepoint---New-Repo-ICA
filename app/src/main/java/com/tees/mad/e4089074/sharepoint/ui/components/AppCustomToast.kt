package com.tees.mad.e4089074.sharepoint.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.ErrorRed
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleGrey40
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppCustomToast(
    message: String,
    isVisible: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {

    val backgroundColor = if (isError) ErrorRed else PurpleGrey40
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message, color = White)
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            scope.launch {
                delay(3000) // Adjust duration as needed
                onDismiss()
            }
        }
    }
}