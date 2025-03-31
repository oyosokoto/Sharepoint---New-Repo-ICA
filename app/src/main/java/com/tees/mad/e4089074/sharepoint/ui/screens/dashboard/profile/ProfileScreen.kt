package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tees.mad.e4089074.sharepoint.R
import com.tees.mad.e4089074.sharepoint.ui.components.profile.ProfileSectionCard
import com.tees.mad.e4089074.sharepoint.ui.components.profile.ProfileSectionItem
import com.tees.mad.e4089074.sharepoint.ui.theme.Black
import com.tees.mad.e4089074.sharepoint.ui.theme.ErrorRed
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple40
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple60
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple80
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleGrey40
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleRoyal
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleSoft
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

@Composable
fun AccountStatItem(
    count: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurpleDeep,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = count,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )
        )

        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Gray
            )
        )
    }
}

@Composable
fun FlatProfileSectionCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(1.dp, Purple0)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun ProfileSectionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean = true,
    iconTint: Color = PurpleGrey40,
    titleColor: Color = Black
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = titleColor
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        if (showDivider) {
            Divider(
                color = Purple0,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    thickness: Dp = 1.dp
) {
    Box(
        modifier
            .width(thickness)
            .fillMaxHeight()
            .background(color = color)
    )
}


