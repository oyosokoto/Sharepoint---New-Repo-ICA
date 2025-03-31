package com.tees.mad.e4089074.sharepoint.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentActivity
import com.tees.mad.e4089074.sharepoint.util.payment.StripePaymentLauncher
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PaymentProcessingEffect(
    paymentProcessingState: PaymentPodViewModel.PaymentProcessingState,
    onShowDialog: (Boolean) -> Unit,
    selectedPodId: String,
    context: Context,
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    LaunchedEffect(paymentProcessingState) {
        when (paymentProcessingState) {
            is PaymentPodViewModel.PaymentProcessingState.Loading -> {
                onShowDialog(true)
            }

            is PaymentPodViewModel.PaymentProcessingState.Success -> {
                onShowDialog(false)
                val state = paymentProcessingState
                Log.d("PaymentScreen", "Payment session created successfully: ${state.clientSecret}")

                // Launch the PaymentActivity to handle the payment
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_CLIENT_SECRET, state.clientSecret)
                    putExtra(PaymentActivity.EXTRA_MERCHANT_NAME, "SharePoint")
                }
                context.startActivity(intent)
            }

            is PaymentPodViewModel.PaymentProcessingState.Error -> {
                onShowDialog(false)
                val state = paymentProcessingState
                Log.e("PaymentScreen", "Payment processing error: ${state.message}")

                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Payment error: ${state.message}",
                        duration = SnackbarDuration.Long
                    )
                }
            }

            else -> {
                // Idle state, do nothing
            }
        }
    }
}

@Composable
fun StripePaymentResultEffect(
    selectedPodId: String,
    paymentViewModel: PaymentPodViewModel,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val paymentResult by StripePaymentLauncher.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        when (paymentResult) {
            is StripePaymentLauncher.PaymentResult.Completed -> {
                Log.d("PaymentScreen", "Payment completed successfully")

                // Mark payment as complete in Firestore
                paymentViewModel.markPaymentComplete(selectedPodId)

                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Payment successful!",
                        duration = SnackbarDuration.Short
                    )
                }

                // Reset payment result
                StripePaymentLauncher.resetPaymentResult()
            }

            is StripePaymentLauncher.PaymentResult.Failed -> {
                val result = paymentResult as StripePaymentLauncher.PaymentResult.Failed
                Log.e("PaymentScreen", "Payment failed: ${result.message}")

                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Payment failed: ${result.message}",
                        duration = SnackbarDuration.Long
                    )
                }

                // Reset payment result
                StripePaymentLauncher.resetPaymentResult()
            }

            is StripePaymentLauncher.PaymentResult.Canceled -> {
                Log.d("PaymentScreen", "Payment canceled by user")

                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Payment canceled",
                        duration = SnackbarDuration.Short
                    )
                }

                // Reset payment result
                StripePaymentLauncher.resetPaymentResult()
            }

            else -> {
                // Idle or Loading state, do nothing
            }
        }
    }
}