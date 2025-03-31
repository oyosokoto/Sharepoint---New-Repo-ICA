package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home;

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.components.AuthScreenComponent

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    AuthScreenComponent(
        modifier = modifier,
        onBackArrowClick = { navController.popBackStack() },
        title = "Notifications",
        subtitle = "View your notifications"
    ) {
        Text(text = "Nothing to see here.")
    }
}