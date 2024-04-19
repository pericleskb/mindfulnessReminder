package com.cherryblossom.mindfulnessreminder.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cherryblossom.mindfulnessreminder.utils.AlarmSchedulingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED", true) ||
            intent.action.equals("android.intent.action.QUICKBOOT_POWERON", true)) {
            CoroutineScope(Job() + Dispatchers.Default).launch {
                AlarmSchedulingUtils.setUpAlarms(context)
            }
        }
    }
}