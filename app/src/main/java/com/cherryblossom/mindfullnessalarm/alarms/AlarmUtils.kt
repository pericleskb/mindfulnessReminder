package com.cherryblossom.mindfullnessalarm.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import androidx.compose.ui.platform.LocalContext
import com.cherryblossom.mindfullnessalarm.WriteFileDelegate
import com.cherryblossom.mindfullnessalarm.broadcastReceivers.RingAlarmReceiver
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.data.models.UserPreferences
import com.cherryblossom.mindfullnessalarm.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfullnessalarm.dataStore
import java.util.Calendar

class AlarmUtils {
    companion object {

        suspend fun scheduleAlarms(context: Context) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val repository = UserPreferencesRepository(context.dataStore)
            val userPreferences = repository.getCurrentPreferences()

            val logFileUri = userPreferences.logFileUri?.let {
                Uri.parse(it)
            }
            val startTime = TimeOfDay(userPreferences.startHour, userPreferences.startMinute)
            val endTime = TimeOfDay(userPreferences.endHour, userPreferences.endMinute)
            val evenDistributionMs = calculateEvenDistributionMs(startTime, endTime, userPreferences.remindersPerDay)

            getAlarmIntervals(startTime, endTime, evenDistributionMs, userPreferences.remindersPerDay)
                .forEachIndexed { index, interval ->
                    logFileUri?.let {
                        WriteFileDelegate(context).appendToFile(it, "$interval - ")
                    }
                    if (timeInBoundaries(userPreferences, interval)) {
                        setUpAlarm(context, alarmManager, interval, evenDistributionMs, index, logFileUri)
                    }
                }
        }

        private fun getAlarmIntervals(startTime: TimeOfDay,
                                      endTime: TimeOfDay,
                                      evenDistributionMs: Long,
                                      remindersPerDay: Int
        ): ArrayList<Long> {
            val firstAlarmDelay = calculateFirstAlarmDelay(startTime, endTime)

            val intervalsList = arrayListOf<Long>()
            intervalsList.add(firstAlarmDelay) //+ evenDistributionMs)
            for (i in 1 until remindersPerDay) {
                val interval = firstAlarmDelay + (i * evenDistributionMs)
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

        private fun setUpAlarm(context: Context,
                               alarmManager: AlarmManager,
                               triggerAfterMillis: Long,
                               evenDistributionMs: Long,
                               index: Int,
                               logFileUri: Uri?
        ) {
            val alarmIntent = Intent(context, RingAlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, index, intent, PendingIntent.FLAG_IMMUTABLE)
            }
            val triggerAt = System.currentTimeMillis() + triggerAfterMillis
            println(triggerAfterMillis)
            //TODO consider using setWindow() instead of setExact to reduce resources consumption
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    alarmIntent
                )
//            try {
//                alarmManager.setWindow(
//                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    triggerAt,
//                    evenDistributionMs,
//                    alarmIntent
//                )
                logFileUri?.let {
                    logAlarmTime(context, triggerAt, logFileUri)
                }
            } catch (e: SecurityException) {

            }
        }

        private fun logAlarmTime(context: Context, triggerAt: Long, logFileUri: Uri) {
            val triggerDate = Calendar.getInstance()
            triggerDate.timeInMillis = triggerAt
            val triggerTimeOfDay = TimeOfDay(
                triggerDate.get(Calendar.HOUR_OF_DAY),
                triggerDate.get(Calendar.MINUTE))
            val content = "Alarm scheduled for $triggerTimeOfDay\n"
            WriteFileDelegate(context).appendToFile(logFileUri, content)
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
    }
}


//private fun generateFirstInterval(evenDistribution: Long): Long {
//    return ThreadLocalRandom.current().nextLong(minValue, (0.83 * evenDistribution).toLong())
//}