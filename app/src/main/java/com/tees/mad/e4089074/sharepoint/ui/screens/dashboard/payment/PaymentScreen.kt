package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple40
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple80
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var podCode by remember { mutableStateOf("") }
    var showPodDetails by remember { mutableStateOf(false) }
    var showWrongCode by remember { mutableStateOf(false) }
    val podDetails = remember {
        mutableStateOf(PodDetails("12345", "The Best Pod", "10"))
    }

    Scaffold {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showBottomSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join Pod", color = Color.White)
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth(),
                containerColor = Purple80
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = podCode,
                        onValueChange = {
                            podCode = it
                            showWrongCode = false
                        },
                        label = { Text("Enter Pod Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (showWrongCode) {
                        Text(
                            text = "Wrong Pod Code",
                            color = Color.Red
                        )
                    }
                    Button(
                        onClick = {
                            if (podCode == podDetails.value.code) {
                                showPodDetails = true
                                scope.launch { bottomSheetState.hide() }
                                showBottomSheet = false
                            } else {
                                showWrongCode = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Text(text = "Validate Code", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Text(text = "Scan QR Code", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        if (showPodDetails) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Purple40, RoundedCornerShape(8.dp)),
            ) {
                Text(
                    text = "Pod Code: ${podDetails.value.code}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Pod Name: ${podDetails.value.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Total Participants: ${podDetails.value.participants}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

data class PodDetails(val code: String, val name: String, val participants: String)

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen()
}