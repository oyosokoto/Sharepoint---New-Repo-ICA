package com.tees.mad.e4089074.sharepoint.util


data class TransactionData(
    val id: String,
    val userId: String,
    val podId: String,
    val amount: Float,
    val status: String,
    val stripeSessionId: String?,
    val stripePaymentIntentId: String?,
    val createdAt: String,
    val updatedAt: String,
    val totalPodders: Int,
    val totalPodAmount: Float,
    val businessName: String
)