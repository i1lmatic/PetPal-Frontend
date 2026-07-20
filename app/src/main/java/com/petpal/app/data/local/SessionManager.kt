package com.petpal.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "petpal_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("full_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("email")
        private val KEY_USER_ROLE = stringPreferencesKey("role")
        private val KEY_USER_STATUS = stringPreferencesKey("status")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }

    val userRoleFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ROLE] }

    val userStatusFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USER_STATUS] }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USER_NAME] }

    suspend fun saveTokenOnly(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun saveUserInfo(user: com.petpal.app.data.model.User) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_NAME] = user.full_name
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_ROLE] = user.role
            prefs[KEY_USER_STATUS] = user.status
        }
    }

    suspend fun getToken(): String? = context.dataStore.data.first()[KEY_TOKEN]

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
