package com.cherryblossom.mindfullnessalarm.models

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