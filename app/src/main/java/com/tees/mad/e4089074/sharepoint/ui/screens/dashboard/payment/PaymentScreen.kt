package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.components.EmptyPodsState
import com.tees.mad.e4089074.sharepoint.ui.components.JoinPodDialog
import com.tees.mad.e4089074.sharepoint.ui.components.PodCard
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.PodDetails

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val pods = remember { mutableStateListOf<PodDetails>() }
    var showJoinPodDialog by remember { mutableStateOf(false) }
    var podCode by remember { mutableStateOf("") }
    var isCodeError by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showJoinPodDialog = true },
                containerColor = PurpleDeep
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Join Pod",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Payment Pods",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = PurpleDeep
                )

                if (pods.isEmpty()) {
                    EmptyPodsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(pods) { pod ->
                            PodCard(pod = pod)
                        }
                    }
                }
            }
        }

        if (showJoinPodDialog) {
            JoinPodDialog(
                podCode = podCode,
                isError = isCodeError,
                onPodCodeChange = {
                    podCode = it
                    isCodeError = false
                },
                onDismiss = { showJoinPodDialog = false },
                onScanQrCode = { /* Implement QR scanning */ },
                onJoin = {
                    if (podCode.isNotBlank() && podCode.length >= 4) {
                        // In a real app, you would validate with an API
                        val newPod = PodDetails(
                            code = podCode,
                            name = "Pod ${podCode.takeLast(4)}",
                            participants = (5..20).random().toString()
                        )
                        pods.add(newPod)
                        podCode = ""
                        showJoinPodDialog = false
                    } else {
                        isCodeError = true
                    }
                }
            )
        }
    }
}
