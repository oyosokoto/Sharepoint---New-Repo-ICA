package com.tees.mad.e4089074.sharepoint.ui.components.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep

@Composable
fun JoinPodDialog(
    podCode: String,
    isError: Boolean,
    onPodCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onScanQrCode: () -> Unit,
    onJoin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join a Payment Pod") },
        text = {
            Column {
                Text(
                    "Enter the pod code provided by the pod creator or scan the QR code.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = podCode,
                    onValueChange = onPodCodeChange,
                    label = { Text("Pod Code") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a valid pod code") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onScanQrCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Scan QR Code")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDeep
                )
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomAmountDialog(
    customAmountText: String,
    isCustomAmountError: Boolean,
    onCustomAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    onValueChange = onCustomAmountChange,
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
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDeep
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PaymentProcessingDialog() {
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