package com.cherryblossom.mindfullnessalarm.ui.composables.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cherryblossom.mindfullnessalarm.R
import com.cherryblossom.mindfullnessalarm.ui.theme.MindfullnessAlarmTheme
import com.cherryblossom.mindfullnessalarm.ui.theme.Montserrat

/*
    Based solution on this post https://stackoverflow.com/a/76804660/4880400
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextFieldWithBorder(
    text: String = "",
    label: String = "",
    onClick: () -> Unit,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = TextFieldValue(
            text = text,
            selection = TextRange(text.length)
        ),
        singleLine = true,
        readOnly = readOnly,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontFamily = Montserrat,
            fontWeight = FontWeight.W500,
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.White),
        onValueChange = { onValueChange(it.text) },
        enabled = enabled,
        keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        modifier = if (label.isNotEmpty()) {
            modifier
                // Merge semantics at the beginning of the modifier chain to ensure padding is
                // considered part of the text field.
                .semantics(mergeDescendants = true) {}
                .clip(RoundedCornerShape(42.dp, 42.dp, 39.dp, 39.dp))
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
            value = text,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            isError = isError,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            label = {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontFamily = Montserrat
                )
            },
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled = true,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.inversePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    focusedBorderThickness = 3.dp,
                    unfocusedBorderThickness = 3.dp
                )
            }
        )
    }
}

@Preview
@Composable
fun editText() {
    MindfullnessAlarmTheme {
        OutlinedTextFieldWithBorder(
            text = "09:00",
            label = stringResource(R.string.set_latest_time),
            onClick = {},
            modifier = Modifier
        )
    }
}