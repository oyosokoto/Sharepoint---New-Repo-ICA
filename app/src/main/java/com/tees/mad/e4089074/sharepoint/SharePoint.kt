package com.tees.mad.e4089074.sharepoint

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.LoginScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.SignupScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.WelcomeScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.DashboardScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.HomeScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.NotificationScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.TransactionHistoryScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile.ProfileScreen


@Composable
fun SharePoint(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = if (false) AppRoute.Auth.route else AppRoute.Dashboard.route,
    ) {
        // Auth Flow
        navigation(
            startDestination = AppRoute.Auth.Welcome.route,
            route = AppRoute.Auth.route
        ) {
            composable(AppRoute.Auth.Welcome.route) {
                WelcomeScreen(modifier, navController)
            }
            composable(AppRoute.Auth.Login.route) {
                LoginScreen(modifier, navController)
            }
            composable(AppRoute.Auth.Register.route) {
                SignupScreen(modifier, navController)
            }
        }

        composable(AppRoute.Dashboard.route) {
            // Pass in a remembered NavController just for the dashboard
            val dashboardNavController = rememberNavController()
            DashboardScreen(modifier, dashboardNavController)
        }
    }
}