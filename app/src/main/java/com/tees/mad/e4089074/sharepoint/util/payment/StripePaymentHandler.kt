package com.tees.mad.e4089074.sharepoint.util.payment

import android.content.Context
import android.util.Log
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Utility class to handle Stripe payments
 */
class StripePaymentHandler(
    private val context: Context,
    private val publishableKey: String = "pk_test_51R8AI0DBiz5tAb0JNbQ44whYD2L6CquJaAoRuanobbmIgvDdNvkTEjBAztav033dd14EbshfqzozUCDERinO3Jex00dgadK5Hw" // TODO: Replace with your publishable key -  do not commit your key to repo
) {
    private val TAG = "StripePaymentHandler"
    
    // Payment sheet instance
    private val paymentSheet: PaymentSheet
    
    // State for tracking payment result
    private val _paymentResult = MutableStateFlow<PaymentResult>(PaymentResult.Idle)
    val paymentResult: StateFlow<PaymentResult> = _paymentResult.asStateFlow()
    
    // Sealed class to represent different payment results
    sealed class PaymentResult {
        object Idle : PaymentResult()
        object Loading : PaymentResult()
        object Completed : PaymentResult()
        data class Failed(val message: String) : PaymentResult()
        object Canceled : PaymentResult()
    }
    
    init {
        // Configure Stripe with publishable key
        PaymentConfiguration.init(context, publishableKey)
        
        // Initialize payment sheet in the constructor
        paymentSheet = PaymentSheet(
            context as androidx.activity.ComponentActivity,
            ::onPaymentSheetResult
        )
    }
    
    /**
     * Present the payment sheet to the user
     * @param clientSecret The client secret from the payment intent
     * @param merchantName The name of the merchant
     */
    fun presentPaymentSheet(clientSecret: String, merchantName: String = "SharePoint") {
        _paymentResult.value = PaymentResult.Loading
        
        try {
            val paymentSheetConfig = PaymentSheet.Configuration(
                merchantDisplayName = merchantName,
                customer = null,
                allowsDelayedPaymentMethods = false
            )
            
            Log.d(TAG, "Presenting payment sheet with client secret: ${clientSecret.take(10)}...")
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = clientSecret,
                configuration = paymentSheetConfig
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error presenting payment sheet", e)
            _paymentResult.value = PaymentResult.Failed("Error presenting payment sheet: ${e.localizedMessage}")
        }
    }
    
    /**
     * Handle payment sheet result
     */
    private fun onPaymentSheetResult(result: PaymentSheetResult) {
        Log.d(TAG, "Payment sheet result: $result")
        
        when (result) {
            is PaymentSheetResult.Completed -> {
                // Payment completed successfully
                _paymentResult.value = PaymentResult.Completed
            }
            is PaymentSheetResult.Canceled -> {
                // User canceled the payment
                _paymentResult.value = PaymentResult.Canceled
            }
            is PaymentSheetResult.Failed -> {
                // Payment failed
                _paymentResult.value = PaymentResult.Failed(
                    result.error.localizedMessage ?: "Payment failed"
                )
            }
        }
    }
    
    /**
     * Reset payment result state
     */
    fun resetPaymentResult() {
        _paymentResult.value = PaymentResult.Idle
    }
}
