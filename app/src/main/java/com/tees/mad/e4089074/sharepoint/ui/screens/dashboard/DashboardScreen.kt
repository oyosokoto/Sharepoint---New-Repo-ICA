package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.components.BottomNavigationBar
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.HomeScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile.ProfileScreen
import com.tees.mad.e4089074.sharepoint.util.BottomTabItem

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    Scaffold(bottomBar = {
        BottomNavigationBar(navController, bottomTabItems)
    }) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {
            // This NavHost will handle the navigation between the bottom tabs
            // and their nested screens
            NavHost(
                navController = navController,
                startDestination = AppRoute.DashboardTab.HomeTab.route
            ) {
                // Home Tab Graph
                navigation(
                    route = AppRoute.DashboardTab.HomeTab.route,
                    startDestination = AppRoute.HomeNestedScreens.Home.createRoute(AppRoute.DashboardTab.HomeTab.route)
                ) {
                    composable(AppRoute.HomeNestedScreens.Home.createRoute(AppRoute.DashboardTab.HomeTab.route)) {
                        HomeScreen(
                            Modifier.padding(innerPadding),
                            navController
                        )
                    }
//                    composable(HomeNestedScreens.Notifications.createRoute(AppRoute.DashboardTab.HomeTab.route)) {
//                        NotificationsScreen(
//                            onBackPressed = { navController.navigateUp() }
//                        )
//                    }
//                    composable(HomeNestedScreens.TransactionHistory.createRoute(AppRoute.DashboardTab.HomeTab.route)) {
//                        TransactionHistoryScreen(
//                            onBackPressed = { navController.navigateUp() }
//                        )
//                    }
                }

                // Payments Tab Graph
                navigation(
                    route = AppRoute.DashboardTab.PaymentsTab.route,
                    startDestination = AppRoute.PaymentsNestedScreens.AddPayment.createRoute(
                        AppRoute.DashboardTab.PaymentsTab.route
                    )
                ) {
                    composable(AppRoute.PaymentsNestedScreens.AddPayment.createRoute(AppRoute.DashboardTab.PaymentsTab.route)) {
                        PaymentScreen(
                            Modifier.padding(innerPadding),
                            navController
                        )
                    }
//                    composable(PaymentsNestedScreens.PaymentDetails.createRoute(AppRoute.DashboardTab.PaymentsTab.route) + "/{paymentId}") { backStackEntry ->
//                        val paymentId = backStackEntry.arguments?.getString("paymentId")
//                        PaymentDetailsScreen(
//                            paymentId = paymentId,
//                            onBackPressed = { navController.navigateUp() }
//                        )
//                    }
//                    composable(PaymentsNestedScreens.AddPayment.createRoute(AppRoute.DashboardTab.PaymentsTab.route)) {
//                        AddPaymentScreen(
//                            onBackPressed = { navController.navigateUp() },
//                            onPaymentAdded = { navController.navigateUp() }
//                        )
//                    }
                }

                // Profile Tab Graph
                navigation(
                    route = AppRoute.DashboardTab.ProfileTab.route,
                    startDestination = AppRoute.ProfileNestedScreens.ProfileInfo.createRoute(
                        AppRoute.DashboardTab.ProfileTab.route
                    )
                ) {
                    composable(AppRoute.ProfileNestedScreens.ProfileInfo.createRoute(AppRoute.DashboardTab.ProfileTab.route)) {
                        ProfileScreen(
                            Modifier.padding(innerPadding),
                            navController
                        )
                    }
                }
            }
        }
    }

}

val bottomTabItems = listOf(
    BottomTabItem(
        title = "Home",
        route = AppRoute.DashboardTab.HomeTab.route,
        icon = Icons.Default.Home
    ),
    BottomTabItem(
        title = "Payments",
        route = AppRoute.DashboardTab.PaymentsTab.route,
        icon = Icons.Default.Payments
    ),
    BottomTabItem(
        title = "Profile",
        route = AppRoute.DashboardTab.ProfileTab.route,
        icon = Icons.Default.Person
    )
)
