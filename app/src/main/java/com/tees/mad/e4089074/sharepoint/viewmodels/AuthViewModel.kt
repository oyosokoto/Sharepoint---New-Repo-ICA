package com.tees.mad.e4089074.sharepoint.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.tees.mad.e4089074.sharepoint.util.isValidateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance();
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    var authState: StateFlow<AuthState> = _authState

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    var authResult: StateFlow<AuthResult?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkAuthState()

        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                _authState.value = AuthState.Authenticated(currentUser)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    private fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                if (email.isEmpty() || password.isEmpty()) throw Exception("Email and password cannot be empty")
                if (!isValidateEmail(email)) throw Exception("Email passed is not a valid email address")
                _isLoading.value = true
                _authResult.value = null
                auth.signInWithEmailAndPassword(email, password)
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login Failed", e)
                _authResult.value = AuthResult.Error(e.message ?: "Login Failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                if (email.isEmpty() || password.isEmpty()) throw Exception("Email and password cannot be empty")
                if (firstName.isEmpty() || lastName.isEmpty()) throw Exception("First and last name cannot be empty")
                _isLoading.value = true  // Set loading to true when starting
                _authResult.value = null
                // Create user with email and password
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()

                // Update display name with first and last name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()

                authResult.user?.updateProfile(profileUpdates)?.await()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _authResult.value = AuthResult.Error(e.message ?: "Registration failed")
            } finally {
                _isLoading.value = false  // Set loading to false when finished
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

    // For password reset functionality
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _authResult.value = null
                auth.sendPasswordResetEmail(email).await()
                _authResult.value = AuthResult.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password reset failed", e)
                _authResult.value = AuthResult.Error(e.message ?: "Failed to send reset email")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()

}