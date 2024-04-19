package com.cherryblossom.mindfulnessreminder.ui.composables.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cherryblossom.mindfulnessreminder.R
import com.cherryblossom.mindfulnessreminder.data.models.TimeOfDay
import com.cherryblossom.mindfulnessreminder.ui.theme.mindfulnessreminderTheme
import com.cherryblossom.mindfulnessreminder.ui.theme.Montserrat
import com.cherryblossom.mindfulnessreminder.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickTimeDialog(label: String,
                   onDismissRequest: () -> Unit,
                   onAcceptRequest: (hour: Int, minute: Int) -> Unit,
                   timeOfDay: TimeOfDay
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            ) {
                Text(text = label,
                    textAlign = TextAlign.Start,
                    style = Typography.labelLarge,
                    fontFamily = Montserrat,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                )
                val state = rememberTimePickerState(
                    initialHour = timeOfDay.hour,
                    initialMinute = timeOfDay.minute,
                    is24Hour = true
                )
                TimeChooser(timePickerState = state, modifier = Modifier.padding(bottom = 24.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismissRequest() }
                    ) {
                        Text(text = stringResource(R.string.cancel_timer_dialog), fontFamily = Montserrat)
                    }
                    TextButton(
                        onClick = { onAcceptRequest(state.hour, state.minute) }
                    ) {
                        Text(stringResource(R.string.accept_timer_dialog), fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeChooser(timePickerState: TimePickerState, modifier: Modifier = Modifier) {
    TimePicker(state = timePickerState, modifier = modifier.fillMaxWidth())
}

@Preview(apiLevel = 32)
@Preview(apiLevel = 32, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun dialogPreview() {
    mindfulnessreminderTheme {
        PickTimeDialog(stringResource(R.string.set_latest_time),
            onDismissRequest = fun () {},
            onAcceptRequest = fun (a: Int, b: Int) {},
            timeOfDay = TimeOfDay(10, 0)
        )
    }
}