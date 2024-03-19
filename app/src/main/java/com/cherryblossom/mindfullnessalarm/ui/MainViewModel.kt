package com.cherryblossom.mindfullnessalarm.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cherryblossom.mindfullnessalarm.WriteFileDelegate
import com.cherryblossom.mindfullnessalarm.alarms.AlarmUtils
import com.cherryblossom.mindfullnessalarm.broadcastReceivers.SetupRemindersReceiver
import com.cherryblossom.mindfullnessalarm.data.mappers.toUserPreferences
import com.cherryblossom.mindfullnessalarm.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.data.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application)  {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.getCurrentPreferences().run {
                _uiState.update { currentState ->
                    currentState.copy(
                        startTime = TimeOfDay(startHour, startMinute),
                        endTime = TimeOfDay(endHour, endMinute),
                        numberOfReminders = remindersPerDay,
                        isEnabled = enabled
                    )
                    //when this done hide loading screen
                }
            }
        }
    }

    fun startTimeChanged(hour: Int, minute: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                startTime = TimeOfDay(hour, minute),
                preferencesChanged = true
            )
        }
    }

    fun endTimeChanged(hour: Int, minute: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                endTime = TimeOfDay(hour, minute),
                preferencesChanged = true
            )
        }
    }

    fun numberOfRemindersChanged(num: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                numberOfReminders = num,
                preferencesChanged = true
            )
        }
    }

    fun updateReminders() {
        _uiState.update { currentState ->
            currentState.copy(
                preferencesChanged = false
            )
        }
        //todo cancel previous alarms
        viewModelScope.launch {
            userPreferencesRepository.updatePreferences(
                _uiState.value.toUserPreferences()
            )
            setUpAlarms(userPreferencesRepository.getCurrentPreferences())
        }
    }

    fun onOffChanged(): Unit {
        _uiState.update { currentState ->
            currentState.copy(
                isEnabled = !currentState.isEnabled,
                preferencesChanged = false
            )
        }
        viewModelScope.launch {
            userPreferencesRepository.updatePreferences(_uiState.value.toUserPreferences())
            if (uiState.value.isEnabled) {
                setUpAlarms(userPreferencesRepository.getCurrentPreferences())
            } else {
                //todo cancel
            }
        }
    }

    private suspend fun setUpAlarms(userPreferences: UserPreferences) {
        scheduleRepeatingAlarm()

        val startTime = TimeOfDay(userPreferences.startHour, userPreferences.startMinute)
        val endTime = TimeOfDay(userPreferences.endHour, userPreferences.endMinute)
        if(TimeOfDay.timeOfDayNow().isBetweenTimes(startTime, endTime)) {
            AlarmUtils.scheduleAlarms(getApplication<Application>().applicationContext)
        }
    }

    /**
     * This schedules a repeating alarm, every day, on start time of the reminders.
     * This alarm will send an Intent to ScheduleAlarms broadcast receiver. Which will schedule
     * the alarms for the day
     */
    private fun scheduleRepeatingAlarm() {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.getCurrentPreferences()

            val timeToStart = Calendar.getInstance()
            timeToStart.set(Calendar.HOUR_OF_DAY, userPreferences.startHour)
            timeToStart.set(Calendar.MINUTE, userPreferences.startMinute)
            timeToStart.time//todo check if needed
            if (Calendar.getInstance().after(timeToStart)) {
                //First alarm time has passed. Schedule for tomorrow
                timeToStart.add(Calendar.DATE, 1)
            }

            val context = getApplication<Application>().applicationContext
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, SetupRemindersReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }

            val delay = timeToStart.timeInMillis - System.currentTimeMillis()
            val triggerAt = SystemClock.elapsedRealtime() + delay
            println("@@ delay - $delay")
            try {
                //set scheduling of daily alarms, one hour before start time so that the intent
                //can be delivered on time
                timeToStart.add(Calendar.HOUR, -1)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    timeToStart.timeInMillis,
                    1000 * 60 * 60 * 24,
                    alarmIntent
                )
                logNextScheduleTime(context, timeToStart)
            } catch (e: SecurityException) {
                Log.e("MindlessReminder", e.toString())
            }
        }
    }

    private suspend fun logNextScheduleTime(context: Context, timeToStart: Calendar) {
        val logFileUri = userPreferencesRepository.getCurrentPreferences().logFileUri ?: return
        val triggerTimeOfDay = TimeOfDay(
            timeToStart.get(Calendar.HOUR_OF_DAY),
            timeToStart.get(Calendar.MINUTE))
        val content = "Time now = ${TimeOfDay.timeOfDayNow()} - Next schedule time: $triggerTimeOfDay ${timeToStart.get(Calendar.DAY_OF_MONTH)}\\${timeToStart.get(Calendar.MONTH) + 1} \n"
        WriteFileDelegate(context).appendToFile(Uri.parse(logFileUri), content)
    }

    fun saveLogFile(uri: Uri) {
        viewModelScope.launch {
            userPreferencesRepository.updateLogFileUri(uri.toString())
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: UserPreferencesRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}