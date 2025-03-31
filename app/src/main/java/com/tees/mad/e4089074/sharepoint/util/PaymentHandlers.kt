package com.tees.mad.e4089074.sharepoint.util

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun handlePayNowClick(
    podId: String,
    podState: PaymentPodViewModel.PodState,
    podListItems: List<PodListItem>,
    activePodListItem: PodListItem?,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onSelectPod: (String, Double) -> Unit,
    onShowCustomAmountDialog: () -> Unit,
    onInitiatePayment: (String, Double) -> Unit
) {
    val TAG = "PaymentHandler"

    when (podState) {
        is PaymentPodViewModel.PodState.Success -> {
            // For initial state, check pod's split type from list items
            val pod = activePodListItem
            Log.d(TAG, "Pay Now clicked in Initial state for pod: $podId")

            if (pod != null) {
                // Get the split type from the pod
                val splitType = pod.splitType
                Log.d(TAG, "Pod split type: $splitType")
                onSelectPod(podId, pod.yourShare)

                if (splitType == SplitType.CUSTOM.value) {
                    // For custom split, show dialog to enter amount
                    Log.d(TAG, "Custom split pod - showing amount dialog")
                    onShowCustomAmountDialog()
                } else {
                    // Initiate payment for equal/random splits
                    onInitiatePayment(podId, pod.yourShare)
                }
            } else {
                // Pod not found, show error
                Log.d(TAG, "Pod not found in list items")
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Pod not found",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        is PaymentPodViewModel.PodState.Error -> {
            // In error state, we don't know the split type
            Log.d(TAG, "Pay Now clicked in Error state for pod: $podId")

            // Find the pod in the list items
            val pod = podListItems.find { it.id == podId }
            if (pod != null) {
                onSelectPod(podId, pod.yourShare)

                if (pod.splitType == SplitType.CUSTOM.value) {
                    onShowCustomAmountDialog()
                } else {
                    // For non-custom splits, initiate payment directly
                    onInitiatePayment(podId, pod.yourShare)
                }
            } else {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Pod not found",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        else -> {
            // For initial state, check pod's split type from list items
            val pod = podListItems.find { it.id == podId }
            Log.d(TAG, "Pay Now clicked in Initial state for pod: $podId")

            if (pod != null) {
                // Get the split type from the pod
                val splitType = pod.splitType
                Log.d(TAG, "Pod split type: $splitType")
                onSelectPod(podId, pod.yourShare)

                if (splitType == SplitType.CUSTOM.value) {
                    // For custom split, show dialog to enter amount
                    Log.d(TAG, "Custom split pod - showing amount dialog")
                    onShowCustomAmountDialog()
                } else {
                    // Initiate payment for equal/random splits
                    onInitiatePayment(podId, pod.yourShare)
                }
            } else {
                // Pod not found, show error
                Log.d(TAG, "Pod not found in list items")
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Pod not found",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}