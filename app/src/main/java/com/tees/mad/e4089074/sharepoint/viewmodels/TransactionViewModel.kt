package com.tees.mad.e4089074.sharepoint.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tees.mad.e4089074.sharepoint.util.TransactionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TransactionViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _transactions = MutableStateFlow<List<TransactionData>>(emptyList())
    val transactions: StateFlow<List<TransactionData>> = _transactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val transactionsSnapshot = firestore.collection("transactions")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val transactionsList = transactionsSnapshot.documents.mapNotNull { document ->
                    try {
                        val data = document.data ?: return@mapNotNull null
                        var createdAt = data["createdAt"] as? Timestamp
                        val updatedAt = data["updatedAt"] as? Timestamp
                        TransactionData(
                            id = document.id,
                            userId = data["userId"] as? String ?: "",
                            podId = data["podId"] as? String ?: "",
                            amount = (data["amount"] as? Number)?.toFloat() ?: 0f,
                            status = data["status"] as? String ?: "",
                            stripeSessionId = data["stripeSessionId"] as? String,
                            stripePaymentIntentId = data["stripePaymentIntentId"] as? String,
                            createdAt = createdAt?.toDate()
                                .toString(),
                            updatedAt = updatedAt?.toDate().toString(),
                            totalPodders = (data["totalPodders"] as? Number)?.toInt() ?: 0,
                            totalPodAmount = (data["totalPodAmount"] as? Number)?.toFloat() ?: 0f,
                            businessName = data["businessName"] as? String ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "TransactionViewModel",
                            "Error parsing transaction document: ${document.id}",
                            e
                        )
                        null
                    }
                }

                _transactions.value = transactionsList
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Failed to fetch transactions", e)
                _error.value = "Failed to load transactions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRecentTransactions(count: Int): List<TransactionData> {
        return transactions.value.take(count)
    }
}
