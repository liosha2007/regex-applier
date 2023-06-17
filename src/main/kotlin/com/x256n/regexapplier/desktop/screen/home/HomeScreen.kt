@file:OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)

package com.x256n.regexapplier.desktop.screen.home

import WinButton
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberDialogState
import com.x256n.regexapplier.desktop.component.WinCheckbox
import com.x256n.regexapplier.desktop.dialog.RegexDialog
import com.x256n.regexapplier.desktop.dialog.TooltipDialog
import com.x256n.regexapplier.desktop.model.RegexModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    windowState: WindowState,
    viewModel: HomeViewModel
) {
    val state by viewModel.state

    val showRegexDialog = remember { mutableStateOf(false) }
    val showRegexDialogRegexModel = remember { mutableStateOf(RegexModel.Empty) }
    val dialogState = remember { DialogState(width = 360.dp, height = 500.dp) }

    if (!state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            rememberSaveable(state.errorMessage) {
                if (state.errorMessage != null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(5000)
                        viewModel.onEvent(HomeEvent.ResetError)
                    }
                }
            }
            state.errorMessage?.let { message ->
                Text(
                    color = Color.Red,
                    text = message
                )
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

                    viewModel.onEvent(HomeEvent.SaveRegexClicked(regexModel))
                }, onRegexChanged = { regexModel ->
                    viewModel.onEvent(
                        HomeEvent.RegexChanged(regexModel)
                    )
                })
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxSize(),
                        value = state.sourceText,
                        placeholder = {
                            Text("Source text")
                        },
                        onValueChange = {
                            viewModel.onEvent(HomeEvent.SourceChanged(it))
                        }
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    TextField(modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged {
                            if (it.hasFocus) {
                                viewModel.onEvent(HomeEvent.ResultFocused)
                            }
                        },
                        value = state.resultText,
                        placeholder = {
                            Text("Result text")
                        },
                        readOnly = false,
                        onValueChange = {})
                }
                Spacer(modifier = Modifier.size(4.dp))
                val expanded = remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(modifier = Modifier
                        .width(8.dp)
                        .height(50.dp), onClick = {
                        expanded.value = !expanded.value
                    }) {}
                }
                Column(
                    modifier = Modifier
                        .width(if (expanded.value) 350.dp else 220.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
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
                                            viewModel.onEvent(HomeEvent.DeleteConfirmed(item))
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
                                                viewModel.onEvent(HomeEvent.EditRegexClicked(item))

                                                showRegexDialogRegexModel.value = item.copy()

                                                showRegexDialog.value = true
                                            },
                                            onClick = {
                                                viewModel.onEvent(HomeEvent.RegexSelected(item, index))
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
                                                x = windowState.position.x + offset.x.dp - tooltipState.size.width + 20.dp,
                                                y = windowState.position.y + offset.y.dp + 55.dp
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
                                            viewModel.onEvent(HomeEvent.EnabledClicked(item))
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
                                                                        viewModel.onEvent(
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
                                                                        viewModel.onEvent(
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
                                                    .clickable { viewModel.onEvent(HomeEvent.DeleteClicked(item)) }
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
                                viewModel.onEvent(HomeEvent.RegexDialogShown)
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