package com.tees.mad.e4089074.sharepoint.ui.components.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.components.pods.EmptyPodsState
import com.tees.mad.e4089074.sharepoint.ui.components.pods.PodLoadingState
import com.tees.mad.e4089074.sharepoint.ui.components.pods.PodsList
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Payment Pods",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        windowInsets = TopAppBarDefaults.windowInsets.exclude(
            WindowInsets.statusBars
        )
    )
}

@Composable
fun JoinPodFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PurpleDeep,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Join Pod",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun CustomSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.padding(16.dp),
        snackbar = { data ->
            Snackbar(
                shape = RoundedCornerShape(10.dp),
                containerColor = Purple0,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionColor = PurpleDeep,
                snackbarData = data
            )
        }
    )
}

@Composable
fun PaymentScreenContent(
    podState: PaymentPodViewModel.PodState,
    podListItems: List<PodListItem>,
    activePodListItem: PodListItem?,
    expandedPodId: String?,
    onExpandPod: (String) -> Unit,
    onPayNow: (String) -> Unit,
    modifier: Modifier = Modifier,
    paymentAllowed: Boolean
) {
    when (podState) {
        is PaymentPodViewModel.PodState.Loading -> {
            PodLoadingState(modifier = modifier)
        }
        is PaymentPodViewModel.PodState.Error -> {
            if (podListItems.isEmpty()) {
                EmptyPodsState(
                    modifier = modifier.padding(horizontal = 24.dp)
                )
            } else {
                PodsList(
                    activePod = null,
                    podListItems = podListItems,
                    expandedPodId = expandedPodId,
                    onExpandPod = onExpandPod,
                    onPayNow = onPayNow,
                    modifier = modifier,
                    paymentAllowed = paymentAllowed
                )
            }
        }
        is PaymentPodViewModel.PodState.Success -> {
            PodsList(
                activePod = activePodListItem,
                podListItems = podListItems,
                expandedPodId = expandedPodId,
                onExpandPod = onExpandPod,
                onPayNow = onPayNow,
                modifier = modifier,
                paymentAllowed = paymentAllowed
            )
        }
        else -> {
            if (podListItems.isEmpty()) {
                EmptyPodsState(
                    modifier = modifier.padding(horizontal = 24.dp)
                )
            } else {
                PodsList(
                    activePod = null,
                    podListItems = podListItems,
                    expandedPodId = expandedPodId,
                    onExpandPod = onExpandPod,
                    onPayNow = onPayNow,
                    modifier = modifier,
                    paymentAllowed = paymentAllowed
                )
            }
        }
    }
}