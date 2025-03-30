package com.tees.mad.e4089074.sharepoint.ui.components.pods

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import kotlinx.coroutines.delay

/**
 * JoinPodDialog displays a dialog for users to enter a pod code to join an existing pod.
 * 
 * @param podCode The current pod code entered by the user
 * @param isError Whether there's an error with the current pod code
 * @param onPodCodeChange Callback when the pod code is changed
 * @param onDismiss Callback when the dialog is dismissed
 * @param onScanQrCode Callback when the QR code scan button is clicked
 * @param onJoin Callback when the Join button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinPodDialog(
    podCode: String,
    isError: Boolean,
    onPodCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onScanQrCode: () -> Unit,
    onJoin: () -> Unit
) {
    val TAG = "JoinPodDialog"
    
    Log.d(TAG, "Showing JoinPodDialog, current code: $podCode, isError: $isError")
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Join a Pod",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pod code input field with QR code scanner option
                OutlinedTextField(
                    value = podCode,
                    onValueChange = { 
                        Log.d(TAG, "Pod code changed: $it")
                        onPodCodeChange(it) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = { Text("Pod Code") },
                    placeholder = { Text("Enter 4+ digit code") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a valid pod code") }
                    } else null,
                    trailingIcon = {
                        IconButton(onClick = { 
                            Log.d(TAG, "QR code scan button clicked")
                            onScanQrCode() 
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                                tint = PurpleDeep
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            Log.d(TAG, "Keyboard Done action - joining pod")
                            onJoin() 
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Cancel button
                    TextButton(
                        onClick = { 
                            Log.d(TAG, "Cancel button clicked")
                            onDismiss() 
                        }
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Join button - only enabled if pod code is valid
                    Button(
                        onClick = { 
                            Log.d(TAG, "Join button clicked with code: $podCode")
                            onJoin() 
                        },
                        enabled = podCode.isNotBlank() && podCode.length >= 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleDeep
                        )
                    ) {
                        Text("Join")
                    }
                }
            }
        }
    }
}
