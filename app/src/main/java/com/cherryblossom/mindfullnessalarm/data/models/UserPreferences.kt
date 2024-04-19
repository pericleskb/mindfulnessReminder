package com.cherryblossom.mindfullnessalarm.data.models

data class UserPreferences(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val remindersPerDay: Int,
    val enabled: Boolean,
    val logFileUri: String?,
    val firstTimeEnabling: Boolean = true
)
