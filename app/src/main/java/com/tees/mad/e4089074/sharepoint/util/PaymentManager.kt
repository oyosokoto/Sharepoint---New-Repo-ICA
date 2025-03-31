package com.tees.mad.e4089074.sharepoint.util

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel
import kotlinx.coroutines.CoroutineScope

class PaymentManager(
    private val paymentViewModel: PaymentPodViewModel,
    private val snackBarHostState: SnackbarHostState,
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val TAG = "PaymentManager"

    fun initiatePayment(podId: String, amount: Double, userToken: String?) {
        if (userToken.isNullOrBlank()) {
            Log.e(TAG, "Cannot initiate payment: User token is null or blank")
            return
        }

        Log.d(TAG, "Initiating payment for pod: $podId, amount: $amount")
        paymentViewModel.createPaymentSession(
            podId = podId,
            amount = amount,
            authToken = "Bearer $userToken"
        )
    }
}