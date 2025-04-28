package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tees.mad.e4089074.sharepoint.R
import com.tees.mad.e4089074.sharepoint.ui.components.profile.AccountStatItem
import com.tees.mad.e4089074.sharepoint.ui.components.profile.FlatProfileSectionCard
import com.tees.mad.e4089074.sharepoint.ui.components.profile.ProfileSectionItem
import com.tees.mad.e4089074.sharepoint.ui.components.profile.VerticalDivider
import com.tees.mad.e4089074.sharepoint.ui.theme.Black
import com.tees.mad.e4089074.sharepoint.ui.theme.ErrorRed
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.showToast
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel
import com.tees.mad.e4089074.sharepoint.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Profile Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image with subtle border
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Purple0)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_avatar),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .border(1.dp, Purple20, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = listOfNotNull(
                    userProfile?.firstName,
                    userProfile?.lastName
                ).joinToString(" "),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
            )

            // Username
            Text(
                text = "@${userProfile?.email?.split("@")?.first() ?: ""}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Account Stats Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = Purple0
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccountStatItem(
                    count = "0",
                    label = "Pods",
                    icon = Icons.Outlined.Group
                )

                VerticalDivider(
                    color = Purple20,
                    modifier = Modifier.height(40.dp),
                    thickness = 1.dp
                )

                AccountStatItem(
                    count = "£0",
                    label = "Spent",
                    icon = Icons.Outlined.ShoppingBag
                )

                VerticalDivider(
                    color = Purple20,
                    modifier = Modifier.height(40.dp),
                    thickness = 1.dp
                )

                AccountStatItem(
                    count = "£0",
                    label = "Saved",
                    icon = Icons.Outlined.Savings
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Help & Support Section
        Text(
            text = "Help & Support",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        FlatProfileSectionCard {
            ProfileSectionItem(
                icon = Icons.AutoMirrored.Outlined.Help,
                title = "Help",
                onClick = {
                    showToast(context, "Help")
                }
            )

            ProfileSectionItem(
                icon = Icons.Outlined.Inbox,
                title = "Inbox",
                onClick = {
                    showToast(context, "Inbox")
                }
            )

            ProfileSectionItem(
                icon = Icons.Outlined.Notifications,
                title = "Notification Settings",
                onClick = { showToast(context, "Notification Settings") },
                showDivider = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account & Security Section
        Text(
            text = "Account & Security",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        FlatProfileSectionCard {
            ProfileSectionItem(
                icon = Icons.Outlined.Person,
                title = "Edit Profile",
                onClick = { showToast(context, "Edit Profile") }
            )

            ProfileSectionItem(
                icon = Icons.Outlined.Security,
                title = "Security",
                onClick = { showToast(context, "Security") }
            )

            ProfileSectionItem(
                icon = Icons.Outlined.CreditCard,
                title = "Payment Methods",
                onClick = { showToast(context, "Payment Methods") }
            )

            ProfileSectionItem(
                icon = Icons.AutoMirrored.Outlined.Logout,
                title = "Log Out",
                iconTint = ErrorRed,
                titleColor = ErrorRed,
                onClick = { authViewModel.logout() },
                showDivider = false
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // App Version
        Text(
            text = "App Version 1.0.0",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}



