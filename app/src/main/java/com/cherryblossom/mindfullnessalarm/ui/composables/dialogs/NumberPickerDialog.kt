package com.cherryblossom.mindfullnessalarm.ui.composables.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chargemap.compose.numberpicker.NumberPicker
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.ui.theme.Montserrat
import com.cherryblossom.mindfullnessalarm.ui.theme.Typography

@Composable
fun NumberPickerDialog(
    numOfReminders: Int,
    visibilityChange: () -> Unit,
    numOfRemindersChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedValue by remember { mutableIntStateOf(numOfReminders) }
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
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 36.dp))
                NumberPicker(
                    value = selectedValue,
                    range =  1..10,
                    onValueChange = { selectedValue = it },
                    dividersColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textStyle = TextStyle(
                        fontFamily = Montserrat,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
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