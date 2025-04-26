package com.example.lungoapp.data.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val userIdKey = intPreferencesKey("user_id")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userTokenKey = stringPreferencesKey("user_token")

    private var cachedToken: String? = null

    val userId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[userIdKey]
    }

    val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[userNameKey]
    }

    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[userEmailKey]
    }

    suspend fun saveUserData(id: Int, name: String, email: String, token: String) {
        cachedToken = token
        dataStore.edit { preferences ->
            preferences[userIdKey] = id
            preferences[userNameKey] = name
            preferences[userEmailKey] = email
            preferences[userTokenKey] = token
        }
    }

    suspend fun clearUserData() {
        cachedToken = null
        dataStore.edit { preferences ->
            preferences.remove(userIdKey)
            preferences.remove(userNameKey)
            preferences.remove(userEmailKey)
            preferences.remove(userTokenKey)
        }
    }

    fun getToken(): String? {
        return cachedToken
    }

    suspend fun refreshToken() {
        cachedToken = dataStore.data.first()[userTokenKey]
    }
} 