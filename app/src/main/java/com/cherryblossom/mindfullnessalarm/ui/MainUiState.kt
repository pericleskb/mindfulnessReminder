package com.cherryblossom.mindfullnessalarm.ui

import com.cherryblossom.mindfullnessalarm.models.TimeOfDay

data class MainUiState(
    val startTime: TimeOfDay = TimeOfDay(9, 0),
    val endTime: TimeOfDay = TimeOfDay(0, 0),
    val numberOfReminders: String = "3"
)