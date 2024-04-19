package com.cherryblossom.mindfulnessreminder.data.mappers

import com.cherryblossom.mindfulnessreminder.data.models.TimeOfDay
import com.cherryblossom.mindfulnessreminder.data.models.UserPreferences
import com.cherryblossom.mindfulnessreminder.ui.MainUiState

fun UserPreferences.toMainUiState(): MainUiState {
    return MainUiState(
        startTime = TimeOfDay(startHour, startMinute),
        endTime = TimeOfDay(endHour, endMinute),
        numberOfReminders = remindersPerDay,
        isEnabled = enabled
    )
}

fun MainUiState.toUserPreferences(): UserPreferences {
    return UserPreferences(
        startHour = startTime.hour,
        startMinute = startTime.minute,
        endHour = endTime.hour,
        endMinute = endTime.minute,
        remindersPerDay = numberOfReminders,
        enabled = isEnabled,
        logFileUri = null
    )
}