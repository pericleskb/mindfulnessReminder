package com.cherryblossom.mindfulnessreminder.utils

import android.app.AlarmManager
import android.content.Context
import android.net.Uri
import android.util.Log
import com.cherryblossom.mindfulnessreminder.WriteFileDelegate
import com.cherryblossom.mindfulnessreminder.data.models.TimeOfDay
import com.cherryblossom.mindfulnessreminder.data.models.UserPreferences
import com.cherryblossom.mindfulnessreminder.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfulnessreminder.dataStore
import java.util.Calendar
import java.util.concurrent.ThreadLocalRandom

class AlarmSchedulingUtils {
    companion object {

        suspend fun setUpAlarms(context: Context) {
            val repository = UserPreferencesRepository(context.dataStore)
            val userPreferences = repository.getCurrentPreferences()
            scheduleRepeatingAlarm(context, userPreferences)
            val startTime = TimeOfDay(userPreferences.startHour, userPreferences.startMinute)
            val endTime = TimeOfDay(userPreferences.endHour, userPreferences.endMinute)
            if(TimeOfDay.timeOfDayNow().isBetweenTimes(startTime, endTime)) {
                scheduleReminders(context, userPreferences)
            }
        }

        /**
         * This schedules a repeating alarm, every day, on start time of the reminders.
         * This alarm will send an Intent to ScheduleAlarms broadcast receiver. Which will schedule
         * the alarms for the day
         */
        private fun scheduleRepeatingAlarm(context: Context, userPreferences: UserPreferences) {
            val timeToStart: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, userPreferences.startHour)
                set(Calendar.MINUTE, userPreferences.startMinute)
            }
            timeToStart.time//todo check if needed
            if (Calendar.getInstance().after(timeToStart)) {
                //First alarm time has passed. Schedule for tomorrow
                timeToStart.add(Calendar.DATE, 1)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = PendingIntentsProvider.getSchedulingPendingIntent(context)
            val logFileUri = userPreferences.logFileUri?.let {
                Uri.parse(it)
            }

            try {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    timeToStart.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
                )
                logFileUri?.let {
                    logNextScheduleTime(context, timeToStart, logFileUri)
                }
            } catch (e: SecurityException) {
                Log.e("MindlessReminder", e.toString())
            }
        }

        fun scheduleReminders(context: Context, userPreferences: UserPreferences) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val logFileUri = userPreferences.logFileUri?.let {
                Uri.parse(it)
            }
            val startTime = TimeOfDay(userPreferences.startHour, userPreferences.startMinute)
            val endTime = TimeOfDay(userPreferences.endHour, userPreferences.endMinute)
            val evenDistributionMs = calculateEvenDistributionMs(startTime, endTime, userPreferences.remindersPerDay)

            getRemindersIntervals(startTime, endTime, evenDistributionMs, userPreferences.remindersPerDay)
                .forEachIndexed { index, interval ->
                    logFileUri?.let {
                        WriteFileDelegate(context).appendToFile(it, "$interval - ")
                    }
                    if (timeInBoundaries(userPreferences, interval)) {
                        setUpReminder(context, alarmManager, interval, index, logFileUri)
                    }
                }
        }

        private fun getRemindersIntervals(startTime: TimeOfDay,
                                          endTime: TimeOfDay,
                                          evenDistributionMs: Long,
                                          remindersPerDay: Int
        ): ArrayList<Long> {
            var firstAlarmDelay = calculateFirstAlarmDelay(startTime, endTime)
            firstAlarmDelay += addRandomDelay(evenDistributionMs, 0.83f)

            val intervalsList = arrayListOf<Long>()
            intervalsList.add(firstAlarmDelay)
            for (i in 1 until remindersPerDay) {
                val percentage = if (i == remindersPerDay - 1) 1f else 0.83f
                val interval = firstAlarmDelay + (i * evenDistributionMs) + addRandomDelay(evenDistributionMs, percentage)
                intervalsList.add(
                    interval
                )
            }
            return intervalsList
        }

        /* Here we need to find out if alarms should be activated right now or the next day.
         * To do this we need to find out if we are between the selected start and end time.
         * If we are, start right away. If we are not, start on the next start time.
         */
        private fun calculateFirstAlarmDelay(startTime: TimeOfDay, endTime: TimeOfDay): Long {
            if (TimeOfDay.timeOfDayNow().isBetweenTimes(startTime, endTime)) {
                return 0
            }

            val calendarStartTime = Calendar.getInstance()
            calendarStartTime.set(Calendar.HOUR_OF_DAY, startTime.hour)
            calendarStartTime.set(Calendar.MINUTE, startTime.minute)

            if (startTime.isBefore(TimeOfDay.timeOfDayNow())) {
                //not between alarms and start time is on the next day
                calendarStartTime.add(Calendar.DATE, 1)
            }
            return calendarStartTime.timeInMillis - System.currentTimeMillis()
        }

        private fun calculateEvenDistributionMs(startTime: TimeOfDay,
                                                endTime: TimeOfDay,
                                                numberOfAlarms: Int): Long {
            val calendarStartTime = Calendar.getInstance()
            calendarStartTime.set(Calendar.HOUR_OF_DAY, startTime.hour)
            calendarStartTime.set(Calendar.MINUTE, startTime.minute)
            calendarStartTime.set(Calendar.SECOND, 0)

            val calendarEndTime = Calendar.getInstance()
            calendarEndTime.set(Calendar.HOUR_OF_DAY, endTime.hour)
            calendarEndTime.set(Calendar.MINUTE, endTime.minute)
            calendarEndTime.set(Calendar.SECOND, 59)

            if (endTime.isBefore(startTime)) {
                calendarEndTime.add(Calendar.DATE, 1)
            }
            return (calendarEndTime.timeInMillis - calendarStartTime.timeInMillis) / numberOfAlarms
        }

        private fun setUpReminder(context: Context,
                                  alarmManager: AlarmManager,
                                  triggerAfterMillis: Long,
                                  index: Int,
                                  logFileUri: Uri?
        ) {
            val alarmIntent = PendingIntentsProvider.getReminderPendingIntent(context, index)
            val triggerAt = System.currentTimeMillis() + triggerAfterMillis
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    alarmIntent
                )
                logFileUri?.let {
                    logReminderTime(context, triggerAt, logFileUri)
                }
            } catch (e: SecurityException) {

            }
        }

        private fun timeInBoundaries(userPreferences: UserPreferences, triggerAfterMillis: Long): Boolean {
            val triggerDate = Calendar.getInstance()
            triggerDate.timeInMillis = System.currentTimeMillis() + triggerAfterMillis
            val triggerTimeOfDay = TimeOfDay(
                triggerDate.get(Calendar.HOUR_OF_DAY),
                triggerDate.get(Calendar.MINUTE))
            return triggerTimeOfDay.isBetweenTimes(
                TimeOfDay(userPreferences.startHour, userPreferences.startMinute),
                TimeOfDay(userPreferences.endHour, userPreferences.endMinute))
        }

        private fun logReminderTime(context: Context, triggerAt: Long, logFileUri: Uri) {
            val triggerDate = Calendar.getInstance()
            triggerDate.timeInMillis = triggerAt
            val triggerTimeOfDay = TimeOfDay(
                triggerDate.get(Calendar.HOUR_OF_DAY),
                triggerDate.get(Calendar.MINUTE))
            val content = "Alarm scheduled for $triggerTimeOfDay\n"
            WriteFileDelegate(context).appendToFile(logFileUri, content)
        }

        private fun logNextScheduleTime(context: Context, timeToStart: Calendar, logFileUri: Uri) {
            val triggerTimeOfDay = TimeOfDay(
                timeToStart.get(Calendar.HOUR_OF_DAY),
                timeToStart.get(Calendar.MINUTE))
            val content = "Time now = ${TimeOfDay.timeOfDayNow()} - Next schedule time: $triggerTimeOfDay ${timeToStart.get(Calendar.DAY_OF_MONTH)}\\${timeToStart.get(Calendar.MONTH) + 1} \n"
            WriteFileDelegate(context).appendToFile(logFileUri, content)
        }

        private fun addRandomDelay(evenDistribution: Long, percentage: Float): Long {
            return ThreadLocalRandom.current().nextLong(0, (percentage * evenDistribution).toLong())
        }
    }
}

