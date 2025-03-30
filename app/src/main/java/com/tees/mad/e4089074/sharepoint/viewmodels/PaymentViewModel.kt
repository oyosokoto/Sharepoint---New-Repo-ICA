package com.tees.mad.e4089074.sharepoint.viewmodels;

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tees.mad.e4089074.sharepoint.util.datamodels.PaymentPod
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodderJoin
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PaymentPodViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // State for tracking pod details
    private val _podState = MutableStateFlow<PodState>(PodState.Initial)
    val podState: StateFlow<PodState> = _podState.asStateFlow()

    // State for tracking previous pods
    private val _previousPods = MutableStateFlow<List<PaymentPod>>(emptyList())
    val previousPods: StateFlow<List<PaymentPod>> = _previousPods.asStateFlow()
    
    // State for tracking converted pod list items
    private val _podListItems = MutableStateFlow<List<PodListItem>>(emptyList())
    val podListItems: StateFlow<List<PodListItem>> = _podListItems.asStateFlow()
    
    // State for tracking active pod as PodListItem
    private val _activePodListItem = MutableStateFlow<PodListItem?>(null)
    val activePodListItem: StateFlow<PodListItem?> = _activePodListItem.asStateFlow()
    
    // State for tracking if payment is allowed
    private val _paymentAllowed = MutableStateFlow<Boolean>(false)
    val paymentAllowed: StateFlow<Boolean> = _paymentAllowed.asStateFlow()
    
    // State for tracking custom split amount
    private val _customSplitAmount = MutableStateFlow<Double>(0.0)
    val customSplitAmount: StateFlow<Double> = _customSplitAmount.asStateFlow()

    // Sealed class to represent different states of pod loading and joining
    sealed class PodState {
        object Initial : PodState()
        object Loading : PodState()
        data class Success(
            val pod: PaymentPod,
            val totalPodders: Int,
            val remainingPodders: Int
        ) : PodState()

        data class Error(val message: String) : PodState()
    }

    // Function to join a pod
    fun joinPod(podCode: String, userId: String) {
        if (userId.isBlank()) {
            _podState.value = PodState.Error("User not logged in")
            return
        }
        viewModelScope.launch {
            _podState.value = PodState.Loading
            try {
                // First, find the pod by code
                val podQuery = firestore.collection("pods")
                    .whereEqualTo("podCode", podCode)
                    .limit(1)
                    .get()
                    .await()

                if (podQuery.isEmpty) {
                    _podState.value = PodState.Error("Pod not found")
                    return@launch
                }

                // Get the first (and only) pod document
                val podDocument = podQuery.documents.first()
                val pod = podDocument.toObject(PaymentPod::class.java)
                    ?: throw Exception("Failed to parse pod data")

                // Check if user has already joined this pod
                val existingPodderQuery = firestore.collection("pods")
                    .document(podDocument.id)
                    .collection("podders")
                    .whereEqualTo("userId", userId)
                    .limit(1)
                    .get()
                    .await()

                if (!existingPodderQuery.isEmpty) {
                    _podState.value = PodState.Error("You have already joined this pod")
                    return@launch
                }

                // Create a new podder join entry in a subcollection
                val podderJoin = PodderJoin(
                    podId = podDocument.id,
                    userId = userId,
                    joinedAt = System.currentTimeMillis().toString(),
                    hasPaid = false
                )

                // Add to podders subcollection
                firestore.collection("pods")
                    .document(podDocument.id)
                    .collection("podders")
                    .add(podderJoin)
                    .await()

                // Count total and remaining podders
                val poddersSnapshot = firestore.collection("pods")
                    .document(podDocument.id)
                    .collection("podders")
                    .get()
                    .await()

                val totalPodders = poddersSnapshot.size()
                val remainingPodders = pod.podderCount - totalPodders

                // Update state with pod details
                _podState.value = PodState.Success(
                    pod = pod,
                    totalPodders = totalPodders,
                    remainingPodders = remainingPodders
                )
                
                // Explicitly set payment allowed to true for this pod (unless it's a custom split)
                _paymentAllowed.value = pod.splitType != SplitType.CUSTOM.value
                Log.d("PaymentPodViewModel", "Setting payment allowed to ${_paymentAllowed.value} after joining pod")
                
                // Update active pod list item
                updateActivePodListItem(pod, totalPodders, remainingPodders)

                // Refresh previous pods after joining a new pod
                fetchPreviousPods(userId)

            } catch (e: Exception) {
                _podState.value = PodState.Error(
                    "Failed to join pod: ${e.localizedMessage}"
                )
            }
        }
    }

    // Real-time listener for pod updates
    fun observePodUpdates(podId: String) {
        firestore.collection("pods")
            .document(podId)
            .collection("podders")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _podState.value = PodState.Error(
                        "Error observing pod: ${e.localizedMessage}"
                    )
                    return@addSnapshotListener
                }

                // Update podder count in real-time
                val totalPodders = snapshot?.size() ?: 0
                val currentState = _podState.value
                if (currentState is PodState.Success) {
                    val updatedState = currentState.copy(
                        totalPodders = totalPodders,
                        remainingPodders = currentState.pod.podderCount - totalPodders
                    )
                    _podState.value = updatedState
                    
                    // Update active pod list item with new counts
                    updateActivePodListItem(
                        updatedState.pod, 
                        updatedState.totalPodders, 
                        updatedState.remainingPodders
                    )
                }
            }
    }

    // Function to fetch user's previous pods
    fun fetchPreviousPods(userId: String) {
        if (userId.isBlank()) {
            Log.w("PaymentPodViewModel", "Cannot fetch pods: User not logged in")
            return
        }

        viewModelScope.launch {
            try {
                // Get all pods from the main collection
                val podsSnapshot = firestore.collection("pods")
                    .get()
                    .await()

                if (podsSnapshot.isEmpty) {
                    _previousPods.value = emptyList()
                    return@launch
                }

                val allPods = mutableListOf<PaymentPod>()

                // For each pod, check if the user is a podder
                for (podDoc in podsSnapshot.documents) {
                    // Check if user exists in this pod's podders subcollection
                    val podderSnapshot = firestore.collection("pods")
                        .document(podDoc.id)
                        .collection("podders")
                        .whereEqualTo("userId", userId)
                        .limit(1)  // We only need to know if the user exists
                        .get()
                        .await()

                    // Skip if user is not a podder in this pod
                    if (podderSnapshot.isEmpty) continue

                    // Get the pod data
                    val pod = podDoc.toObject(PaymentPod::class.java)
                    if (pod != null) {
                        // Get all podders to determine progress
                        val allPoddersSnapshot = firestore.collection("pods")
                            .document(podDoc.id)
                            .collection("podders")
                            .get()
                            .await()

                        val joinedCount = allPoddersSnapshot.size()
                        val remainingCount = pod.podderCount - joinedCount
                        val progress = joinedCount.toFloat() / pod.podderCount

                        // Get the user's joining timestamp
                        val podderDoc = podderSnapshot.documents.firstOrNull()
                        val joinedAt = podderDoc?.getString("joinedAt")?.toLongOrNull() ?: 0L
                        val date = formatDate(joinedAt)

                        // Check if this pod is active (currently in podState)
                        val currentState = _podState.value
                        val isActive = if (currentState is PodState.Success) {
                            podDoc.id == currentState.pod.id
                        } else {
                            false
                        }

                        // Skip adding to previous pods if it's the active pod
                        if (!isActive) {
                            allPods.add(
                                PaymentPod(
                                    id = podDoc.id,
                                    businessName = pod.businessName,
                                    totalAmount = pod.totalAmount,
                                    podCode = pod.podCode,
                                    items = pod.items,
                                    active = pod.active,
                                    createdAt = pod.createdAt,
                                    createdBy = pod.createdBy,
                                    splitType = pod.splitType,
                                    updatedAt = pod.updatedAt,
                                    splitAmounts = pod.splitAmounts,
                                    podderCount = pod.podderCount,
                                    amountPerPodder = pod.amountPerPodder
                                )
                            )
                        }
                    }
                }

                _previousPods.value = allPods
                Log.d("PaymentPodViewModel", "Fetched ${allPods.size} previous pods")
                
                // Convert previous pods to PodListItems
                convertPreviousPodsToListItems(allPods)

            } catch (e: Exception) {
                Log.e("PaymentPodViewModel", "Error fetching previous pods", e)
                _previousPods.value = emptyList()
            }
        }
    }

    // Helper function to format timestamps to readable dates
    private fun formatDate(timestamp: Long): String {
        if (timestamp == 0L) return "Unknown"

        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return when {
            isSameDay(now, date) -> "Today"
            isYesterday(now, date) -> "Yesterday"
            isSameWeek(now, date) -> "This week"
            isSameMonth(now, date) -> {
                val format = SimpleDateFormat("MMM d", Locale.getDefault())
                format.format(date.time)
            }

            else -> {
                val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                format.format(date.time)
            }
        }
    }

    // Helper date comparison functions
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(today: Calendar, other: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, other)
    }

    private fun isSameWeek(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isSameMonth(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
    
    // Helper function to update the active pod list item
    private fun updateActivePodListItem(pod: PaymentPod, totalPodders: Int, remainingPodders: Int) {
        viewModelScope.launch {
            val progress = if (pod.podderCount > 0) {
                totalPodders.toFloat() / pod.podderCount
            } else {
                0f
            }
            
            // Determine if payment is allowed based on pod state
            checkIfPaymentAllowed(pod, totalPodders, remainingPodders)
            
            _activePodListItem.value = PodListItem(
                id = pod.id ?: "",
                businessName = pod.businessName,
                totalAmount = pod.totalAmount,
                yourShare = if (pod.splitType == SplitType.CUSTOM.value) _customSplitAmount.value else pod.amountPerPodder,
                progress = progress,
                joinedCount = totalPodders,
                remainingCount = remainingPodders,
                items = pod.items,
                isActive = pod.active,
                date = pod.createdAt?.toDate()?.toString() ?: "Today",
                splitType = pod.splitType ?: ""
            )
        }
    }
    
    // Function to check if payment is allowed
    private suspend fun checkIfPaymentAllowed(pod: PaymentPod, totalPodders: Int, remainingPodders: Int) {
        val TAG = "PaymentPodViewModel"
        
        // IMPORTANT: Default to allowing payment for all split types except custom
        // This ensures random and equal splits can be paid immediately
        _paymentAllowed.value = pod.splitType != SplitType.CUSTOM.value
        
        // Log the current state for debugging
        Log.d(TAG, "Checking payment allowed: pod=${pod.id}, active=${pod.active}, splitType=${pod.splitType}")
        Log.d(TAG, "Initial payment allowed value: ${_paymentAllowed.value}")
        
        // Check if pod is active
        if (!pod.active) {
            Log.d(TAG, "Payment not allowed: Pod is not active")
            _paymentAllowed.value = false
            return
        }
        
        // Check if user has already paid
        val currentUser = getCurrentUserId()
        if (currentUser.isNotBlank() && pod.id != null) {
            val podderQuery = firestore.collection("pods")
                .document(pod.id)
                .collection("podders")
                .whereEqualTo("userId", currentUser)
                .limit(1)
                .get()
                .await()
                
            if (!podderQuery.isEmpty) {
                val podderDoc = podderQuery.documents.first()
                val hasPaid = podderDoc.getBoolean("hasPaid") ?: false
                
                if (hasPaid) {
                    Log.d(TAG, "Payment not allowed: User has already paid")
                    _paymentAllowed.value = false
                    return
                }
            } else {
                // User is not a podder in this pod
                Log.d(TAG, "Payment not allowed: User is not a podder in this pod")
                _paymentAllowed.value = false
                return
            }
        } else {
            // No current user or pod ID
            Log.d(TAG, "Payment not allowed: No current user or pod ID")
            _paymentAllowed.value = false
            return
        }
        
        // For custom split type, all podders must join and total must match
        if (pod.splitType == SplitType.CUSTOM.value) {
            // All podders must have joined
            if (remainingPodders > 0) {
                Log.d(TAG, "Payment not allowed for custom split: Not all podders have joined")
                _paymentAllowed.value = false
                return
            }
            
            // Check if total of all custom amounts equals the pod total
            val poddersSnapshot = firestore.collection("pods")
                .document(pod.id ?: "")
                .collection("podders")
                .get()
                .await()
                
            var totalCustomAmount = 0.0
            poddersSnapshot.documents.forEach { doc ->
                val customAmount = doc.getDouble("customAmount") ?: 0.0
                totalCustomAmount += customAmount
            }
            
            // Allow payment only if total matches (with small rounding tolerance)
            val totalMatches = Math.abs(totalCustomAmount - pod.totalAmount) < 0.01
            if (!totalMatches) {
                Log.d(TAG, "Payment not allowed for custom split: Total amounts don't match. Custom: $totalCustomAmount, Pod: ${pod.totalAmount}")
                _paymentAllowed.value = false
                return
            }
            
            // For custom split, explicitly set to true if we passed all checks
            _paymentAllowed.value = true
        }
        
        // If we got here, payment is allowed
        Log.d(TAG, "Final payment allowed value: ${_paymentAllowed.value}")
    }
    
    // Helper function to convert previous pods to list items
    private suspend fun convertPreviousPodsToListItems(pods: List<PaymentPod>) {
        val listItems = mutableListOf<PodListItem>()
        val currentUser = getCurrentUserId()
        
        for (pod in pods) {
            try {
                // Get all podders to determine progress
                val poddersSnapshot = firestore.collection("pods")
                    .document(pod.id ?: "")
                    .collection("podders")
                    .get()
                    .await()
                
                val joinedCount = poddersSnapshot.size()
                val remainingCount = pod.podderCount - joinedCount
                val progress = if (pod.podderCount > 0) {
                    joinedCount.toFloat() / pod.podderCount
                } else {
                    0f
                }
                
                // Check if current user has a custom amount for this pod
                var yourShare = pod.amountPerPodder
                if (pod.splitType == SplitType.CUSTOM.value && currentUser.isNotBlank()) {
                    val userPodderDoc = poddersSnapshot.documents.find { 
                        it.getString("userId") == currentUser 
                    }
                    
                    if (userPodderDoc != null) {
                        val customAmount = userPodderDoc.getDouble("customAmount")
                        if (customAmount != null) {
                            yourShare = customAmount
                        }
                    }
                }
                
                // Check if user has already paid for this pod
                var isActive = pod.active
                if (currentUser.isNotBlank()) {
                    val userPodderDoc = poddersSnapshot.documents.find { 
                        it.getString("userId") == currentUser 
                    }
                    
                    if (userPodderDoc != null && userPodderDoc.getBoolean("hasPaid") == true) {
                        // If user has paid, mark as inactive for this user
                        isActive = false
                    }
                }
                
                listItems.add(
                    PodListItem(
                        id = pod.id ?: "",
                        businessName = pod.businessName,
                        totalAmount = pod.totalAmount,
                        yourShare = yourShare,
                        progress = progress,
                        joinedCount = joinedCount,
                        remainingCount = remainingCount,
                        items = pod.items,
                        isActive = isActive,
                        date = pod.createdAt?.toDate()?.toString() ?: "Previous",
                        splitType = pod.splitType ?: ""
                    )
                )
            } catch (e: Exception) {
                Log.e("PaymentPodViewModel", "Error converting pod to list item", e)
            }
        }
        
        _podListItems.value = listItems
    }
    
    // Helper function to get current user ID
    private fun getCurrentUserId(): String {
        return try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            auth.currentUser?.uid ?: ""
        } catch (e: Exception) {
            Log.e("PaymentPodViewModel", "Error getting current user", e)
            ""
        }
    }
    
    // Function to set custom split amount for current user
    fun setCustomSplitAmount(podId: String, amount: Double) {
        val userId = getCurrentUserId()
        if (userId.isBlank() || podId.isBlank() || amount <= 0) {
            return
        }
        
        viewModelScope.launch {
            try {
                // Update the custom amount in Firestore
                val podderQuery = firestore.collection("pods")
                    .document(podId)
                    .collection("podders")
                    .whereEqualTo("userId", userId)
                    .limit(1)
                    .get()
                    .await()
                    
                if (!podderQuery.isEmpty) {
                    val podderDoc = podderQuery.documents.first()
                    firestore.collection("pods")
                        .document(podId)
                        .collection("podders")
                        .document(podderDoc.id)
                        .update("customAmount", amount)
                        .await()
                        
                    _customSplitAmount.value = amount
                    
                    // Refresh pod state to update payment allowed status
                    val currentState = _podState.value
                    if (currentState is PodState.Success && currentState.pod.id == podId) {
                        checkIfPaymentAllowed(
                            currentState.pod,
                            currentState.totalPodders,
                            currentState.remainingPodders
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PaymentPodViewModel", "Error setting custom amount", e)
            }
        }
    }
    
    // Function to process payment
    fun processPayment(podId: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank() || podId.isBlank() || !_paymentAllowed.value) {
            return
        }
        
        viewModelScope.launch {
            try {
                // Mark the user as paid in the podders collection
                val podderQuery = firestore.collection("pods")
                    .document(podId)
                    .collection("podders")
                    .whereEqualTo("userId", userId)
                    .limit(1)
                    .get()
                    .await()
                    
                if (!podderQuery.isEmpty) {
                    val podderDoc = podderQuery.documents.first()
                    firestore.collection("pods")
                        .document(podId)
                        .collection("podders")
                        .document(podderDoc.id)
                        .update("hasPaid", true)
                        .await()
                        
                    // Refresh pod state
                    val currentState = _podState.value
                    if (currentState is PodState.Success && currentState.pod.id == podId) {
                        checkIfPaymentAllowed(
                            currentState.pod,
                            currentState.totalPodders,
                            currentState.remainingPodders
                        )
                        
                        // Refresh the pod list items
                        fetchPreviousPods(userId)
                    }
                }
            } catch (e: Exception) {
                Log.e("PaymentPodViewModel", "Error processing payment", e)
            }
        }
    }
}
