package com.tees.mad.e4089074.sharepoint.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tees.mad.e4089074.sharepoint.util.datamodels.ProfileInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val _userProfile = MutableStateFlow<ProfileInformation?>(null)
    val userProfile: StateFlow<ProfileInformation?> = _userProfile

    // Fetch user profile from Firestore
    fun getUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (userDoc.exists()) {
                _userProfile.value = userDoc.toObject(ProfileInformation::class.java)
            }
        }
    }

    // Create a default profile if it doesn't exist in Firestore
    private suspend fun createUserProfile(userId: String) {
        val user = auth.currentUser
        val defaultProfile = ProfileInformation(
            userId = userId,
            firstName = user?.displayName?.substringBefore(" ") ?: "",
            lastName = user?.displayName?.substringAfter(" ") ?: "",
            email = user?.email ?: "",
            profileImage = "", // Default empty image
            notificationsEnabled = true
        )
        firestore.collection("users").document(userId).set(defaultProfile).await()
        _userProfile.value = defaultProfile
    }

    // Update user profile fields (firstName, lastName, notificationsEnabled)
    fun updateUserProfile(firstName: String, lastName: String, notificationsEnabled: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val updatedProfile = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "notificationsEnabled" to notificationsEnabled
        )
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId).update(updatedProfile).await()
                // Update the local state
                _userProfile.value = _userProfile.value?.copy(firstName = firstName, lastName = lastName, notificationsEnabled = notificationsEnabled)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Profile update failed", e)
            }
        }
    }

    // Upload profile image to Firebase Storage and update the user profile with the URL
    fun uploadProfileImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        viewModelScope.launch {
            try {
                // Upload image to Firebase Storage
                val uploadTask = storageRef.putFile(imageUri).await()

                // Get the image URL
                val imageUrl = storageRef.downloadUrl.await().toString()

                // Update the user profile in Firestore with the image URL
                firestore.collection("users").document(userId).update("profileImage", imageUrl).await()

                // Update the local state with the new profile image URL
                _userProfile.value = _userProfile.value?.copy(profileImage = imageUrl)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Image upload failed", e)
            }
        }
    }
}

