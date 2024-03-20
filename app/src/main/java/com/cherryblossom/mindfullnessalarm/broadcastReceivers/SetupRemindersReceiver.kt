package com.cherryblossom.mindfullnessalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cherryblossom.mindfullnessalarm.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfullnessalarm.dataStore
import com.cherryblossom.mindfullnessalarm.utils.AlarmSchedulingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetupRemindersReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        println("@@set up reminders receiver@")
        val repository = UserPreferencesRepository(context.dataStore)
        val scope = CoroutineScope(Job() + Dispatchers.Default)
        scope.launch {
            val userPreferences = repository.getCurrentPreferences()
            AlarmSchedulingUtils.scheduleReminders(context, userPreferences)
        }
    }
}