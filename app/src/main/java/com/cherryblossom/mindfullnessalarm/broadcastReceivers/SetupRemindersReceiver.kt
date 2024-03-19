package com.cherryblossom.mindfullnessalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cherryblossom.mindfullnessalarm.alarms.AlarmUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetupRemindersReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        println("@@set up reminders receiver@")
        val scope = CoroutineScope(Job() + Dispatchers.Default)
        scope.launch {
            AlarmUtils.scheduleAlarms(context)
        }
    }
}