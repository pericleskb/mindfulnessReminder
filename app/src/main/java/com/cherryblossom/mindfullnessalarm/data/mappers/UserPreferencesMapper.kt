package com.cherryblossom.mindfullnessalarm.data.mappers

import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.data.models.UserPreferences
import com.cherryblossom.mindfullnessalarm.ui.MainUiState

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
        enabled = isEnabled
    )
}