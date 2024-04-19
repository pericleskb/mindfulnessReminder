package com.cherryblossom.mindfulnessreminder.ui

import android.app.AlarmManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cherryblossom.mindfulnessreminder.broadcastReceivers.BootReceiver
import com.cherryblossom.mindfulnessreminder.utils.AlarmSchedulingUtils
import com.cherryblossom.mindfulnessreminder.data.mappers.toUserPreferences
import com.cherryblossom.mindfulnessreminder.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfulnessreminder.data.models.TimeOfDay
import com.cherryblossom.mindfulnessreminder.utils.PendingIntentsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application)  {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState>
        get() = _uiState.asStateFlow()
    var isReady = false;

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
                isReady = true
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
       cancelAlarms(getApplication<Application>().applicationContext)
        viewModelScope.launch {
            userPreferencesRepository.updatePreferences(
                _uiState.value.toUserPreferences()
            )
            AlarmSchedulingUtils.setUpAlarms(getApplication<Application>().applicationContext)
        }
    }

    fun onOffChanged() {
        viewModelScope.launch {
            val shouldShowXiaomiReboot = userPreferencesRepository.getCurrentPreferences().firstTimeEnabling && isXiaomi()
            _uiState.update { currentState ->
                currentState.copy(
                    isEnabled = !currentState.isEnabled,
                    preferencesChanged = false,
                    showXiaomiRebootScreen = shouldShowXiaomiReboot
                )
            }
            userPreferencesRepository.updatePreferences(_uiState.value.toUserPreferences())
            userPreferencesRepository.updateFirstTimeEnabling(false)
            setBootReceiverState(uiState.value.isEnabled)
            val context = getApplication<Application>().applicationContext
            if (uiState.value.isEnabled) {
                AlarmSchedulingUtils.setUpAlarms(context)
            } else {
                cancelAlarms(context)
            }
        }
    }

    private fun isXiaomi(): Boolean {
        val manufacturer = "xiaomi"
        return manufacturer.equals(Build.MANUFACTURER, ignoreCase = true)
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

    fun disableXiaomiDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showXiaomiRebootScreen = false
            )
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