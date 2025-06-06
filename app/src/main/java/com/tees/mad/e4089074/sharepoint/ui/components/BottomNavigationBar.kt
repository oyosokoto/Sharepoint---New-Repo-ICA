package com.tees.mad.e4089074.sharepoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.BottomTabItem

@Composable
fun BottomNavigationBar(
    navController: NavHostController = rememberNavController(),
    items: List<BottomTabItem>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 10.dp,
        color = White
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            containerColor = White,
            tonalElevation = 0.dp
        ) {
            val currentBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = currentBackStackEntry?.destination?.route

            val currentTab = when {
                currentRoute?.startsWith("dashboard/home") == true ->
                    AppRoute.Dashboard.HomeTab.route

                currentRoute?.startsWith("dashboard/payments") == true ->
                    AppRoute.Dashboard.PaymentsTab.route

                currentRoute?.startsWith("dashboard/profile") == true ->
                    AppRoute.Dashboard.ProfileTab.route

                else -> null
            }

            items.forEach { item ->
                val isSelected = currentTab == item.route

                NavigationBarItem(
                    icon = {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PurpleDeep.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = PurpleDeep
                                )
                            }
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = Gray
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = if (isSelected) PurpleDeep else Gray
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentTab != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleDeep,
                        unselectedIconColor = Gray,
                        selectedTextColor = PurpleDeep,
                        unselectedTextColor = Gray,
                        indicatorColor = White
                    )
                )
            }
        }
    }
}



