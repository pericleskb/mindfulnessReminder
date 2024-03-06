package com.cherryblossom.mindfullnessalarm.ui

import androidx.lifecycle.ViewModel
import com.cherryblossom.mindfullnessalarm.models.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel()  {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState>
        get() = _uiState.asStateFlow()

    fun startTimeChanged(hour: Int, minute: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                startTime = TimeOfDay(hour, minute)
            )
        }
    }

    fun endTimeChanged(hour: Int, minute: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                endTime = TimeOfDay(hour, minute)
            )
        }
    }

    fun numberOfRemindersChanged(num: String) {
        _uiState.update { currentState ->
            currentState.copy(
                numberOfReminders = num
            )
        }
    }

    fun enabled(isEnabled: Boolean): Unit {
        _uiState.update { currentState ->
            currentState.copy(
                isEnabled = isEnabled
            )
        }
    }
}