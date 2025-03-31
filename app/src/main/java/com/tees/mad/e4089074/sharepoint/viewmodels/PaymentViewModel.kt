package com.tees.mad.e4089074.sharepoint.viewmodels;

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tees.mad.e4089074.sharepoint.util.AppConfig
import com.tees.mad.e4089074.sharepoint.util.datamodels.PaymentPod
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodderJoin
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType
import com.tees.mad.e4089074.sharepoint.util.network.CreatePaymentSessionRequest
import com.tees.mad.e4089074.sharepoint.util.network.PaymentApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

class PaymentPodViewModel : ViewModel() {
    private val TAG = "PaymentPodViewModel"
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

    // State for tracking payment processing
    private val _paymentProcessingState =
        MutableStateFlow<PaymentProcessingState>(PaymentProcessingState.Idle)
    val paymentProcessingState: StateFlow<PaymentProcessingState> =
        _paymentProcessingState.asStateFlow()

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

    // Sealed class to represent different states of payment processing
    sealed class PaymentProcessingState {
        object Idle : PaymentProcessingState()
        object Loading : PaymentProcessingState()
        data class Success(
            val clientSecret: String,
            val paymentIntentId: String,
            val transactionId: String
        ) : PaymentProcessingState()

        data class Error(val message: String) : PaymentProcessingState()
    }

    // Function to join a pod
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

                // Check if pod is active
                if (!pod.active) {
                    _podState.value = PodState.Error("This pod is no longer active")
                    return@launch
                }

                // Check if the pod is full by counting existing podders
                val poddersSnapshot = firestore.collection("pods")
                    .document(podDocument.id)
                    .collection("podders")
                    .get()
                    .await()

                val currentPodders = poddersSnapshot.size()

                // Check if pod is full
                if (currentPodders >= pod.podderCount) {
                    _podState.value = PodState.Error("This pod is already full")
                    return@launch
                }

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
                val updatedPoddersSnapshot = firestore.collection("pods")
                    .document(podDocument.id)
                    .collection("podders")
                    .get()
                    .await()

                val totalPodders = updatedPoddersSnapshot.size()
                val remainingPodders = pod.podderCount - totalPodders

                // Update state with pod details
                _podState.value = PodState.Success(
                    pod = pod,
                    totalPodders = totalPodders,
                    remainingPodders = remainingPodders
                )

                // Explicitly set payment allowed to true for this pod (unless it's a custom split)
                _paymentAllowed.value = pod.splitType != SplitType.CUSTOM.value
                Log.d(TAG, "Setting payment allowed to ${_paymentAllowed.value} after joining pod")

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

    // Store the pod listener registration for cleanup
    private var podListenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null
    
    // Real-time listener for pod updates
    fun observePodUpdates(podId: String) {
        // Clean up any existing listener first
        cleanupPodListener()
        
        // Set up a new listener
        podListenerRegistration = firestore.collection("pods")
            .document(podId)
            .addSnapshotListener { podSnapshot, podError ->
                if (podError != null) {
                    Log.e(TAG, "Error observing pod document", podError)
                    _podState.value = PodState.Error(
                        "Error observing pod: ${podError.localizedMessage}"
                    )
                    return@addSnapshotListener
                }
                
                // Check if pod is still active
                val pod = podSnapshot?.toObject(PaymentPod::class.java)
                if (pod != null && !pod.active) {
                    Log.d(TAG, "Pod is no longer active: ${pod.id}")
                    // If pod is no longer active, clear the active pod list item
                    _activePodListItem.value = null
                    
                    // Also update the pod state to reflect the change
                    val currentState = _podState.value
                    if (currentState is PodState.Success && currentState.pod.id == podId) {
                        // Create updated pod object with active status set to false
                        val updatedPod = currentState.pod.copy(active = false)
                        _podState.value = PodState.Success(
                            pod = updatedPod,
                            totalPodders = currentState.totalPodders,
                            remainingPodders = currentState.remainingPodders
                        )
                    }
                    
                    // Refresh the pod list items
                    val userId = getCurrentUserId()
                    if (userId.isNotBlank()) {
                        fetchPreviousPods(userId)
                    }
                }
                
                // Also listen to podders collection for payment updates
                firestore.collection("pods")
                    .document(podId)
                    .collection("podders")
                    .addSnapshotListener { poddersSnapshot, e ->
                        if (e != null) {
                            Log.e(TAG, "Error observing podders", e)
                            _podState.value = PodState.Error(
                                "Error observing pod: ${e.localizedMessage}"
                            )
                            return@addSnapshotListener
                        }
                        
                        // Update podder count in real-time
                        val totalPodders = poddersSnapshot?.size() ?: 0
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
                            
                            // Check if current user has paid
                            val userId = getCurrentUserId()
                            if (userId.isNotBlank()) {
                                val userPodder = poddersSnapshot?.documents?.find {
                                    it.getString("userId") == userId
                                }
                                
                                if (userPodder != null && userPodder.getBoolean("hasPaid") == true) {
                                    Log.d(TAG, "User has paid for pod: $podId")
                                    // Update payment allowed state
                                    _paymentAllowed.value = false
                                }
                            }
                        }
                    }
            }
    }
    
    // Clean up pod listener when leaving the screen
    fun cleanupPodListener() {
        podListenerRegistration?.remove()
        podListenerRegistration = null
    }

    // Function to fetch user's previous pods
    fun fetchPreviousPods(userId: String) {
        if (userId.isBlank()) {
            Log.w(TAG, "Cannot fetch pods: User not logged in")
            // Set to Initial state if no user ID
            _podState.value = PodState.Initial
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
                    // Set to Initial state if no pods found
                    _podState.value = PodState.Initial
                    return@launch
                }

                val allPods = mutableListOf<PaymentPod>()
                var foundActivePod = false

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

                        // Check if this pod is active
                        if (pod.active && podderDoc?.getBoolean("hasPaid") != true) {
                            // Found an active pod that the user hasn't paid for
                            foundActivePod = true
                            
                            // Update pod state with this active pod
                            _podState.value = PodState.Success(
                                pod = pod.copy(id = podDoc.id),
                                totalPodders = joinedCount,
                                remainingPodders = remainingCount
                            )
                            
                            // Update active pod list item
                            updateActivePodListItem(pod.copy(id = podDoc.id), joinedCount, remainingCount)
                        } else {
                            // Add to previous pods list
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
                Log.d(TAG, "Fetched ${allPods.size} previous pods")

                // Convert previous pods to PodListItems
                convertPreviousPodsToListItems(allPods)
                
                // If no active pod was found, set state to Initial
                if (!foundActivePod && _podState.value is PodState.Loading) {
                    _podState.value = PodState.Initial
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching previous pods", e)
                _previousPods.value = emptyList()
                // Set to Initial state on error
                _podState.value = PodState.Initial
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
    private suspend fun checkIfPaymentAllowed(
        pod: PaymentPod,
        totalPodders: Int,
        remainingPodders: Int
    ) {
        // IMPORTANT: Default to allowing payment for all split types except custom
        // This ensures random and equal splits can be paid immediately
        _paymentAllowed.value = pod.splitType != SplitType.CUSTOM.value

        // Log the current state for debugging
        Log.d(
            TAG,
            "Checking payment allowed: pod=${pod.id}, active=${pod.active}, splitType=${pod.splitType}"
        )
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
                val hasPaid = podderDoc.getBoolean("hasPaid") == true

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
                .document(pod.id)
                .collection("podders")
                .get()
                .await()

            var totalCustomAmount = 0.0
            poddersSnapshot.documents.forEach { doc ->
                val customAmount = doc.getDouble("customAmount") ?: 0.0
                totalCustomAmount += customAmount
            }

            // Allow payment only if total matches (with small rounding tolerance)
            val totalMatches = abs(totalCustomAmount - pod.totalAmount) < 0.01
            if (!totalMatches) {
                Log.d(
                    TAG,
                    "Payment not allowed for custom split: Total amounts don't match. Custom: $totalCustomAmount, Pod: ${pod.totalAmount}"
                )
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
                Log.e(TAG, "Error converting pod to list item", e)
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
            Log.e(TAG, "Error getting current user", e)
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
                Log.e(TAG, "Error setting custom amount", e)
            }
        }
    }

    // Function to create payment session and get Stripe client secret
    fun createPaymentSession(podId: String, amount: Double, authToken: String) {
        if (podId.isBlank() || amount <= 0 || authToken.isBlank()) {
            _paymentProcessingState.value =
                PaymentProcessingState.Error(
                    "Unable to initiate payment session. " +
                            "Please contact support!"
                )
            Log.d(TAG, "Invalid payment parameters")
            return
        }

        viewModelScope.launch {
            try {

                val currentUser = getCurrentUserId()
                if (currentUser.isNotBlank()) {
                    val podderQuery = firestore.collection("pods")
                        .document(podId)
                        .collection("podders")
                        .whereEqualTo("userId", currentUser)
                        .limit(1)
                        .get()
                        .await()

                    if (!podderQuery.isEmpty) {
                        val podderDoc = podderQuery.documents.first()
                        val hasPaid = podderDoc.getBoolean("hasPaid") == true
                        if (hasPaid) {
                            _paymentProcessingState.value = PaymentProcessingState.Error(
                                "You have already paid for this pod."
                            )
                            return@launch
                        }
                    }

                } else {
                    return@launch
                }

                _paymentProcessingState.value = PaymentProcessingState.Loading

                // Configure API client with the base URL
                val apiBaseUrl = AppConfig.getPaymentApiUrl()
                PaymentApiClient.setBaseUrl(apiBaseUrl)

                // Create the request
                val request = CreatePaymentSessionRequest(
                    amount = amount,
                    podId = podId
                )

                // Make the API call
                val response = PaymentApiClient.getService().createPaymentSession(
                    authToken = authToken,
                    request = request
                )

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!

                    // Update payment processing state with client secret
                    _paymentProcessingState.value = PaymentProcessingState.Success(
                        clientSecret = responseBody.data.clientSecret,
                        paymentIntentId = responseBody.data.paymentIntentId,
                        transactionId = responseBody.data.transactionId
                    )

                    Log.d(
                        TAG,
                        "Payment session created successfully: ${responseBody.data.clientSecret}"
                    )
                } else {
                    // Handle error response
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _paymentProcessingState.value = PaymentProcessingState.Error(
                        "Failed to create payment session: $errorMessage"
                    )
                    Log.e(TAG, "Failed to create payment session: $errorMessage")
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                _paymentProcessingState.value = PaymentProcessingState.Error(
                    "Error creating payment session: ${e.localizedMessage}"
                )
                Log.e(TAG, "Error creating payment session", e)
            }
        }
    }

    // Function to process payment after successful Stripe payment
    // Function to process payment after successful Stripe payment
    fun markPaymentComplete(podId: String) {
        val userId = getCurrentUserId()
        if (userId.isBlank() || podId.isBlank()) {
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
                    
                    Log.d(TAG, "Payment marked as complete for pod: ${podderDoc.id}")
                    
                    // The observePodUpdates listener will handle UI updates automatically
                    // when the Firestore data changes
                    
                    // Reset payment processing state
                    _paymentProcessingState.value = PaymentProcessingState.Idle
                    
                    // Update payment allowed state immediately for better UX
                    _paymentAllowed.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking payment complete", e)
            }
        }
    }

    // Reset payment processing state
    fun resetPaymentProcessingState() {
        _paymentProcessingState.value = PaymentProcessingState.Idle
    }
    
    // Set loading state for initial pod fetch
    fun setLoadingState() {
        _podState.value = PodState.Loading
    }
}
