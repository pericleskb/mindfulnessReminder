package com.cherryblossom.mindfullnessalarm.ui.composables.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cherryblossom.mindfullnessalarm.R

@Composable
fun OnOffIconButton(
    isEnabled: Boolean = false,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier) {
    val color = animateColorAsState(if (isEnabled) colorResource(R.color.enabled) else colorResource(R.color.disabled),
        label = "icon color"
    )
    IconButton(
        onClick = {
            onClick(!isEnabled)
        },
        content = {
            Icon(
                painter = painterResource(R.drawable.on_icon),
                contentDescription = stringResource(R.string.enable_description),
                tint = color.value,
                modifier = Modifier.fillMaxSize()
            )
        },
        modifier = modifier
    )
}