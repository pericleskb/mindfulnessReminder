package com.cherryblossom.mindfullnessalarm.ui

data class MainUiState(
    val startTime: TimeOfDay = TimeOfDay(9, 0),
    val endTime: TimeOfDay = TimeOfDay(0, 0),
    @androidx.annotation.IntRange(from = 0, to = 10)
    val numberOfAlarms: Int = 3
)

data class TimeOfDay(
    @androidx.annotation.IntRange(from = 0, to = 23)
    val hour: Int,
    @androidx.annotation.IntRange(from = 0, to = 59)
    val minute: Int
) {
    override fun toString(): String {
        return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
    }
}