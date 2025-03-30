package com.tees.mad.e4089074.sharepoint.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tees.mad.e4089074.sharepoint.routes.AppRoute
import com.tees.mad.e4089074.sharepoint.ui.components.AuthScreenComponent
import com.tees.mad.e4089074.sharepoint.ui.components.forms.AppButton
import com.tees.mad.e4089074.sharepoint.ui.components.forms.AppTextInput
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.util.AuthResult
import com.tees.mad.e4089074.sharepoint.util.showToast
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val authResult by authViewModel.authResult.collectAsStateWithLifecycle()

    LaunchedEffect(authResult) {
        authResult?.let {
            if (it is AuthResult.Error) {
                showToast(context, it.message)
            }
            delay(500)
            authViewModel.resetResult()
        }
    }


    AuthScreenComponent(
        modifier,
        onBackArrowClick = { navController.popBackStack() },
        title = "Welcome Back",
        subtitle = "Sign in to continue"
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AppTextInput(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                fieldVisibility = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextInput(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                trailingIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                onTrailingIconClick = { passwordVisible = !passwordVisible },
                imeAction = ImeAction.Next,
                isPassword = true,
                fieldVisibility = !passwordVisible
            )

            // Forgot Password
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot Password?",
                    color = PurpleDeep,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable() {
                        navController.navigate(AppRoute.Auth.ForgotPassword.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign In Button
            AppButton(
                label = "Sign In",
                Modifier,
                onClick = {
                    authViewModel.login(email, password)
                },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // No account text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = "Sign Up",
                    color = PurpleDeep,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(AppRoute.Auth.Register.route)
                    }
                )
            }
        }
    }
}