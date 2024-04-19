package com.cherryblossom.mindfulnessreminder.data.models

import java.util.Calendar

data class TimeOfDay(
    @androidx.annotation.IntRange(from = 0, to = 23)
    val hour: Int,
    @androidx.annotation.IntRange(from = 0, to = 59)
    val minute: Int
) {
    companion object {
        fun timeOfDayNow(): TimeOfDay {
            val calendarNow = Calendar.getInstance()
            return TimeOfDay(
                calendarNow.get(Calendar.HOUR_OF_DAY),
                calendarNow.get(Calendar.MINUTE))
        }
    }
    override fun toString(): String {
        return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}"
    }

    fun isBetweenTimes(start: TimeOfDay, end: TimeOfDay): Boolean {
        val pastStartTime = start.isBefore(this)
        val pastEndTime = end.isBefore(this)

        return if (start.isBefore(end)) {
            //both reminders on the same day
            pastStartTime && !pastEndTime
        } else {
            // case when end time is on the next day
            !(pastEndTime && !pastStartTime) //not simplifying 'cause easier to understand this way
        }
    }

    fun isBefore(other: TimeOfDay): Boolean {
        if (hour < other.hour) {
            return true
        } else if (hour == other.hour && minute <= other.minute) {
            return true
        }
        return false
    }
}