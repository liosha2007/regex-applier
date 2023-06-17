@file:OptIn(ExperimentalComposeUiApi::class)

package com.x256n.regexapplier.desktop.dialog.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.x256n.lthwords.desktop.theme.spaces
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory

@Composable
fun AboutDialog(
    onCancel: () -> Unit
) {
    val LOG = remember { LoggerFactory.getLogger("AboutDialog") }
    val viewModel by KoinJavaComponent.inject<AboutViewModel>(AboutViewModel::class.java)
    val state by viewModel.state


    LaunchedEffect(Unit) {
        viewModel.onEvent(AboutEvent.AboutDisplayed)
    }
    Dialog(
        title = "About",
        undecorated = false,
        resizable = false,
        visible = true,
        state = DialogState(width = 320.dp, height = 320.dp),
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

        if (!state.isLoading) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spaces.small)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Image(
                    modifier = Modifier
                        .size(128.dp, 128.dp),
                    painter = painterResource("icon.ico"),
                    contentDescription = "icon"
                )

                Text(
                    modifier = Modifier
                        .padding(MaterialTheme.spaces.medium),
                    text = "Made by liosha\nhttps://github.com/liosha2007",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}