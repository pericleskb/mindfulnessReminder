package com.cherryblossom.mindfullnessalarm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cherryblossom.mindfullnessalarm.composables.dialogs.PickTimeDialog
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme
import kotlinx.coroutines.CoroutineExceptionHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

@Composable
fun ScreenHost() {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        var isDialogVisible by remember { mutableStateOf(false) }
        CompositionLocalProvider(
            LocalFontFamilyResolver provides createFontFamilyResolver(LocalContext.current, handler)
        ) {
            Text(text = "press me damn it",
                modifier = Modifier.clickable {
                isDialogVisible = !isDialogVisible
            })
        }
        if (isDialogVisible) {
            PickTimeDialog(stringResource(R.string.set_earliest_time),
                onDismissRequest = { isDialogVisible = false },
                onAcceptRequest = fun (hour: Int, minute: Int) {
                    println("$hour:$minute")
                    isDialogVisible = false
                }
            )
        }
    }
}

val handler = CoroutineExceptionHandler { _, throwable ->
    // process the Throwable
    Log.e("@@@  ", "There has been an issue: ", throwable)
}

@Preview(showSystemUi = true, name = "PIXEL 4", device = Devices.PIXEL_4)
//@Preview(showSystemUi = true, name = "NEXUS 5", device = Devices.NEXUS_5)
//@Preview(name = "NEXUS_7", device = Devices.NEXUS_7)
@Composable
fun ScreenPreview() {
    MindfullnessAlarmTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenHost()
        }
    }
}