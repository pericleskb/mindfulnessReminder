package com.cherryblossom.mindfullnessalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cherryblossom.mindfullnessalarm.utils.AlarmSchedulingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            CoroutineScope(Job() + Dispatchers.Default).launch {
                AlarmSchedulingUtils.setUpAlarms(context)
            }
        }
    }
}