package com.cherryblossom.mindfullnessalarm.ui

import android.app.AlarmManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cherryblossom.mindfullnessalarm.WriteFileDelegate
import com.cherryblossom.mindfullnessalarm.broadcastReceivers.BootReceiver
import com.cherryblossom.mindfullnessalarm.utils.AlarmSchedulingUtils
import com.cherryblossom.mindfullnessalarm.data.mappers.toUserPreferences
import com.cherryblossom.mindfullnessalarm.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.data.models.UserPreferences
import com.cherryblossom.mindfullnessalarm.utils.PendingIntentsProvider
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
                        isEnabled = enabled,
                        splashScreenVisible = false
                    )
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
            AlarmSchedulingUtils.setUpAlarms(getApplication<Application>().applicationContext)
        }
    }

    fun onOffChanged(): Unit {
        _uiState.update { currentState ->
            currentState.copy(
                isEnabled = !currentState.isEnabled,
                preferencesChanged = false
            )
        }
        setBootReceiverState(uiState.value.isEnabled)
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            userPreferencesRepository.updatePreferences(_uiState.value.toUserPreferences())
            if (uiState.value.isEnabled) {
                AlarmSchedulingUtils.setUpAlarms(context)
            } else {
                cancelAlarms(context)
            }
        }
    }

    private fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntentsProvider.getSchedulingPendingIntent(context))
        for (i in 0..9)
            alarmManager.cancel(PendingIntentsProvider.getReminderPendingIntent(context, i))
    }

    fun saveLogFile(uri: Uri) {
        viewModelScope.launch {
            userPreferencesRepository.updateLogFileUri(uri.toString())
        }
    }

    private fun setBootReceiverState(enabled: Boolean) {
        val state = if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        val context = getApplication<Application>().applicationContext
        val receiver = ComponentName(context, BootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            state,
            PackageManager.DONT_KILL_APP
        )
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