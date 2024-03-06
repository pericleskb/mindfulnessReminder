package com.cherryblossom.mindfullnessalarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.ui.composables.buttons.OnOffIconButton
import com.cherryblossom.mindfullnessalarm.ui.composables.dialogs.PickTimeDialog
import com.cherryblossom.mindfullnessalarm.ui.composables.text.OutlinedBoldEndAlignedTextField
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme


@Composable
fun ScreenHost(viewModel: MainViewModel = viewModel(), modifier: Modifier = Modifier) {
    val mainUiState by viewModel.uiState.collectAsState()
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight(0.7f)
        ) {
            TextFields(
                startTime = mainUiState.startTime,
                endTime = mainUiState.endTime,
                numOfReminders = mainUiState.numberOfReminders,
                startTimeChanged = { hour: Int, minute: Int ->
                    viewModel.startTimeChanged(
                        hour,
                        minute
                    )
                },
                endTimeChanged = { hour: Int, minute: Int ->
                    viewModel.endTimeChanged(
                        hour,
                        minute
                    )
                },
                numOfRemindersChanged = {viewModel.numberOfRemindersChanged(it)}
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            OnOffIconButton(
                isEnabled = mainUiState.isEnabled,
                onClick = { viewModel.enabled(it) },
                Modifier.fillMaxSize(0.6f).aspectRatio(1f, matchHeightConstraintsFirst = true))
        }
    }
}

@Composable
fun TextFields(
    startTime: TimeOfDay,
    endTime: TimeOfDay,
    numOfReminders: String,
    startTimeChanged: (Int, Int) -> Unit,
    endTimeChanged: (Int, Int) -> Unit,
    numOfRemindersChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    var startTimeDialogVisible by remember { mutableStateOf(false) }
    var endTimeDialogVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    OutlinedBoldEndAlignedTextField(
        text = startTime.toString(),
        label = stringResource(R.string.set_earliest_time),
        onClick = {
            startTimeDialogVisible = !startTimeDialogVisible
            focusManager.clearFocus()
        },
        enabled = false,
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    OutlinedBoldEndAlignedTextField(
        text = endTime.toString(),
        label = stringResource(R.string.set_latest_time),
        onClick = {
            endTimeDialogVisible = !endTimeDialogVisible
            focusManager.clearFocus()
        },
        enabled = false,
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    OutlinedBoldEndAlignedTextField(
        text = numOfReminders,
        label = stringResource(R.string.choose_number_of_reminders),
        onClick = {
            endTimeDialogVisible = !endTimeDialogVisible
        },
        onValueChange = fun (value: String) { numOfRemindersChanged(value) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
    )
    if (startTimeDialogVisible) {
        PickTimeDialog(
            stringResource(R.string.set_earliest_time),
            onDismissRequest = { startTimeDialogVisible = false },
            onAcceptRequest = fun (hour: Int, minute: Int) {
                startTimeChanged(hour, minute)
                startTimeDialogVisible = false
            },
            startTime
        )
    }
    if (endTimeDialogVisible) {
        PickTimeDialog(
            stringResource(R.string.set_latest_time),
            onDismissRequest = { endTimeDialogVisible = false },
            onAcceptRequest = fun (hour: Int, minute: Int) {
                endTimeChanged(hour, minute)
                endTimeDialogVisible = false
            },
            endTime
        )
    }
}

//@Preview(showSystemUi = true, name = "PIXEL 4", device = Devices.PIXEL_4)
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
//            ScreenHost()
        }
    }
}