package com.tees.mad.e4089074.sharepoint.routes

sealed class AppRoute(open val route: String) {
    // Auth Stack
    sealed class Auth(route: String) : AppRoute(route) {
        object Welcome : Auth("welcome")
        object Login : Auth("login")
        object Register : Auth("register")
        object ForgotPassword : Auth("forgot_password")

        companion object {
            const val route = "auth"
        }
    }

    // Dashboard Stack with nested screens
    sealed class Dashboard : AppRoute("dashboard") {
        // Main tab screens
        object HomeTab : Dashboard() {
            override val route = "dashboard/home"

            // Nested screens within HomeTab
            object Home : Dashboard() {
                override val route = "dashboard/home/main"
            }
            object Notifications : Dashboard() {
                override val route = "dashboard/home/notifications"
            }
            object TransactionHistory : Dashboard() {
                override val route = "dashboard/home/transaction_history"
            }
        }

        object PaymentsTab : Dashboard() {
            override val route = "dashboard/payments"

            // Nested screens within PaymentsTab
            object PaymentDetails : Dashboard() {
                override val route = "dashboard/payments/details"
            }
            object AddPayment : Dashboard() {
                override val route = "dashboard/payments/add"
            }
        }

        object ProfileTab : Dashboard() {
            override val route = "dashboard/profile"

            // Nested screens within ProfileTab
            object ProfileInfo : Dashboard() {
                override val route = "dashboard/profile/info"
            }
            object ChangePassword : Dashboard() {
                override val route = "dashboard/profile/change_password"
            }
            object EditProfile : Dashboard() {
                override val route = "dashboard/profile/edit"
            }
        }

        companion object {
            const val route = "dashboard"
        }
    }
}