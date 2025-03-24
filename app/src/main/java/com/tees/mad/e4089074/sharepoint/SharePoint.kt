package com.tees.mad.e4089074.sharepoint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthState
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel


@Composable
fun SharePoint(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState = authViewModel.authState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = if (authState.value is AuthState.Authenticated) AppRoute.Dashboard.route else AppRoute.Auth.route
    ) {
        // Auth Flow
        navigation(
            startDestination = AppRoute.Auth.Welcome.route,
            route = AppRoute.Auth.route
        ) {
            composable(AppRoute.Auth.Welcome.route) {
                WelcomeScreen(modifier, navController, authViewModel)
            }
            composable(AppRoute.Auth.Login.route) {
                LoginScreen(modifier, navController, authViewModel)
            }
            composable(AppRoute.Auth.Register.route) {
                SignupScreen(modifier, navController, authViewModel)
            }
        }

        composable(AppRoute.Dashboard.route) {
            // Pass in a remembered NavController just for the dashboard
            val dashboardNavController = rememberNavController()
            DashboardScreen(modifier, dashboardNavController)
        }
    }
}