package com.cherryblossom.mindfullnessalarm.ui

import android.content.res.Resources.Theme
import android.os.Build
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.models.TimeOfDay
import com.cherryblossom.mindfullnessalarm.ui.composables.dialogs.PickTimeDialog
import com.cherryblossom.mindfullnessalarm.ui.composables.text.AdjustableBorderOutlinedTextField
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme
import com.cherryblossom.mindfullnessalarm.ui.theme.Montserrat
import com.cherryblossom.mindfullnessalarm.ui.theme.Typography

@Composable
fun ScreenHost(viewModel: MainViewModel = viewModel(),
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        DescriptionText(modifier = Modifier.fillMaxWidth()
            .align(Alignment.Start)
            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(8.dp))//todo add brush
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
    AdjustableBorderOutlinedTextField(
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
    numOfReminders: String,
    numOfRemindersChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var numberPickerVisible by remember { mutableStateOf(false) }
    AdjustableBorderOutlinedTextField(
        text = numOfReminders,
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

@Composable
fun NumberPickerDialog(
    numOfReminders: String,
    visibilityChange: () -> Unit,
    numOfRemindersChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedValue by remember { mutableStateOf(numOfReminders) }
    Dialog(onDismissRequest = visibilityChange) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(R.string.choose_number_of_reminders),
                    textAlign = TextAlign.Start,
                    style = Typography.labelLarge,
                    fontFamily = Montserrat,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                NumberPickerComposable(
                    preselectedValue = numOfReminders,
                    updateValue = { selectedValue = it },
                    modifier = Modifier.padding(vertical = 24.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            visibilityChange()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel), fontFamily = Montserrat)
                    }
                    TextButton(
                        onClick = {
                            visibilityChange()
                            numOfRemindersChanged(selectedValue)
                        }
                    ) {
                        Text(text = stringResource(R.string.ok), fontFamily = Montserrat)
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPickerComposable(
    preselectedValue: String,
    updateValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                minValue = 1
                maxValue = 10
                this.value = preselectedValue.toInt()
                wrapSelectorWheel = false
                displayedValues = Array<String>(10){"${it+1}."}
                setOnValueChangedListener { _, oldValue, newValue ->
                    updateValue(newValue.toString())
                }
            }
        },
        update = {
        }
    )
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