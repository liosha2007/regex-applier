package com.x256n.regexapplier.desktop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun WinCheckbox(
    text: String? = null,
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (isChecked: Boolean) -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            Checkbox(
                modifier = Modifier
                    .padding(2.dp),
                checked = isChecked,
                enabled = enabled,
                colors = CheckboxDefaults.colors(checkedColor = Color.DarkGray),
                onCheckedChange = onCheckedChange
            )
        }
        if (content == null) {
            text?.let {
                Text(
                    modifier = Modifier
                        .clickable {
                            onCheckedChange(!isChecked)
                        },
                    text = text
                )
            }
        } else {
            content()
        }
    }
}