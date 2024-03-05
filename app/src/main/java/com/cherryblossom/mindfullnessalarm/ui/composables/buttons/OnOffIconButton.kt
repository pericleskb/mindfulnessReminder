package com.cherryblossom.mindfullnessalarm.ui.composables.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
fun OnOffIconButton() {
    var enabled by remember { mutableStateOf(false) }
    val color = animateColorAsState(if (enabled) colorResource(R.color.enabled) else colorResource(R.color.disabled),
        label = "icon color"
    )
    IconButton(
        onClick = {enabled = !enabled},
        content = {
            Icon(
                painter = painterResource(R.drawable.on_icon),
                contentDescription = stringResource(R.string.enable_description),
                modifier = Modifier.fillMaxSize(),
                tint = color.value
            )
        },
        modifier = Modifier.defaultMinSize(56.dp)
            .fillMaxSize(fraction = 0.3f)
    )
}