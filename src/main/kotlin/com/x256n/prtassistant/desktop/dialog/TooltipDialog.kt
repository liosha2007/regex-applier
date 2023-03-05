package com.x256n.prtassistant.desktop.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import java.awt.Dimension
import java.util.regex.Pattern

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun TooltipDialog(
    dialogVisible: Boolean,
    state: DialogState,
    regex: String,
    replacement: String,
    exampleSource: String,
    isCaseInsensitive: Boolean,
    isDotAll: Boolean,
    isMultiline: Boolean,
    onCloseRequest: () -> Unit
) {
    Dialog(
        undecorated = false,
        resizable = false,
        focusable = false,
        state = state,
        title = "Regex example",
        visible = dialogVisible,
        onCloseRequest = onCloseRequest
    ) {
        this.window.minimumSize = Dimension(360, 240)
        this.window.size = this.window.minimumSize
        this.window.isModal = false

        val replaceResult = rememberSaveable(exampleSource) {
            var mode = 0
            if (isCaseInsensitive) mode = mode or Pattern.CASE_INSENSITIVE
            if (isDotAll) mode = mode or Pattern.DOTALL
            if (isMultiline) mode = mode or Pattern.MULTILINE

            val pattern = Pattern.compile(regex, mode)
            pattern.matcher(exampleSource).replaceAll(replacement)
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
                    .weight(1f),
                text = exampleSource,
                fontSize = 10.sp
            )

            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
                    .weight(1f),
                text = replaceResult,
                fontSize = 10.sp
            )
        }
    }
}