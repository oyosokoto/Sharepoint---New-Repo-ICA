package com.tees.mad.e4089074.sharepoint.routes

sealed class AppRoute(val route: String) {
    // Auth Stack
    sealed class Auth(route: String) : AppRoute(route) {
        object Welcome : Auth("welcome")
        object Login : Auth("login")
        object Register : Auth("register")
        companion object {
            const val route = "auth-stack"
        }
    }

    // Dashboard Stack
    sealed class Dashboard(route: String) : AppRoute(route) {
        object Home : Dashboard("dashboard/home")
        object Profile : Dashboard("dashboard/profile")
        object Payments : Dashboard("dashboard/payments")

        companion object {
            const val route = "dashboard-stack"
        }
    }

    // Bottom Tabs within Dashboard
    sealed class DashboardTab(route: String) : AppRoute(route) {
        object HomeTab : DashboardTab("dashboard-tab/home")
        object PaymentsTab : DashboardTab("dashboard-tab/payments")
        object ProfileTab : DashboardTab("dashboard-tab/profile")

        companion object {
            const val route = "dashboard-tabs"
        }
    }

    sealed class HomeNestedScreens(val route: String) {
        object Home : HomeNestedScreens("home")
        object Notifications : HomeNestedScreens("notifications")
        object TransactionHistory : HomeNestedScreens("transaction_history")

        fun createRoute(root: String) = "$root/$route"
    }

    sealed class PaymentsNestedScreens(val route: String) {
        object PaymentDetails : PaymentsNestedScreens("payment_details")
        object AddPayment : PaymentsNestedScreens("add_payment")

        fun createRoute(root: String) = "$root/$route"
    }

    sealed class ProfileNestedScreens(val route: String) {
        object ProfileInfo : ProfileNestedScreens("profile_info")
        object ChangePassword : ProfileNestedScreens("change_password")
        object EditProfile : ProfileNestedScreens("edit_profile")

        fun createRoute(root: String) = "$root/$route"
    }

}