package com.tees.mad.e4089074.sharepoint.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Singleton object to manage application configuration
 */
object AppConfig {
    // Default values
    private const val DEFAULT_PAYMENT_API_URL = "https://splitiou.vercel.app/"
    
    // Current values (in-memory)
    private var paymentApiUrl = DEFAULT_PAYMENT_API_URL
    
    /**
     * Get the current payment API URL
     */
    fun getPaymentApiUrl(): String {
        return paymentApiUrl
    }
    
    /**
     * Set the payment API URL
     */
    fun setPaymentApiUrl(url: String) {
        paymentApiUrl = url
    }
    
    /**
     * Reset to default values
     */
    fun resetToDefaults() {
        paymentApiUrl = DEFAULT_PAYMENT_API_URL
    }
}

/**
 * Extension for Context to access DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_config")

/**
 * DataStore keys
 */
private object PreferencesKeys {
    val PAYMENT_API_URL = stringPreferencesKey("payment_api_url")
}

/**
 * Class to manage persistent configuration using DataStore
 */
class AppConfigDataStore(private val context: Context) {
    /**
     * Get the payment API URL from DataStore
     */
    val paymentApiUrl: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PAYMENT_API_URL] ?: AppConfig.getPaymentApiUrl()
        }
    
    /**
     * Save the payment API URL to DataStore
     */
    suspend fun savePaymentApiUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAYMENT_API_URL] = url
        }
        AppConfig.setPaymentApiUrl(url)
    }
}
