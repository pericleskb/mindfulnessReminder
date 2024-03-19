package com.cherryblossom.mindfullnessalarm.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.WriteFileDelegate
import com.cherryblossom.mindfullnessalarm.alarms.AlarmUtils
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfullnessalarm.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar

class RingAlarmReceiver: BroadcastReceiver() {

    companion object {
        private var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
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
        CoroutineScope(Job() + Dispatchers.Default).launch {
            logRingTime(context)
        }
    }

    private suspend fun logRingTime(context: Context) {
        val repository = UserPreferencesRepository(context.dataStore)
        val userPreferences = repository.getCurrentPreferences()

        userPreferences.logFileUri?.let { uri ->
            val triggerDate = Calendar.getInstance()
            val triggerTimeOfDay = TimeOfDay(
                triggerDate.get(Calendar.HOUR_OF_DAY),
                triggerDate.get(Calendar.MINUTE))
            val content = "Rang at $triggerTimeOfDay\n"
            WriteFileDelegate(context).appendToFile(Uri.parse(uri), content)
        }
    }
}