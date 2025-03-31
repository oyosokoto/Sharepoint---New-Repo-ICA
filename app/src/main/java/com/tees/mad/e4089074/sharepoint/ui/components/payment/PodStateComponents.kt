package com.tees.mad.e4089074.sharepoint.ui.components.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem

@Composable
fun PodLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = PurpleDeep)
            Text(
                text = "Loading pods...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EmptyPodsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No Payment Pods Yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Join a pod using the button below or create a new pod from the expense screen.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PodsList(
    activePod: PodListItem?,
    podListItems: List<PodListItem>,
    expandedPodId: String?,
    onExpandPod: (String) -> Unit,
    onPayNow: (String) -> Unit,
    modifier: Modifier = Modifier,
    paymentAllowed: Boolean
) {
    // Implementation of the pod list display
    // This would contain the scrolling list of pod cards
    // Note: This implementation would depend on your PodCard composable which isn't shown in the original code
}