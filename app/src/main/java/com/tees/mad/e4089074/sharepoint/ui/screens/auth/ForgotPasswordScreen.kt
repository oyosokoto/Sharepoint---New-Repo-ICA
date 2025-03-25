package com.tees.mad.e4089074.sharepoint.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.tees.mad.e4089074.sharepoint.ui.components.AuthScreenComponent
import com.tees.mad.e4089074.sharepoint.ui.components.forms.AppButton
import com.tees.mad.e4089074.sharepoint.ui.components.forms.AppTextInput
import com.tees.mad.e4089074.sharepoint.util.AuthResult
import com.tees.mad.e4089074.sharepoint.util.showToast
import com.tees.mad.e4089074.sharepoint.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val authResult by authViewModel.authResult.collectAsStateWithLifecycle()

    LaunchedEffect(authResult) {
        authResult?.let {
            if (it is AuthResult.Error) {
                showToast(context, it.message)
            } else {
                showToast(context, "Password reset email sent successfully")
                delay(500)
                navController.popBackStack()
            }
            delay(500)
            authViewModel.resetResult()
        }
    }


    AuthScreenComponent(
        modifier,
        onBackArrowClick = { navController.popBackStack() },
        title = "Forgot Password",
        subtitle = "Enter your email to reset your password"
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            AppTextInput(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                label = "Reset Password",
                isLoading = isLoading,
                onClick = {
                    authViewModel.sendPasswordResetEmail(email)
                }
            )
        }


    }

}