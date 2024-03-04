package com.cherryblossom.mindfullnessalarm.composables.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme
import com.cherryblossom.mindfullnessalarm.ui.theme.Montserrat

/*
    Edited solution from this post https://stackoverflow.com/a/76804660/4880400
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTextField(
    time: String = "",
    label: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = time,
        singleLine = true,
        readOnly = true,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontFamily = Montserrat,
            fontWeight = FontWeight.W500,
            fontSize = 36.sp,
            textAlign = TextAlign.End
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.White),
        onValueChange = { },
        enabled = false,
        modifier = if (label.isNotEmpty()) {
            modifier
                // Merge semantics at the beginning of the modifier chain to ensure padding is
                // considered part of the text field.
                .semantics(mergeDescendants = true) {}
                .clip(RoundedCornerShape(39.dp))
                .padding(top = 12.dp, bottom = 10.dp, start = 4.dp, end = 4.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = MaterialTheme.colorScheme.secondary))
                {
                    onClick()
                }
            } else {
                modifier
            }
            .defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight
            )
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = time,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            label = {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            },
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(),
                    shape = RoundedCornerShape(16.dp),
                    focusedBorderThickness = 5.dp,
                    unfocusedBorderThickness = 5.dp
                )
            }
        )
    }
}

@Preview
@Composable
fun editText() {
    MindfullnessAlarmTheme {
        TimeTextField(
            time = "09:00",
            label = stringResource(R.string.set_latest_time),
            {},
            modifier = Modifier
        )
    }
}