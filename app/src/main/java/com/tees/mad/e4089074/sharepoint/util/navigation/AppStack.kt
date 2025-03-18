//package com.tees.mad.e4089074.sharepoint.util.navigation;
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.composable
//import androidx.navigation.navigation
//import com.tees.mad.e4089074.sharepoint.routes.AppRoute
//import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home.HomeScreen
//import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.payment.PaymentScreen
//import com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile.ProfileScreen
//
//fun NavGraphBuilder.homeStack(modifier: Modifier = Modifier, navController: NavHostController) {
//    navigation(
//        startDestination = AppRoute.Dashboard.Home.route,
//        route = AppRoute.DashboardTab.route
//    ) {
//        composable(AppRoute.Dashboard.Home.route) {
//            HomeScreen(modifier, navController)
//        }
//    }
//}
//
//fun NavGraphBuilder.paymentStack(modifier: Modifier = Modifier, navController: NavHostController) {
//    navigation(
//        startDestination = AppRoute.Dashboard.Payments.route,
//        route = AppRoute.DashboardTab.route
//    ) {
//        composable(AppRoute.Dashboard.Payments.route) {
//            PaymentScreen(modifier, navController)
//        }
//    }
//}
//
//
//fun NavGraphBuilder.profileStack(modifier: Modifier = Modifier, navController: NavHostController) {
//    navigation(
//        startDestination = AppRoute.Dashboard.Profile.route,
//        route = AppRoute.DashboardTab.route
//    ) {
//        composable(AppRoute.Dashboard.Profile.route) {
//            ProfileScreen(modifier, navController)
//        }
//    }
//}
