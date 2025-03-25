package com.tees.mad.e4089074.sharepoint.util

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}
