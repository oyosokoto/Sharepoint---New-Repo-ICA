package com.tees.mad.e4089074.sharepoint.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.tees.mad.e4089074.sharepoint.util.AuthResult
import com.tees.mad.e4089074.sharepoint.util.AuthState
import com.tees.mad.e4089074.sharepoint.util.datamodels.ProfileInformation
import com.tees.mad.e4089074.sharepoint.util.datamodels.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = firebaseAuth.currentUser?.let {
                AuthState.Authenticated(it)
            } ?: AuthState.Unauthenticated
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

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        if (!validateRegistration(firstName, lastName, email, password)) return

        viewModelScope.launch {
            performAuthAction("Registration failed") {
                // Create user with email and password
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                // Update user profile with display name
                result.user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()
                )?.await()

                // Send email verification
                result.user?.sendEmailVerification()?.await()

                // Create user profile in Firestore
                val userId = result.user?.uid ?: ""
                val defaultProfile = ProfileInformation(
                    userId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    profileImage = "", // Default empty image
                    notificationsEnabled = true
                )

                // Save profile to Firestore
                firestore.collection("users")
                    .document(userId)
                    .set(defaultProfile)
                    .await()
            }
        }
    }

    fun getUserProfile(): UserProfile? {
        val user = auth.currentUser
        return user?.let {
            UserProfile(
                firstName = it.displayName?.substringBefore(" ") ?: "",
                lastName = it.displayName?.substringAfter(" ") ?: "",
                email = it.email ?: "",
                profileImage = it.photoUrl?.toString() ?: ""
            )
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

    fun changePassword(currentPassword: String, newPassword: String) {
        val currentUser = auth.currentUser ?: run {
            _authResult.value = AuthResult.Error("User not authenticated")
            return
        }

        if (!isStrongPassword(newPassword)) {
            _authResult.value = AuthResult.Error(
                "New password must be at least 8 characters long and contain " +
                        "at least one uppercase letter, one lowercase letter, " +
                        "and one special character"
            )
            return
        }

        viewModelScope.launch {
            performAuthAction("Unable to change password, please try again.") {
                val credential =
                    EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                currentUser.reauthenticate(credential).await()
                currentUser.updatePassword(newPassword).await()
            }
        }
    }

    private suspend fun performAuthAction(
        errorMessage: String,
        action: suspend () -> Unit
    ) {
        try {
            _isLoading.value = true
            _authResult.value = null
            action()
            _authResult.value = AuthResult.Success
        } catch (e: FirebaseAuthException) {
            val errorText =
                if (e
                        .localizedMessage?.contains(
                            "The supplied auth credential is incorrect, malformed or has expired."
                        ) == true
                ) {
                    "Invalid email or password"
                } else {
                    e.localizedMessage ?: errorMessage
                }
            _authResult.value = AuthResult.Error(errorText)
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

            !isValidEmail(email) -> {
                _authResult.value = AuthResult.Error("Invalid email format")
                false
            }

            !isStrongPassword(password) -> {
                _authResult.value = AuthResult.Error(
                    "Password must be at least 8 characters long and contain " +
                            "at least one uppercase letter, one lowercase letter, " +
                            "and one special character"
                )
                false
            }

            else -> true
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { !it.isLetterOrDigit() }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}