package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.components.BottomNavigationBar
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.HomeScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.NotificationScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.TransactionHistoryScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile.ProfileScreen
import com.tees.mad.e4089074.sharepoint.util.BottomTabItem
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel
import com.tees.mad.e4089074.sharepoint.viewmodels.PaymentPodViewModel
import com.tees.mad.e4089074.sharepoint.viewmodels.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    paymentViewModel: PaymentPodViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    LaunchedEffect(userProfile) {
        profileViewModel.getUserProfile()
    }
    LaunchedEffect(profileViewModel) {
        paymentViewModel.fetchPreviousPods(userProfile?.userId ?: "")
    }


    Scaffold(bottomBar = {
        BottomNavigationBar(navController, bottomTabItems)
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // This NavHost handles navigation within Dashboard
            NavHost(
                navController = navController,
                startDestination = AppRoute.Dashboard.HomeTab.route
            ) {
                // Home Tab Graph
                navigation(
                    route = AppRoute.Dashboard.HomeTab.route,
                    startDestination = AppRoute.Dashboard.HomeTab.Home.route
                ) {
                    composable(AppRoute.Dashboard.HomeTab.Home.route) {
                        HomeScreen(modifier, navController, profileViewModel)
                    }
                    composable(AppRoute.Dashboard.HomeTab.Notifications.route) {
                        NotificationScreen(modifier, navController)
                    }
                    composable(AppRoute.Dashboard.HomeTab.TransactionHistory.route) {
                        TransactionHistoryScreen(modifier, navController)
                    }
                }

                // Payments Tab Graph
                navigation(
                    route = AppRoute.Dashboard.PaymentsTab.route,
                    startDestination = AppRoute.Dashboard.PaymentsTab.AddPayment.route
                ) {
                    composable(AppRoute.Dashboard.PaymentsTab.AddPayment.route) {
                        PaymentScreen(modifier, navController, profileViewModel)
                    }

                }

                // Profile Tab Graph
                navigation(
                    route = AppRoute.Dashboard.ProfileTab.route,
                    startDestination = AppRoute.Dashboard.ProfileTab.ProfileInfo.route
                ) {
                    composable(AppRoute.Dashboard.ProfileTab.ProfileInfo.route) {
                        ProfileScreen(modifier, navController, authViewModel, profileViewModel)
                    }

                }
            }
        }
    }
}


val bottomTabItems = listOf(
    BottomTabItem(
        title = "Home",
        route = AppRoute.Dashboard.HomeTab.route,
        icon = Icons.Default.Home
    ),
    BottomTabItem(
        title = "Payments",
        route = AppRoute.Dashboard.PaymentsTab.route,
        icon = Icons.Default.Payments
    ),
    BottomTabItem(
        title = "Profile",
        route = AppRoute.Dashboard.ProfileTab.route,
        icon = Icons.Default.Person
    )
)
