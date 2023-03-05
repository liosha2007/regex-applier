package com.x256n.prtassistant.desktop.screen.home

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.prtassistant.desktop.dialog.RegexDialog
import com.x256n.prtassistant.desktop.navigation.Destinations
import com.x256n.prtassistant.desktop.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigator: Navigator<Destinations.Home>) {
    val state by viewModel.state

    val showRegexDialog = remember {
        mutableStateOf(false)
    }

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
            RegexDialog(showRegexDialog.value, onCancel = {
                showRegexDialog.value = false
            }, onSave = { ruleName, regex, replacement, exampleSource, isCaseInsensitive, isDotAll, isMultiline ->
                showRegexDialog.value = false
                viewModel.onEvent(
                    HomeEvent.SaveRegexClicked(
                        ruleName,
                        regex,
                        replacement,
                        exampleSource,
                        isCaseInsensitive,
                        isDotAll,
                        isMultiline
                    )
                )
            }, onRegexChanged = { regex, isCaseInsensitive, isDotAll, isMultiline ->
                viewModel.onEvent(
                    HomeEvent.RegexChanged(
                        regex,
                        isCaseInsensitive,
                        isDotAll,
                        isMultiline
                    )
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
                    TextField(modifier = Modifier
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
                            Row(
                                modifier = Modifier
                                    .height(32.dp)
                                    .fillMaxWidth()
                                    .clickable { viewModel.onEvent(HomeEvent.RegexSelected(item, index)) }
                                    .background(if (state.selectedIndex == index) Color.LightGray else Color.Transparent),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = item.enabled, onCheckedChange = {
                                    viewModel.onEvent(HomeEvent.EnabledClicked(item))
                                })
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = item.name,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .border(border = BorderStroke(1.dp, Color.DarkGray))
                                            .clickable { viewModel.onEvent(HomeEvent.DeleteClicked(item)) }
                                            .size(30.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            modifier = Modifier,
                                            text = "D"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Row {
                        WinButton(modifier = Modifier
                            .weight(1f),
                            enabled = state.hasData,
                            onClick = {
                                viewModel.onEvent(HomeEvent.UpClicked)
                            }) {
                            Text("UP")
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        WinButton(modifier = Modifier
                            .weight(1f),
                            enabled = state.hasData,
                            onClick = {
                                viewModel.onEvent(HomeEvent.DownClicked)
                            }) {
                            Text("DOWN")
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        WinButton(modifier = Modifier
                            .weight(1f),
                            onClick = {
                                showRegexDialog.value = true
                            }) {
                            Text("NEW")
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