package com.cherryblossom.mindfullnessalarm.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.cherryblossom.mindfullnessalarm.broadcastReceivers.RingAlarmReceiver
import com.cherryblossom.mindfullnessalarm.broadcastReceivers.SetupRemindersReceiver

class PendingIntentsProvider {

    companion object {

        private const val SCHEDULING_REQUEST_CODE = 100

        fun getSchedulingPendingIntent(context: Context): PendingIntent {
            return Intent(context, SetupRemindersReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, SCHEDULING_REQUEST_CODE, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
        }

        fun getReminderPendingIntent(context: Context, index: Int): PendingIntent {
            return Intent(context, RingAlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, index, intent, PendingIntent.FLAG_IMMUTABLE)
            }
        }
    }
}