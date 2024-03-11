package com.cherryblossom.mindfullnessalarm.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.cherryblossom.mindfullnessalarm.data.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val START_HOUR = intPreferencesKey("start_hour")
        val START_MINUTE = intPreferencesKey("start_minute")
        val END_HOUR = intPreferencesKey("end_hour")
        val END_MINUTE = intPreferencesKey("end_minute")
        val REMINDERS_PER_DAY = intPreferencesKey("reminders_per_day")
        val ENABLED = booleanPreferencesKey("enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("", "")
            } else {
                throw exception
            }
        }.map { preferences ->
            mapPreferences(preferences)
        }

    suspend fun getCurrentPreferences(): UserPreferences {
        return userPreferencesFlow.first()
    }

    suspend fun updatePreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_HOUR] = userPreferences.startHour
            preferences[PreferencesKeys.START_MINUTE] = userPreferences.startMinute
            preferences[PreferencesKeys.END_HOUR] = userPreferences.endHour
            preferences[PreferencesKeys.END_MINUTE] = userPreferences.endMinute
            preferences[PreferencesKeys.REMINDERS_PER_DAY] = userPreferences.remindersPerDay
            preferences[PreferencesKeys.ENABLED] = userPreferences.enabled
        }
    }

    private fun mapPreferences(preferences: Preferences): UserPreferences {
        val startHour = preferences[PreferencesKeys.START_HOUR] ?: 9
        val startMinute = preferences[PreferencesKeys.START_MINUTE] ?: 0
        val endHour = preferences[PreferencesKeys.END_HOUR] ?: 0
        val endMinute = preferences[PreferencesKeys.END_MINUTE] ?: 0
        val remindersPerDay = preferences[PreferencesKeys.REMINDERS_PER_DAY] ?: 0
        val enabled = preferences[PreferencesKeys.ENABLED] ?: false
        return UserPreferences(
            startHour, startMinute, endHour, endMinute, remindersPerDay, enabled
        )
    }
}