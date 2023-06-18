@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)

package com.x256n.regexapplier.desktop.screen.home.component

import WinButton
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.x256n.lthwords.desktop.theme.spaces
import com.x256n.regexapplier.desktop.component.WinCheckbox
import com.x256n.regexapplier.desktop.dialog.TooltipDialog
import com.x256n.regexapplier.desktop.model.RegexModel
import com.x256n.regexapplier.desktop.screen.home.HomeEvent
import com.x256n.regexapplier.desktop.screen.home.HomeState

@Composable
fun RegexList(
    modifier: Modifier = Modifier,
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit = {},
    onEditRegex: (RegexModel) -> Unit = {},
    onCreateRegex: () -> Unit = { },
    windowPositionX: Dp,
    windowPositionY: Dp,
) {
    Column(
        modifier = modifier
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
                                    onEditRegex(item)
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
                            isChecked = item.isEnabled,
                            onCheckedChange = {
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
                                                            sendEvent(HomeEvent.UpClicked(item))
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
                                                            sendEvent(HomeEvent.DownClicked(item))
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
                                        .clickable {
                                            sendEvent(HomeEvent.DeleteClicked(item))
                                        }
                                        .padding(2.dp),
                                    painter = painterResource("images/cross.png"), contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(start = MaterialTheme.spaces.extraSmall)) {
            WinButton(modifier = Modifier
                .weight(1f),
                onClick = {
                    onCreateRegex()
                }) {
                Text("CREATE RULE")
            }
        }
    }
}
