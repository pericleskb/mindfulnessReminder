package com.cherryblossom.mindfullnessalarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.composables.dialogs.PickTimeDialog
import com.cherryblossom.mindfullnessalarm.composables.text.TimeTextField
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme


@Composable
fun ScreenHost(viewModel: MainViewModel = viewModel()) {
    val mainUiState by viewModel.uiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(16.dp).fillMaxSize()
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        var startTimeDialogVisible by remember { mutableStateOf(false) }
        var endTimeDialogVisible by remember { mutableStateOf(false) }
        TimeTextField(
            time = mainUiState.startTime.toString(),
            label = stringResource(R.string.set_earliest_time),
            onClick = { startTimeDialogVisible = !startTimeDialogVisible },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        TimeTextField(
            time = mainUiState.endTime.toString(),
            label = stringResource(R.string.set_latest_time),
            onClick = { endTimeDialogVisible = !endTimeDialogVisible },
            modifier = Modifier
                .fillMaxWidth()
        )
        if (startTimeDialogVisible) {
            PickTimeDialog(
                stringResource(R.string.set_earliest_time),
                onDismissRequest = { startTimeDialogVisible = false },
                onAcceptRequest = fun (hour: Int, minute: Int) {
                    viewModel.startTimeChanged(hour, minute)
                    startTimeDialogVisible = false
                },
                mainUiState.startTime
            )
        }
        if (endTimeDialogVisible) {
            PickTimeDialog(
                stringResource(R.string.set_latest_time),
                onDismissRequest = { endTimeDialogVisible = false },
                onAcceptRequest = fun (hour: Int, minute: Int) {
                    viewModel.endTimeChanged(hour, minute)
                    endTimeDialogVisible = false
                },
                mainUiState.endTime
            )
        }
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