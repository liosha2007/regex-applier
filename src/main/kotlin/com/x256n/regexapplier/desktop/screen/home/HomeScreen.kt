@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.x256n.regexapplier.desktop.screen.home

import WinButton
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.x256n.regexapplier.desktop.component.WinCheckbox
import com.x256n.regexapplier.desktop.dialog.RegexDialog
import com.x256n.regexapplier.desktop.dialog.TooltipDialog
import com.x256n.regexapplier.desktop.model.RegexModel
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
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit = {},
    rootPanel: JRootPane? = null,
    windowPositionX: Dp = 0.dp,
    windowPositionY: Dp = 0.dp,
) {
    val log = remember { LoggerFactory.getLogger("HomeScreen") }
    val coroutineScope = rememberCoroutineScope()

    val showRegexDialog = remember { mutableStateOf(false) }
    val showRegexDialogRegexModel = remember { mutableStateOf(RegexModel.Empty) }
    val dialogState = remember { DialogState(width = 360.dp, height = 500.dp) }
    val spacerSize = 4.dp
    val minRegexPanelWidth = 128.dp
    var sourcePanelWidth by remember { mutableStateOf(380.dp) }
    var resultPanelSize by remember { mutableStateOf(IntSize.Zero) }
    var resultPanelWidth by remember { mutableStateOf(380.dp) }
    var regexPanelSize by remember { mutableStateOf(IntSize.Zero) }

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
            if (state.isShowChooseProjectDirectoryDialog) {
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
            RegexDialog(
                regexModel = showRegexDialogRegexModel,
                dialogVisible = showRegexDialog,
                state = dialogState,
                onCancel = {
                    showRegexDialog.value = false
                    showRegexDialogRegexModel.value = RegexModel.Empty
                }, onSave = { regexModel ->
                    showRegexDialog.value = false
                    showRegexDialogRegexModel.value = RegexModel.Empty

                    sendEvent(HomeEvent.SaveRegexClicked(regexModel))
                }, onRegexChanged = { regexModel ->
                    sendEvent(
                        HomeEvent.RegexChanged(regexModel)
                    )
                })
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged {
                            regexPanelSize = it
                        }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        itemsIndexed(state.storage.regexs.sortedBy { it.order }) { index, item ->
                            if (state.itemToDelete == item) {
                                Row(
                                    modifier = Modifier
                                        .height(36.dp)
                                        .padding(2.dp)
                                        .fillMaxWidth()
                                        .background(Color.Red.copy(alpha = 0.2f)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                            .weight(1f),
                                        text = item.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    WinButton(modifier = Modifier
                                        .padding(bottom = 2.dp),
                                        text = "Delete!",
                                        onClick = {
                                            sendEvent(HomeEvent.DeleteConfirmed(item))
                                        }
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .fillMaxWidth()
                                        .combinedClickable(enabled = true,
                                            onDoubleClick = {
                                                sendEvent(HomeEvent.EditRegexClicked(item))

                                                showRegexDialogRegexModel.value = item.copy()

                                                showRegexDialog.value = true
                                            },
                                            onClick = {
                                                sendEvent(HomeEvent.RegexSelected(item, index))
                                            })
                                        .background(if (state.selectedIndex == index) Color.LightGray else Color.Transparent),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isTooltipShown = remember { mutableStateOf(false) }
                                    val tooltipState = rememberDialogState(WindowPosition.Absolute(0.dp, 0.dp))
                                    TooltipDialog(
                                        dialogVisible = isTooltipShown.value && item.exampleSource.isNotBlank(),
                                        state = tooltipState,
                                        regex = item.regex,
                                        replacement = item.replacement,
                                        exampleSource = item.exampleSource,
                                        isCaseInsensitive = item.isCaseInsensitive,
                                        isDotAll = item.isDotAll,
                                        isMultiline = item.isMultiline,
                                        onCloseRequest = {
                                            isTooltipShown.value = false
                                        }
                                    )

                                    WinCheckbox(modifier = Modifier
                                        .onGloballyPositioned { coordinates ->
                                            val offset = coordinates.positionInWindow()

                                            tooltipState.position = WindowPosition(
                                                x = windowPositionX + offset.x.dp - tooltipState.size.width + 20.dp,
                                                y = windowPositionY + offset.y.dp + 55.dp
                                            )
//                                            println("Tooltip position = x: ${tooltipState.position.x}, y: ${tooltipState.position.y}")
                                        }
                                        .pointerMoveFilter(
                                            onEnter = {
                                                isTooltipShown.value = true
                                                false
                                            },
                                            onExit = {
                                                isTooltipShown.value = false
                                                false
                                            }),
                                        isChecked = item.isEnabled, onCheckedChange = {
                                            sendEvent(HomeEvent.EnabledClicked(item))
                                        })
                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        maxLines = 1,
                                        text = item.name,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        modifier = Modifier
                                            .padding(end = 3.dp), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (state.selectedIndex == index) {
                                            Column(
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .padding(horizontal = 3.dp)
                                            ) {
                                                if (state.storage.regexs.size > 1) {
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .padding(1.dp)
                                                    ) {
                                                        if (index > 0) {
                                                            Image(
                                                                modifier = Modifier
                                                                    .clickable {
                                                                        sendEvent(
                                                                            HomeEvent.UpClicked(
                                                                                item
                                                                            )
                                                                        )
                                                                    },
                                                                painter = painterResource("images/arrow-up.png"),
                                                                contentDescription = null
                                                            )

                                                        }
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .padding(1.dp)
                                                    ) {
                                                        if (index < state.storage.regexs.size - 1) {
                                                            Image(
                                                                modifier = Modifier
                                                                    .clickable {
                                                                        sendEvent(
                                                                            HomeEvent.DownClicked(
                                                                                item
                                                                            )
                                                                        )
                                                                    },
                                                                painter = painterResource("images/arrow-down.png"),
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Image(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable { sendEvent(HomeEvent.DeleteClicked(item)) }
                                                    .padding(2.dp),
                                                painter = painterResource("images/cross.png"), contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row {
                        WinButton(modifier = Modifier
                            .weight(1f),
                            onClick = {
                                sendEvent(HomeEvent.RegexDialogShown)
                                showRegexDialog.value = true
                            }) {
                            Text("CREATE RULE")
                        }
                    }
                }
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