package com.tees.mad.e4089074.sharepoint.util.datamodels

import com.google.firebase.Timestamp
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType


data class PodItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0
)

data class PaymentPod(
    val id: String? = null,
    val businessName: String = "",
    val items: List<PodItem> = listOf(),
    val totalAmount: Double = 0.0,
    val podderCount: Int = 0,
    val amountPerPodder: Double = 0.0,
    val splitType: String? = "",
    val splitAmounts: List<Double>? = null,
    val podCode: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val createdBy: String = "",
    val active: Boolean = true
)

data class PodderJoin(
    val podId: String = "",
    val userId: String = "",
    val joinedAt: String = "",
    val hasPaid: Boolean = false
)

data class PodListItem(
    val id: String,
    val businessName: String,
    val totalAmount: Double,
    val yourShare: Double,
    val progress: Float,
    val joinedCount: Int,
    val remainingCount: Int,
    val items: List<PodItem>,
    val isActive: Boolean,
    val date: String,
    val splitType: String = ""
)
