package com.tees.mad.e4089074.sharepoint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.ForgotPasswordScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.LoginScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.SignupScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.auth.WelcomeScreen
import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.DashboardScreen
import com.tees.mad.e4089074.sharepoint.util.AuthState
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel


@Composable
fun SharePoint(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState = authViewModel.authState.collectAsState()
    
    // Add a loading state to prevent flicker
    var isInitializing by remember { mutableStateOf(true) }
    
    LaunchedEffect(authState.value) {
        if (isInitializing) {
            isInitializing = false
        }
    }
    
    if (isInitializing) {
        // Show loading indicator while determining auth state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.White),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color(0xFF6200EE))
        }
        return
    }
    
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
            composable(AppRoute.Auth.ForgotPassword.route) {
                ForgotPasswordScreen(modifier, navController, authViewModel)
            }
        }

        composable(AppRoute.Dashboard.route) {
            // Pass in a remembered NavController just for the dashboard
            val dashboardNavController = rememberNavController()
            DashboardScreen(modifier, dashboardNavController)
        }
    }
}
