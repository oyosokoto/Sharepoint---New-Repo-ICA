package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.R
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.BalanceCard
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.EmptyTransaction
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.JoinPod
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.TransactionListItem
import com.tees.mad.e4089074.sharepoint.ui.theme.Black
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.viewmodels.ProfileViewModel
import com.tees.mad.e4089074.sharepoint.viewmodels.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    profileViewModel: ProfileViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val allTransactions by transactionViewModel.transactions.collectAsStateWithLifecycle()
    val transactions = remember(allTransactions) {
        allTransactions.take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 20.dp)
            .padding(top = 36.dp)
    ) {
        // Top Bar with Profile and Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_avatar),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, Purple20, CircleShape)
                        .clickable {
                            navController.navigate(AppRoute.Dashboard.ProfileTab.ProfileInfo.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Hi, ${userProfile?.firstName ?: ""}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black
                    )
                )
            }

            // Notification icon
            IconButton(
                onClick = {
                    navController.navigate(AppRoute.Dashboard.HomeTab.Notifications.route)
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Purple0)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = PurpleDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Balance Card - Flattened design
        BalanceCard(amountSaved = 0f, totalAmountSpent = 0f)

        Spacer(modifier = Modifier.height(24.dp))


        JoinPod(onClick = {
            navController.navigate(AppRoute.Dashboard.PaymentsTab.AddPayment.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        })
        // Join Pod Card - Flattened design

        Spacer(modifier = Modifier.height(28.dp))

        // Latest Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Latest Transactions",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
            )

            TextButton(
                onClick = {
                    navController.navigate(AppRoute.Dashboard.HomeTab.TransactionHistory.route)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "See All",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PurpleDeep
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Empty state for transactions
        if (transactions.isEmpty()) {
            EmptyTransaction()
        } else {
            Text(
                text = "Every Red is an amount saved!",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            // Transaction items
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(transactions.size) { index ->
                    TransactionListItem(transaction = transactions[index])
                }
            }
        }
    }
}
