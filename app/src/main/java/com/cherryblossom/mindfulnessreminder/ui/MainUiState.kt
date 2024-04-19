package com.cherryblossom.mindfulnessreminder.ui

import com.cherryblossom.mindfulnessreminder.data.models.TimeOfDay

data class MainUiState(
    val startTime: TimeOfDay = TimeOfDay(9, 0),
    val endTime: TimeOfDay = TimeOfDay(0, 0),
    val numberOfReminders: Int = 3,
    val isEnabled: Boolean = false,
    val preferencesChanged: Boolean = false,
    val splashScreenVisible: Boolean = true,
    val showXiaomiRebootScreen: Boolean = false
) {
    companion object {
        val USER_PREFERENCES_NAME = "user_preferences"
    }
}