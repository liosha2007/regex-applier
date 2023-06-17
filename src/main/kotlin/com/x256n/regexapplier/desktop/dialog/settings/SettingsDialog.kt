@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.x256n.regexapplier.desktop.dialog.settings

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.lthwords.desktop.theme.spaces
import com.x256n.regexapplier.desktop.component.WinCheckbox
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun SettingsDialog(
    onCancel: () -> Unit
) {
    val LOG = remember { LoggerFactory.getLogger("SettingsDialog") }
    val viewModel by KoinJavaComponent.inject<SettingsViewModel>(SettingsViewModel::class.java)
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(SettingsEvent.SettingsDisplayed)
    }
    Dialog(
        title = "Settings",
        undecorated = false,
        resizable = false,
        visible = true,
        state = DialogState(width = 380.dp, height = 240.dp),
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCancel()
            }
            return@Dialog true
        },
        onCloseRequest = {
            onCancel()
        }
    ) {

        Column(
            modifier = Modifier
                .padding(MaterialTheme.spaces.small)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spaces.medium)
            ) {
                WinCheckbox(
                    text = "Is debug mode",
                    isChecked = state.isDebugMode,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.IsDebugMode(it))
                    },
                    enabled = false
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.processTimeout.toString(),
                    title = "Process timeout (ms):",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.ProcessTimeout(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.large)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    WinButton(text = "Close", onClick = {
                        onCancel()
                    })
                }
            }
        }
    }
}