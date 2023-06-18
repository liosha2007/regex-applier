@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.x256n.regexapplier.desktop.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import com.x256n.regexapplier.desktop.navigation.Destination
import com.x256n.regexapplier.desktop.screen.home.component.RegexList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.awt.Cursor
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.JRootPane

@Composable
fun HomeScreen(
    action: Destination.Home.Action = Destination.Home.Action.None,
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit = {},
    rootPanel: JRootPane? = null,
    windowPositionX: Dp = 0.dp,
    windowPositionY: Dp = 0.dp,
    navigate: (Destination) -> Unit = {},
) {
    val log = remember { LoggerFactory.getLogger("HomeScreen") }
    val coroutineScope = rememberCoroutineScope()
    val spacerSize = 4.dp
    val minRegexPanelWidth = 128.dp
    var sourcePanelWidth by remember { mutableStateOf(380.dp) }
    var resultPanelSize by remember { mutableStateOf(IntSize.Zero) }
    var resultPanelWidth by remember { mutableStateOf(380.dp) }
    var regexPanelSize by remember { mutableStateOf(IntSize.Zero) }

    rememberSaveable(action) {
        if (action is Destination.Home.Action.RegexChange) {
            sendEvent(HomeEvent.SaveRegexClicked(action.regex))
        }
    }

    if (!state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            rememberSaveable(state.errorMessage) {
                if (state.errorMessage != null) {
                    coroutineScope.launch(Dispatchers.Default) {
                        delay(5000)
                        sendEvent(HomeEvent.ResetError)
                    }
                }
            }
            state.errorMessage?.let { message ->
                Text(
                    color = Color.Red,
                    text = message
                )
            }
            if (state.isShowFileChooserDialog) {
                val fileChooser = JFileChooser(System.getProperty("user.home") ?: "/").apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    dialogTitle = "Select a file"
                    approveButtonText = "Select"
//            approveButtonToolTipText = "Select current directory as save destination"
                }
                fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                rootPanel?.let {
                    fileChooser.showOpenDialog(it)
                }
                fileChooser.selectedFile?.let {
                    log.debug("result = ${it.path}")
                    sendEvent(HomeEvent.OpenFile(action = HomeEvent.OpenFile.Action.ProcessSelectedFile(Path.of(it.path))))
                }
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(sourcePanelWidth),
                ) {
                    TextField(
                        modifier = Modifier
                            .background(Color(0.95f, 0.95f, 0.95f))
                            .fillMaxSize(),
                        value = state.sourceText,
                        placeholder = {
                            Text("Source text")
                        },
                        onValueChange = {
                            sendEvent(HomeEvent.SourceChanged(it))
                        }
                    )
                }

                Spacer(modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxHeight()
                    .width(spacerSize)
                    .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            matcher = PointerMatcher.Primary
                        ) {
                            if ((it.x > 0 && resultPanelSize.width.dp - it.x.dp > spacerSize) || (it.x < 0 && sourcePanelWidth + it.x.dp > spacerSize)) {
                                sourcePanelWidth += it.x.dp
                                resultPanelWidth -= it.x.dp
                            }
                        }
                    })
                Column(
                    modifier = Modifier
                        .width(resultPanelWidth)
                        .onSizeChanged {
                            resultPanelSize = it
                        }
                ) {
                    TextField(modifier = Modifier
                        .background(Color(0.95f, 0.95f, 0.95f))
                        .fillMaxSize()
                        .onFocusChanged {
                            if (it.hasFocus) {
                                sendEvent(HomeEvent.ResultFocused)
                            }
                        },
                        value = state.resultText,
                        placeholder = {
                            Text("Result text")
                        },
                        readOnly = false,
                        onValueChange = {})
                }

                Spacer(modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxHeight()
                    .width(spacerSize)
                    .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            matcher = PointerMatcher.Primary
                        ) {
                            if ((it.x > 0 && regexPanelSize.width.dp - it.x.dp > minRegexPanelWidth) || (it.x < 0 && regexPanelSize.width.dp - it.x.dp > minRegexPanelWidth)) {
                                resultPanelWidth += it.x.dp
                            }
                        }
                    })
                RegexList(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged {
                            regexPanelSize = it
                        },
                    state = state,
                    sendEvent = sendEvent,
                    onEditRegex = {
                        navigate(
                            Destination.RegexDialog(
                                action = Destination.RegexDialog.Action.Edit(state.selectedItem)
                            )
                        )
                    },
                    windowPositionX = windowPositionX,
                    windowPositionY = windowPositionY,
                    onCreateRegex = {
                        navigate(Destination.RegexDialog(action = Destination.RegexDialog.Action.Create))
                    },
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}