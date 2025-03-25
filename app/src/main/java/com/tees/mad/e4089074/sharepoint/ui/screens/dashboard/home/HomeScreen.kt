package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.tees.mad.e4089074.sharepoint.ui.components.TransactionList
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.BalanceCard
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple40
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.AppTransactionData
import com.tees.mad.e4089074.sharepoint.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    profileViewModel: ProfileViewModel = viewModel(),
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()


    // Sample data for transactions
    val transactions = remember {
        listOf<AppTransactionData>(
//            AppTransactionData(
//                businessName = "Crystals Coffe Shop",
//                amount = 14.99f,
//                podderCount = 4,
//                date = "Mar 15, 2025",
//                logo = R.drawable.placeholder_logo
//            ),
//            AppTransactionData(
//                businessName = "D & D Restaurant",
//                amount = 16.99f,
//                podderCount = 6,
//                date = "Mar 10, 2025",
//                logo = R.drawable.placeholder_logo
//            ),
//            AppTransactionData(
//                businessName = "Hot Wings",
//                amount = 1800.00f,
//                podderCount = 3,
//                date = "Mar 01, 2025",
//                logo = R.drawable.placeholder_logo
//            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp)
        ) {
            // Top greeting section with avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_avatar),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(2.dp, White, CircleShape)
                        .clickable {
                            navController.navigate(AppRoute.Dashboard.ProfileTab.ProfileInfo.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Hi, ${userProfile?.firstName ?: ""}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Notification and account buttons
                Row {
                    IconButton(
                        onClick = {
                            navController
                                .navigate(
                                    AppRoute
                                        .Dashboard.HomeTab.Notifications.route
                                )
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = PurpleDeep
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Balance Card -
            BalanceCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Join Pod Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clickable {
                        navController.navigate(AppRoute.Dashboard.PaymentsTab.AddPayment.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = PurpleDeep
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Join a Pod",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = White
                        )
                        Text(
                            text = "Split payments with friends",
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Join Pod",
                        tint = White,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Latest Transactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest Transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                TextButton(onClick = {
                    navController.navigate(AppRoute.Dashboard.HomeTab.TransactionHistory.route)
                }) {
                    Text(
                        text = "See All",
                        fontSize = 14.sp,
                        color = Purple40,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            transactions.isNotEmpty().takeIf { it }?.let {
                Text(
                    text = "Every Red is an amount saved!",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction items
            TransactionList(transactions.subList(0, 0))


        }
    }
}

