package com.cherryblossom.mindfullnessalarm.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.data.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.ui.composables.dialogs.NumberPickerDialog
import com.cherryblossom.mindfullnessalarm.ui.composables.dialogs.PickTimeDialog
import com.cherryblossom.mindfullnessalarm.ui.composables.text.OutlinedTextFieldWithBorder
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme
import com.cherryblossom.mindfullnessalarm.ui.theme.Montserrat

@Composable
fun ScreenHost(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TestSwitch(viewModel)
        TopOfScreen(viewModel)
        Spacer(modifier = Modifier.weight(1f))
        BottomButton(viewModel)
    }
}

//TODO enable only on debug builds?
@Composable
fun TestSwitch(viewModel: MainViewModel) {
    var logFileChecked by remember { mutableStateOf(false) }
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType = "text/plain")) {
        result.value = it
    }
    result.value?.let { uri ->
        val contentResolver = LocalContext.current.applicationContext?.contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver?.takePersistableUriPermission(uri, takeFlags)
        viewModel.saveLogFile(uri);
    }
    Switch(checked = logFileChecked,
        onCheckedChange = { logFileChecked = !logFileChecked
            if (logFileChecked) {
                launcher.launch("mindfulllogs.txt")
            }}
    )
}

@Composable
fun TopOfScreen(viewModel: MainViewModel,
                modifier: Modifier = Modifier) {
    val mainUiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        DescriptionText(modifier = Modifier.fillMaxWidth()
            .align(Alignment.Start)
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))//todo add brush
            .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.guidelines),
            fontFamily = Montserrat,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        TimeTextField(
            time = mainUiState.startTime,
            timeChanged = {hour: Int, minute: Int -> viewModel.startTimeChanged(hour, minute)},
            stringResource(R.string.set_earliest_time),
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(32.dp))
        TimeTextField(
            time = mainUiState.endTime,
            timeChanged = {hour: Int, minute: Int -> viewModel.endTimeChanged(hour, minute )},
            stringResource(R.string.set_latest_time),
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(32.dp))
        NumberOfAlarmsTextField(
            mainUiState.numberOfReminders,
            numOfRemindersChanged = {viewModel.numberOfRemindersChanged(it)}
        )
    }
}

@Composable
fun BottomButton(viewModel: MainViewModel,
                modifier: Modifier = Modifier) {
    val mainUiState by viewModel.uiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp)
            .fillMaxHeight()
    ) {
        //Update reminders button
        AnimatedVisibility(mainUiState.isEnabled && mainUiState.preferencesChanged) {
            TextButton(
                onClick = {viewModel.updateReminders()},
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth()
                    .border(3.dp,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        RoundedCornerShape(8.dp))
                    .height(64.dp)
            ) {
                Text(
                    text = stringResource(R.string.update_reminders),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 18.sp,
                    fontFamily = Montserrat
                )
            }
        }
        //Enable disable feature button
        TextButton(
            onClick = { viewModel.onOffChanged() },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.inversePrimary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .border(3.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    RoundedCornerShape(8.dp))
                .height(64.dp)
        ) {
            Text(
                text = if (mainUiState.isEnabled) stringResource(R.string.turn_reminders_off) else
                    stringResource(R.string.turn_reminders_on),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                fontFamily = Montserrat
            )
        }
    }
}

@Composable
fun DescriptionText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_description),
        style = TextStyle(
            fontFamily = Montserrat,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
    )
}

@Composable
fun TimeTextField(
    time: TimeOfDay,
    timeChanged: (Int, Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var dialogVisible by remember { mutableStateOf(false) }
    OutlinedTextFieldWithBorder(
        text = time.toString(),
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth(),
        label = label,
        onClick = {
            dialogVisible = !dialogVisible
            focusManager.clearFocus()
        },
        enabled = false,
        readOnly = true
    )
    if (dialogVisible) {
        PickTimeDialog(
            label,
            onDismissRequest = { dialogVisible = false },
            onAcceptRequest = fun (hour: Int, minute: Int) {
                timeChanged(hour, minute)
                dialogVisible = false
            },
            time
        )
    }
}

@Composable
fun NumberOfAlarmsTextField(
    numOfReminders: Int,
    numOfRemindersChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var numberPickerVisible by remember { mutableStateOf(false) }
    OutlinedTextFieldWithBorder(
        text = numOfReminders.toString(),
        label = stringResource(R.string.choose_number_of_reminders),
        enabled = false,
        readOnly = true,
        onClick = {
            numberPickerVisible = !numberPickerVisible
        },
        onValueChange = { },
        modifier = modifier
            .fillMaxWidth()
    )
    if (numberPickerVisible) {
        NumberPickerDialog(
            numOfReminders,
            visibilityChange = { numberPickerVisible = !numberPickerVisible },
            numOfRemindersChanged = { numOfRemindersChanged(it) }
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