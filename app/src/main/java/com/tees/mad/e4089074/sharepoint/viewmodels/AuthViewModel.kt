package com.tees.mad.e4089074.sharepoint.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = firebaseAuth.currentUser?.let { AuthState.Authenticated(it) }
                ?: AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if (!validateCredentials(email, password)) return
        viewModelScope.launch {
            performAuthAction("Login Failed") {
                auth.signInWithEmailAndPassword(email, password).await()
            }
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        if (!validateRegistration(firstName, lastName, email, password)) return
        viewModelScope.launch {
            performAuthAction("Registration failed") {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName("$firstName $lastName")
                        .build()
                )?.await()
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _authResult.value = AuthResult.Error("Email cannot be empty")
            return
        }
        if (!isValidEmail(email)) {
            _authResult.value = AuthResult.Error("Invalid email format")
            return
        }
        viewModelScope.launch {
            performAuthAction("Failed to send reset email") {
                auth.sendPasswordResetEmail(email).await()
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun resetResult() {
        _authResult.value = null
    }

    private suspend fun performAuthAction(errorMessage: String, action: suspend () -> Unit) {
        try {
            _isLoading.value = true
            _authResult.value = null
            action()
            _authResult.value = AuthResult.Success
        } catch (e: FirebaseAuthException) {
            if (e.localizedMessage?.contains("The supplied auth credential is incorrect, malformed or has expired.") == true) {
                _authResult.value = AuthResult.Error("Invalid email or password")
            } else {
                _authResult.value = AuthResult.Error(e.localizedMessage ?: errorMessage)
            }
        } catch (e: Exception) {
            _authResult.value = AuthResult.Error(errorMessage)
        } finally {
            _isLoading.value = false
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                _authResult.value = AuthResult.Error("Email and password cannot be empty")
                false
            }

            !isValidEmail(email) -> {
                _authResult.value = AuthResult.Error("Invalid email format")
                false
            }

            else -> true
        }
    }

    private fun validateRegistration(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        return when {
            firstName.isBlank() || lastName.isBlank() -> {
                _authResult.value = AuthResult.Error("First and last name cannot be empty")
                false
            }

            !validateCredentials(email, password) -> false
            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}
