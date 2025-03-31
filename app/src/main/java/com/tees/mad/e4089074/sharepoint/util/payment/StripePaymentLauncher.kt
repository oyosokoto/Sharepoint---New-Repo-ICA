package com.tees.mad.e4089074.sharepoint.util.payment

import android.util.Log
import androidx.activity.ComponentActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Utility class to handle Stripe payments
 */
object StripePaymentLauncher {
    private val TAG = "StripePaymentLauncher"
    
    // State for tracking payment result
    private val _paymentResult = MutableStateFlow<PaymentResult>(PaymentResult.Idle)
    val paymentResult: StateFlow<PaymentResult> = _paymentResult.asStateFlow()
    
    // Publishable key for Stripe
    private const val PUBLISHABLE_KEY = "pk_test_51R8AI0DBiz5tAb0JNbQ44whYD2L6CquJaAoRuanobbmIgvDdNvkTEjBAztav033dd14EbshfqzozUCDERinO3Jex00dgadK5Hw" // TODO: Replace with your publishable key -  do not commit your key to repo
    
    // Sealed class to represent different payment results
    sealed class PaymentResult {
        object Idle : PaymentResult()
        object Loading : PaymentResult()
        object Completed : PaymentResult()
        data class Failed(val message: String) : PaymentResult()
        object Canceled : PaymentResult()
    }
    
    // Store the client secret for later use
    private var pendingClientSecret: String? = null
    private var pendingMerchantName: String = "SharePoint"
    
    /**
     * Prepare the payment sheet with the client secret
     * This should be called before the activity is created
     * 
     * @param clientSecret The client secret from the payment intent
     * @param merchantName The name of the merchant
     */
    fun preparePayment(clientSecret: String, merchantName: String = "SharePoint") {
        pendingClientSecret = clientSecret
        pendingMerchantName = merchantName
        _paymentResult.value = PaymentResult.Loading
    }
    
    /**
     * Present the payment sheet to the user
     * This should be called from the activity's onCreate method
     * 
     * @param activity The activity to launch the payment sheet from
     */
    fun presentPreparedPayment(activity: ComponentActivity) {
        val clientSecret = pendingClientSecret
        if (clientSecret == null) {
            Log.e(TAG, "No pending client secret")
            _paymentResult.value = PaymentResult.Failed("No pending client secret")
            return
        }
        
        try {
            // Configure Stripe with publishable key
            PaymentConfiguration.init(activity.applicationContext, PUBLISHABLE_KEY)
            
            // Create payment sheet
            val paymentSheet = PaymentSheet(activity) { result ->
                handlePaymentSheetResult(result)
            }
            
            // Configure payment sheet
            val paymentSheetConfig = PaymentSheet.Configuration(
                merchantDisplayName = pendingMerchantName,
                customer = null,
                allowsDelayedPaymentMethods = false
            )
            
            // Present payment sheet
            Log.d(TAG, "Presenting payment sheet with client secret: ${clientSecret.take(10)}...")
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = clientSecret,
                configuration = paymentSheetConfig
            )
            
            // Clear the pending client secret
            pendingClientSecret = null
        } catch (e: Exception) {
            Log.e(TAG, "Error presenting payment sheet", e)
            _paymentResult.value = PaymentResult.Failed("Error presenting payment sheet: ${e.localizedMessage}")
        }
    }
    
    /**
     * Handle payment sheet result
     */
    private fun handlePaymentSheetResult(result: PaymentSheetResult) {
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
