package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.tees.mad.e4089074.sharepoint.ui.theme.SharepointTheme
import com.tees.mad.e4089074.sharepoint.util.payment.StripePaymentLauncher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PaymentActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_CLIENT_SECRET = "client_secret"
        const val EXTRA_MERCHANT_NAME = "merchant_name"
        private const val TAG = "PaymentActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val clientSecret = intent.getStringExtra(EXTRA_CLIENT_SECRET)
        val merchantName = intent.getStringExtra(EXTRA_MERCHANT_NAME) ?: "SharePoint"
        
        if (clientSecret != null) {
            StripePaymentLauncher.preparePayment(clientSecret, merchantName)
            StripePaymentLauncher.presentPreparedPayment(this)
        } else {
            finish()
            return
        }
        
        setContent {
            SharepointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            StripePaymentLauncher.paymentResult.collectLatest { result ->
                Log.d(TAG, "Payment result: $result")
                when (result) {
                    is StripePaymentLauncher.PaymentResult.Completed,
                    is StripePaymentLauncher.PaymentResult.Failed,
                    is StripePaymentLauncher.PaymentResult.Canceled -> {
                        Log.d(TAG, "Payment completed, failed, or canceled. Finishing activity.")
                        finish()
                    }
                    else -> {
                    }
                }
            }
        }
    }
}
