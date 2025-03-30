package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.components.pods.EmptyPodsState
import com.tees.mad.e4089074.sharepoint.ui.components.pods.JoinPodDialog
import com.tees.mad.e4089074.sharepoint.ui.components.pods.PodLoadingState
import com.tees.mad.e4089074.sharepoint.ui.components.pods.PodsList
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType
import com.tees.mad.e4089074.sharepoint.util.payment.StripePaymentLauncher
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel
import com.tees.mad.e4089074.sharepoint.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch


/**
 * PaymentScreen is the main screen for handling payment pods.
 * It allows users to:
 * 1. View their active pod (if any)
 * 2. View their previous pods
 * 3. Join a new pod using a pod code
 * 4. Make payments for pods they've joined
 *
 * The payment flow varies based on the pod's split type:
 * - For EQUAL and RANDOM splits: Users can pay directly
 * - For CUSTOM splits: Users must enter their share amount, and all members must join
 *   with amounts that sum to the total pod amount before payment is allowed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    profileViewModel: ProfileViewModel = viewModel(),
    paymentViewModel: PaymentPodViewModel = viewModel()
) {
    // Tag for logging
    val TAG = "PaymentScreen"
    val context = LocalContext.current

    // Get the current activity for Stripe payment
    val activity = context as androidx.activity.ComponentActivity

    // View model states
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val podState by paymentViewModel.podState.collectAsState()
    val activePodListItem by paymentViewModel.activePodListItem.collectAsStateWithLifecycle()
    val podListItems by paymentViewModel.podListItems.collectAsStateWithLifecycle()
    val paymentAllowed by paymentViewModel.paymentAllowed.collectAsStateWithLifecycle()
    val userToken by profileViewModel.userToken.collectAsStateWithLifecycle()
    val customSplitAmount by paymentViewModel.customSplitAmount.collectAsStateWithLifecycle()
    val paymentProcessingState by paymentViewModel.paymentProcessingState.collectAsStateWithLifecycle()
    
    // Helper function to initiate payment
    fun initiatePayment(podId: String, amount: Double) {
        val token = userToken
        if (token.isNullOrBlank()) {
            Log.e(TAG, "Cannot initiate payment: User token is null or blank")
            return
        }
        
        Log.d(TAG, "Initiating payment for pod: $podId, amount: $amount")
        paymentViewModel.createPaymentSession(
            podId = podId,
            amount = amount,
            authToken = "Bearer $token"
        )
    }

    // Stripe payment result
    val paymentResult by StripePaymentLauncher.paymentResult.collectAsState()

    // Log payment allowed state changes
    LaunchedEffect(paymentAllowed) {
        Log.d(TAG, "Payment allowed state changed: $paymentAllowed")
    }

    // UI state
    var showJoinPodDialog by remember { mutableStateOf(false) }
    var showCustomAmountDialog by remember { mutableStateOf(false) }
    var showPaymentProcessingDialog by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("") }
    var podCode by remember { mutableStateOf("") }
    var isCodeError by remember { mutableStateOf(false) }
    var isCustomAmountError by remember { mutableStateOf(false) }
    var selectedPodId by remember { mutableStateOf("") }
    var selectedPodAmount by remember { mutableStateOf(0.0) }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Track expanded pod cards
    var expandedPodId by remember { mutableStateOf<String?>(null) }

    // Fetch user's pods when the screen is first displayed
    LaunchedEffect(Unit) {
        Log.d(TAG, "Fetching pods for user: ${userProfile?.userId}")
        paymentViewModel.fetchPreviousPods(userProfile?.userId ?: "")
    }

    // Handle payment processing state changes
    LaunchedEffect(paymentProcessingState) {
        when (paymentProcessingState) {
            is PaymentPodViewModel.PaymentProcessingState.Loading -> {
                showPaymentProcessingDialog = true
            }
            is PaymentPodViewModel.PaymentProcessingState.Success -> {
                showPaymentProcessingDialog = false
                val state = paymentProcessingState as PaymentPodViewModel.PaymentProcessingState.Success
                Log.d(TAG, "Payment session created successfully: ${state.clientSecret}")

                // Launch the PaymentActivity to handle the payment
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_CLIENT_SECRET, state.clientSecret)
                    putExtra(PaymentActivity.EXTRA_MERCHANT_NAME, "SharePoint")
                }
                context.startActivity(intent)
            }
            is PaymentPodViewModel.PaymentProcessingState.Error -> {
                showPaymentProcessingDialog = false
                val state = paymentProcessingState as PaymentPodViewModel.PaymentProcessingState.Error
                Log.e(TAG, "Payment processing error: ${state.message}")

                scope.launch {
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

    // Handle Stripe payment result
    LaunchedEffect(paymentResult) {
        when (paymentResult) {
            is StripePaymentLauncher.PaymentResult.Completed -> {
                Log.d(TAG, "Payment completed successfully")

                // Mark payment as complete in Firestore
//                paymentViewModel.markPaymentComplete(selectedPodId)

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
                Log.e(TAG, "Payment failed: ${result.message}")

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
                Log.d(TAG, "Payment canceled by user")

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

    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            paymentViewModel.resetPaymentProcessingState()
            StripePaymentLauncher.resetPaymentResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment Pods",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                windowInsets = TopAppBarDefaults.windowInsets.exclude(
                    androidx.compose.foundation.layout.WindowInsets.statusBars
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showJoinPodDialog = true },
                containerColor = PurpleDeep,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Join Pod",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(16.dp),
                snackbar = { data ->
                    Snackbar(
                        shape = RoundedCornerShape(10.dp),
                        containerColor = Purple0,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        actionColor = PurpleDeep,
                        snackbarData = data
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (val state = podState) {
            is PaymentPodViewModel.PodState.Loading -> {
                PodLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is PaymentPodViewModel.PodState.Error -> {
                LaunchedEffect(state) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message,
                            duration = SnackbarDuration.Long
                        )
                    }
                }

                if (podListItems.isEmpty()) {
                    EmptyPodsState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 24.dp)
                    )
                } else {
                    // Use the podListItems from the ViewModel
                    PodsList(
                        activePod = null,
                        podListItems = podListItems,
                        expandedPodId = expandedPodId,
                        onExpandPod = { podId ->
                            expandedPodId = if (expandedPodId == podId) null else podId
                        },
                        onPayNow = { podId ->
                            // In error state, we don't know the split type
                            // So we'll just show the custom amount dialog to be safe
                            Log.d(TAG, "Pay Now clicked in Error state for pod: $podId")
                            selectedPodId = podId

                            // Find the pod in the list items
                            val pod = podListItems.find { it.id == podId }
                            if (pod != null) {
                                selectedPodAmount = pod.yourShare

                                if (pod.splitType == SplitType.CUSTOM.value) {
                                    customAmountText = customSplitAmount.toString()
                                    showCustomAmountDialog = true
                                } else {
                                    // For non-custom splits, initiate payment directly
                                    initiatePayment(podId, pod.yourShare)
                                }
                            } else {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "Pod not found",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        paymentAllowed = paymentAllowed
                    )
                }
            }

            is PaymentPodViewModel.PodState.Success -> {
                // Use the activePodListItem from the ViewModel
                PodsList(
                    activePod = activePodListItem,
                    podListItems = podListItems,
                    expandedPodId = expandedPodId,
                    onExpandPod = { podId ->
                        expandedPodId = if (expandedPodId == podId) null else podId
                    },
                    onPayNow = { podId ->
                        Log.d(TAG, "Pay Now clicked with paymentAllowed: $paymentAllowed")
                        selectedPodId = podId

                        // Check if this is a custom split pod
                        val pod = state.pod
                        Log.d(TAG, "Pay Now clicked for pod: $podId, splitType: ${pod.splitType}")

                        // Only show custom amount dialog for CUSTOM split type
                        if (pod.splitType == SplitType.CUSTOM.value) {
                            // For custom split, show dialog to enter amount
                            Log.d(TAG, "Custom split pod - showing amount dialog")
                            customAmountText = customSplitAmount.toString()
                            showCustomAmountDialog = true
                        } else {
                            // For equal/random splits, get the amount from the pod
                            val amount = if (activePodListItem?.id == podId) {
                                activePodListItem?.yourShare ?: pod.amountPerPodder
                            } else {
                                podListItems.find { it.id == podId }?.yourShare ?: pod.amountPerPodder
                            }

                            // Initiate payment
                            initiatePayment(podId, amount)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    paymentAllowed = paymentAllowed
                )
            }

            else -> {
                if (podListItems.isEmpty()) {
                    EmptyPodsState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 24.dp)
                    )
                } else {
                    // Use the podListItems from the ViewModel
                    PodsList(
                        activePod = null,
                        podListItems = podListItems,
                        expandedPodId = expandedPodId,
                        onExpandPod = { podId ->
                            expandedPodId = if (expandedPodId == podId) null else podId
                        },
                        onPayNow = { podId ->
                            // For initial state, we need to check the pod's split type
                            // Get the pod from the list items
                            val pod = podListItems.find { it.id == podId }
                            Log.d(TAG, "Pay Now clicked in Initial state for pod: $podId")
                            selectedPodId = podId

                            if (pod != null) {
                                // Get the split type from the pod
                                val splitType = pod.splitType
                                Log.d(TAG, "Pod split type: $splitType")
                                selectedPodAmount = pod.yourShare

                                if (splitType == SplitType.CUSTOM.value) {
                                    // For custom split, show dialog to enter amount
                                    Log.d(TAG, "Custom split pod - showing amount dialog")
                                    customAmountText = customSplitAmount.toString()
                                    showCustomAmountDialog = true
                                } else {
                                    // Initiate payment for equal/random splits
                                    initiatePayment(podId, pod.yourShare)
                                }
                            } else {
                                // Pod not found, show error
                                Log.d(TAG, "Pod not found in list items")
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "Pod not found",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        paymentAllowed = paymentAllowed
                    )
                }
            }
        }
    }

    // Join Pod Dialog - Displayed when user clicks the "Join Pod" FAB
    if (showJoinPodDialog) {
        Log.d(TAG, "Showing Join Pod Dialog")
        JoinPodDialog(
            podCode = podCode,
            isError = isCodeError,
            onPodCodeChange = {
                podCode = it
                isCodeError = false
                Log.d(TAG, "Pod code changed: $it")
            },
            onDismiss = {
                Log.d(TAG, "Join Pod Dialog dismissed")
                showJoinPodDialog = false
            },
            onScanQrCode = {
                Log.d(TAG, "QR scan requested (not implemented)")
                /* Implement QR scanning */
            },
            onJoin = {
                if (podCode.isNotBlank() && podCode.length >= 4) {
                    // Valid pod code - attempt to join
                    Log.d(TAG, "Joining pod with code: $podCode")
                    paymentViewModel.joinPod(
                        podCode = podCode,
                        userId = userProfile?.userId ?: ""
                    )
                    podCode = ""
                    showJoinPodDialog = false
                } else {
                    // Invalid pod code - show error
                    Log.d(TAG, "Invalid pod code: $podCode")
                    isCodeError = true
                }
            }
        )
    }

    // Custom Amount Dialog - Displayed for custom split pods
    if (showCustomAmountDialog) {
        Log.d(
            TAG,
            "Showing Custom Amount Dialog for pod: $selectedPodId, current amount: $customAmountText"
        )
        AlertDialog(
            onDismissRequest = {
                Log.d(TAG, "Custom Amount Dialog dismissed")
                showCustomAmountDialog = false
            },
            title = { Text("Enter Your Share") },
            text = {
                Column {
                    Text(
                        "For custom split pods, each member needs to enter their share amount. " +
                                "All members must join and the total must equal the pod amount.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = {
                            customAmountText = it
                            isCustomAmountError = false
                            Log.d(TAG, "Custom amount changed: $it")
                        },
                        label = { Text("Your Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = isCustomAmountError,
                        supportingText = if (isCustomAmountError) {
                            { Text("Please enter a valid amount") }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val amount = customAmountText.toDouble()
                            if (amount <= 0) {
                                // Invalid amount (zero or negative)
                                Log.d(TAG, "Invalid custom amount: $amount")
                                isCustomAmountError = true
                            } else {
                                // Valid amount - set it and try to process payment
                                Log.d(TAG, "Setting custom amount: $amount for pod: $selectedPodId")
                                paymentViewModel.setCustomSplitAmount(selectedPodId, amount)
                                showCustomAmountDialog = false
                                selectedPodAmount = amount

                                // If payment is allowed after setting the amount, initiate payment
                                if (paymentAllowed) {
                                    Log.d(TAG, "Payment allowed - initiating payment")
                                    initiatePayment(selectedPodId, amount)
                                } else {
                                    // Show a message that payment is not allowed yet
                                    Log.d(TAG, "Payment not allowed - showing message")
                                    scope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = "Cannot process payment yet. All members must join and total amounts must match.",
                                            duration = SnackbarDuration.Long
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Exception when parsing amount (not a valid number)
                            Log.e(TAG, "Error parsing custom amount: ${e.message}")
                            isCustomAmountError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDeep
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Log.d(TAG, "Custom Amount Dialog canceled")
                    showCustomAmountDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Payment Processing Dialog
    if (showPaymentProcessingDialog) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss while processing */ },
            title = { Text("Processing Payment") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = PurpleDeep,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Please wait while we process your payment...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = { /* No confirm button during processing */ },
            dismissButton = { /* No dismiss button during processing */ }
        )
    }
}
