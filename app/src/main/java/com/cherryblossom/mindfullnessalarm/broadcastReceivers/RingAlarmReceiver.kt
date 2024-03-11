package com.cherryblossom.mindfullnessalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.WriteFileDelegate
import com.cherryblossom.mindfullnessalarm.alarms.AlarmUtils
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import java.util.Calendar

class RingAlarmReceiver: BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        logRingTime(context)
        context.applicationContext.let { context ->
            mediaPlayer = MediaPlayer.create(context, R.raw.ting).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnCompletionListener {
                    it.reset()
                    it.release()
                    mediaPlayer = null
                }
                start()
            }
        }
    }

    private fun logRingTime(context: Context) {
        val triggerDate = Calendar.getInstance()
        val triggerTimeOfDay = TimeOfDay(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        val content = "Rang at $triggerTimeOfDay\n"
        WriteFileDelegate(context).appendToFile(AlarmUtils.URI, content)
    }
}