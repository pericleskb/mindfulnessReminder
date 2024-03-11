package com.cherryblossom.mindfullnessalarm

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import com.cherryblossom.mindfullnessalarm.ui.MainUiState
import com.cherryblossom.mindfullnessalarm.ui.ScreenHost
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme


val Context.dataStore by preferencesDataStore(name = MainUiState.USER_PREFERENCES_NAME)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Box(Modifier.safeDrawingPadding()) {
                MindfullnessAlarmTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        tonalElevation = 5.dp,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ScreenHost()
                    }
                }
            }
        }
    }
}