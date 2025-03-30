package com.tees.mad.e4089074.sharepoint.ui.components.pods

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep


@Composable
fun PodLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = PurpleDeep,
            modifier = Modifier.size(48.dp)
        )
    }
}
