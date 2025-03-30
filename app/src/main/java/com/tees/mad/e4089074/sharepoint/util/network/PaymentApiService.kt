package com.tees.mad.e4089074.sharepoint.util.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Data class for payment session request
 */
data class CreatePaymentSessionRequest(
    val amount: Double,
    val podId: String
)

/**
 * Data class for payment session response
 */
data class CreatePaymentSessionResponse(
    val responseCode: String,
    val responseMessage: String,
    val data: PaymentSessionData
)

/**
 * Data class for payment session data
 */
data class PaymentSessionData(
    val clientSecret: String,
    val paymentIntentId: String,
    val processorReference: String,
    val transactionId: String
)

/**
 * Interface for payment API endpoints
 */
interface PaymentApiService {
    @POST("api/v1/payment/create-session")
    suspend fun createPaymentSession(
        @Header("Authorization") authToken: String,
        @Body request: CreatePaymentSessionRequest
    ): Response<CreatePaymentSessionResponse>
}

/**
 * Singleton object to provide the payment API service
 */
object PaymentApiClient {
    private var apiBaseUrl = "http://localhost:3000/"
    private var instance: PaymentApiService? = null

    /**
     * Set the base URL for the API
     */
    fun setBaseUrl(url: String) {
        apiBaseUrl = url
        // Reset instance so it will be recreated with new URL
        instance = null
    }

    /**
     * Get the payment API service instance
     */
    fun getService(): PaymentApiService {
        return instance ?: synchronized(this) {
            val retrofit = Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val newInstance = retrofit.create(PaymentApiService::class.java)
            instance = newInstance
            newInstance
        }
    }
}
