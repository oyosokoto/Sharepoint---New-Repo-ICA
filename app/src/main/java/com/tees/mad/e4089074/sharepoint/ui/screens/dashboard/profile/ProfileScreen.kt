package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tees.mad.e4089074.sharepoint.R
import com.tees.mad.e4089074.sharepoint.ui.components.profile.ProfileSectionCard
import com.tees.mad.e4089074.sharepoint.ui.components.profile.ProfileSectionItem
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple40
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple60
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple80
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Purple0,
                            Purple20,
                            Purple40,
                            Purple60,
                            Purple80,
                            PurpleSoft,
                            PurpleDeep,
                            PurpleRoyal
                        )
                    )
                )
                .blur(radius = 10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Profile Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_avatar),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = listOfNotNull(
                        userProfile?.firstName,
                        userProfile?.lastName
                    ).joinToString(" "),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@${userProfile?.email?.split("@")?.first() ?: ""}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // First Section
            ProfileSectionCard {
                ProfileSectionItem(
                    icon = Icons.AutoMirrored.Outlined.Help,
                    title = "Help",
                    onClick = { showToast(context, "Help") }
                )

                ProfileSectionItem(
                    icon = Icons.Outlined.Inbox,
                    title = "Inbox",
                    onClick = { showToast(context, "Inbox") },
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color.Red, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                )

                ProfileSectionItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Notification Settings",
                    onClick = { showToast(context, "Notification Settings") },
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Second Section
            ProfileSectionCard {
                ProfileSectionItem(
                    icon = Icons.Outlined.Security,
                    title = "Security",
                    onClick = { showToast(context, "Security") }
                )

                ProfileSectionItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    title = "Log Out",
                    onClick = { authViewModel.logout() },
                    showDivider = false
                )
            }
        }
    }
}


