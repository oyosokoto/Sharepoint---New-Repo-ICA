package com.tees.mad.e4089074.sharepoint.util

import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser

fun showToast(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun formatNumber(number: Float): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.UK)
    return formatter.format(number)
}

fun isValidateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}
