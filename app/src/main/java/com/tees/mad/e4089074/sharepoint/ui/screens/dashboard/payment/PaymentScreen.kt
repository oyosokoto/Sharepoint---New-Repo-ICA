package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.components.payment.CustomAmountDialog
import com.tees.mad.e4089074.sharepoint.ui.components.payment.CustomSnackbarHost
import com.tees.mad.e4089074.sharepoint.ui.components.payment.JoinPodFAB
import com.tees.mad.e4089074.sharepoint.ui.components.payment.PaymentProcessingDialog
import com.tees.mad.e4089074.sharepoint.ui.components.payment.PaymentScreenContent
import com.tees.mad.e4089074.sharepoint.ui.components.payment.PaymentScreenTopBar
import com.tees.mad.e4089074.sharepoint.ui.components.pods.JoinPodDialog
import com.tees.mad.e4089074.sharepoint.util.PaymentManager
import com.tees.mad.e4089074.sharepoint.util.PaymentProcessingEffect
import com.tees.mad.e4089074.sharepoint.util.StripePaymentResultEffect
import com.tees.mad.e4089074.sharepoint.util.handlePayNowClick
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

    // View model states
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val podState by paymentViewModel.podState.collectAsState()
    val activePodListItem by paymentViewModel.activePodListItem.collectAsStateWithLifecycle()
    val podListItems by paymentViewModel.podListItems.collectAsStateWithLifecycle()
    val paymentAllowed by paymentViewModel.paymentAllowed.collectAsStateWithLifecycle()
    val userToken by profileViewModel.userToken.collectAsStateWithLifecycle()
    val customSplitAmount by paymentViewModel.customSplitAmount.collectAsStateWithLifecycle()
    val paymentProcessingState by paymentViewModel.paymentProcessingState.collectAsStateWithLifecycle()

    // UI state
    var showJoinPodDialog by remember { mutableStateOf(false) }
    var showCustomAmountDialog by remember { mutableStateOf(false) }
    var showPaymentProcessingDialog by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("") }
    var podCode by remember { mutableStateOf("") }
    var isCodeError by remember { mutableStateOf(false) }
    var isCustomAmountError by remember { mutableStateOf(false) }
    var selectedPodId by remember { mutableStateOf("") }
    var selectedPodAmount by remember { mutableDoubleStateOf(0.0) }
    var expandedPodId by remember { mutableStateOf<String?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Payment handling
    val paymentManager = remember {
        PaymentManager(
            paymentViewModel = paymentViewModel,
            snackBarHostState = snackBarHostState,
            context = context,
            coroutineScope = scope
        )
    }

    // Fetch user's pods when the screen is first displayed
    LaunchedEffect(userProfile?.userId) {
        Log.d(TAG, "Fetching pods for user: ${userProfile?.userId}")
        // Set loading state before fetching pods
        paymentViewModel.setLoadingState()
        paymentViewModel.fetchPreviousPods(userProfile?.userId ?: "")
    }
    
    // Set up pod updates listener when an active pod is available
    LaunchedEffect(podState) {
        if (podState is PaymentPodViewModel.PodState.Success) {
            val pod = podState as PaymentPodViewModel.PodState.Success
            Log.d(TAG, "Setting up pod updates listener for pod: ${pod.pod.id}")
            pod.pod.id?.let { podId ->
                paymentViewModel.observePodUpdates(podId)
            }
        }
    }

    // Handle payment processing state changes
    PaymentProcessingEffect(
        paymentProcessingState = paymentProcessingState,
        onShowDialog = { showPaymentProcessingDialog = it },
        selectedPodId = selectedPodId,
        context = context,
        snackBarHostState = snackBarHostState,
        coroutineScope = scope
    )

    // Handle Stripe payment result
    StripePaymentResultEffect(
        selectedPodId = selectedPodId,
        paymentViewModel = paymentViewModel,
        snackBarHostState = snackBarHostState,
        scope = scope
    )

    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            paymentViewModel.resetPaymentProcessingState()
            StripePaymentLauncher.resetPaymentResult()
            paymentViewModel.cleanupPodListener()
            Log.d(TAG, "Cleaned up pod listener")
        }
    }

    Scaffold(
        topBar = {
            PaymentScreenTopBar()
        },
        floatingActionButton = {
            JoinPodFAB(onClick = { showJoinPodDialog = true })
        },
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        PaymentScreenContent(
            podState = podState,
            podListItems = podListItems,
            activePodListItem = activePodListItem,
            expandedPodId = expandedPodId,
            onExpandPod = { podId ->
                expandedPodId = if (expandedPodId == podId) null else podId
            },
            onPayNow = { podId ->
                handlePayNowClick(
                    podId = podId,
                    podState = podState,
                    podListItems = podListItems,
                    activePodListItem = activePodListItem,
                    snackBarHostState = snackBarHostState,
                    scope = scope,
                    onSelectPod = { id, amount ->
                        selectedPodId = id
                        selectedPodAmount = amount
                    },
                    onShowCustomAmountDialog = {
                        customAmountText = customSplitAmount.toString()
                        showCustomAmountDialog = true
                    },
                    onInitiatePayment = { id, amount ->
                        paymentManager.initiatePayment(
                            podId = id,
                            amount = amount,
                            userToken = userToken
                        )
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            paymentAllowed = paymentAllowed
        )
    }

    // Dialogs
    if (showJoinPodDialog) {
        JoinPodDialog(
            podCode = podCode,
            isError = isCodeError,
            onPodCodeChange = {
                podCode = it
                isCodeError = false
            },
            onDismiss = {
                showJoinPodDialog = false
            },
            onScanQrCode = {
                /* Implement QR scanning */
            },
            onJoin = {
                if (podCode.isNotBlank() && podCode.length >= 4) {
                    paymentViewModel.joinPod(
                        podCode = podCode,
                        userId = userProfile?.userId ?: ""
                    )
                    podCode = ""
                    showJoinPodDialog = false
                } else {
                    isCodeError = true
                }
            }
        )
    }

    if (showCustomAmountDialog) {
        CustomAmountDialog(
            customAmountText = customAmountText,
            isCustomAmountError = isCustomAmountError,
            onCustomAmountChange = {
                customAmountText = it
                isCustomAmountError = false
            },
            onDismiss = {
                showCustomAmountDialog = false
            },
            onConfirm = {
                try {
                    val amount = customAmountText.toDouble()
                    if (amount <= 0) {
                        isCustomAmountError = true
                    } else {
                        paymentViewModel.setCustomSplitAmount(selectedPodId, amount)
                        showCustomAmountDialog = false
                        selectedPodAmount = amount

                        if (paymentAllowed) {
                            paymentManager.initiatePayment(
                                podId = selectedPodId,
                                amount = amount,
                                userToken = userToken
                            )
                        } else {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "Cannot process payment yet. All members must join and total amounts must match.",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    isCustomAmountError = true
                }
            }
        )
    }

    if (showPaymentProcessingDialog) {
        PaymentProcessingDialog()
    }
}
