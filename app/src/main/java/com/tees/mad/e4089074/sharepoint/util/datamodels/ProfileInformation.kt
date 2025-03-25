package com.tees.mad.e4089074.sharepoint.util.datamodels

data class ProfileInformation(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profileImage: String = "",
    val notificationsEnabled: Boolean = true
)