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
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile.ProfileScreen
import com.tees.mad.e4089074.sharepoint.util.navigation.homeStack
import com.tees.mad.e4089074.sharepoint.util.navigation.paymentStack
import com.tees.mad.e4089074.sharepoint.util.navigation.profileStack


@Composable
fun SharePoint(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = if (false) AppRoute.Auth.route else AppRoute.Dashboard.route,
    ) {
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

        navigation(
            startDestination = AppRoute.Dashboard.Home.route,
            route = AppRoute.Dashboard.route
        ) {
            composable(AppRoute.Dashboard.Home.route) {
                DashboardScreen(modifier)
            }

            composable(AppRoute.Dashboard.Profile.route) {
                ProfileScreen(modifier, navController)
            }

            composable(AppRoute.Dashboard.Payments.route) {
                PaymentScreen(modifier, navController)
            }

        }

    }
}