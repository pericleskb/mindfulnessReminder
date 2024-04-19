package com.cherryblossom.mindfulnessreminder

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cherryblossom.mindfulnessreminder.data.repositories.UserPreferencesRepository
import com.cherryblossom.mindfulnessreminder.ui.MainUiState
import com.cherryblossom.mindfulnessreminder.ui.MainViewModel
import com.cherryblossom.mindfulnessreminder.ui.MainViewModelFactory
import com.cherryblossom.mindfulnessreminder.ui.ScreenHost
import com.cherryblossom.mindfulnessreminder.ui.theme.mindfulnessreminderTheme


val Context.dataStore by preferencesDataStore(name = MainUiState.USER_PREFERENCES_NAME)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(
                LocalContext.current.applicationContext as Application,
                UserPreferencesRepository(LocalContext.current.dataStore)
            ))
            Box(Modifier.safeDrawingPadding()) {
                mindfulnessreminderTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        tonalElevation = 5.dp,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ScreenHost(viewModel)
                    }
                }
            }
            addPreDrawListener(viewModel)
        }
        reportFullyDrawn()
    }

    @Composable
    fun addPreDrawListener(viewModel: MainViewModel) {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}