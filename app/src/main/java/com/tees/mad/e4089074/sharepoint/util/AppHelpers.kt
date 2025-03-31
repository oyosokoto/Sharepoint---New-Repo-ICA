package com.tees.mad.e4089074.sharepoint.util

import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


/**
 * Formats a given nullable Date into "Month Day, Year" format (e.g., "December 23, 2023").
 * If the input date is null, it formats today's date.
 *
 * @param inputDate The Date object to format, or null to use today's date.
 * @return A string representing the date in "MMMM dd, yyyy" format.
 */
fun formatDateOrDefault(inputDate: Date?): String {
    // 1. Determine the date to format: use inputDate if not null, otherwise use the current date.
    // The Date() constructor without arguments defaults to the current date and time.
    val dateToFormat: Date = inputDate ?: Date()

    // 2. Create a Date Formatter for the desired pattern ("Month Day, Year")
    // Using Locale.ENGLISH ensures month names are in English regardless of system default.
    val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)

    // 3. Format the date and return the string
    return formatter.format(dateToFormat)
}