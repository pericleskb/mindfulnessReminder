package com.cherryblossom.mindfullnessalarm.ui

import com.cherryblossom.mindfullnessalarm.models.TimeOfDay

data class MainUiState(
    val startTime: TimeOfDay = TimeOfDay(9, 0),
    val endTime: TimeOfDay = TimeOfDay(0, 0),
    @androidx.annotation.IntRange(from = 0, to = 10)
    val numberOfReminders: Int = 3
)