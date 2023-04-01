package com.x256n.regexapplier.desktop.dialog

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.x256n.lthwords.desktop.theme.spaces
import com.x256n.regexapplier.desktop.common.createPattern
import com.x256n.regexapplier.desktop.component.WinCheckbox
import com.x256n.regexapplier.desktop.model.RegexModel
import java.awt.Dimension
import java.util.regex.PatternSyntaxException

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun RegexDialog(
    dialogVisible: MutableState<Boolean>,
    regexModel: MutableState<RegexModel> = mutableStateOf(RegexModel.Empty),
    onCancel: () -> Unit,
    onSave: (
        regexModel: RegexModel
    ) -> Unit,
    onRegexChanged: (regexModel: RegexModel) -> Unit
) {

    val ruleNameColor = remember { mutableStateOf(Color.Black) }
    val regexColor = remember { mutableStateOf(Color.Black) }
    val sampleResult = remember { mutableStateOf(regexModel.value.exampleSource) }
    val isError = remember { mutableStateOf(false) }

    rememberSaveable(dialogVisible.value) {
        updateSampleResult(regexModel, sampleResult, isError)
    }

    Dialog(
        undecorated = false,
        resizable = true,
        visible = dialogVisible.value,
        state = DialogState(width = 360.dp, height = 500.dp),
        onCloseRequest = {
            onCancel()
        }
    ) {
        this.window.minimumSize = Dimension(240, 410)

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                        if (it.isShiftPressed) {
                            focusManager.moveFocus(FocusDirection.Previous)
                        } else {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        true
                    } else {
                        false
                    }
                }
                .padding(MaterialTheme.spaces.small)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Rule name",
                    color = ruleNameColor.value
                )
                WinTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = regexModel.value.name
                ) {
                    regexModel.value = regexModel.value.copy(name = it)
                    ruleNameColor.value = Color.Black
                }

                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Regex",
                    color = regexColor.value
                )
                WinTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = regexModel.value.regex,
                    singleLine = false,
                    maxLines = 3,
                    isError = isError.value
                ) {
                    regexModel.value = regexModel.value.copy(regex = it)
                    regexColor.value = Color.Black
                    onRegexChanged(regexModel.value)

                    updateSampleResult(regexModel, sampleResult, isError)
                }

                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Replacement"
                )
                WinTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = regexModel.value.replacement,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexModel.value = regexModel.value.copy(replacement = it)

                    updateSampleResult(regexModel, sampleResult, isError)
                }

                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Source text (for example)"
                )
                WinTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = regexModel.value.exampleSource,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexModel.value = regexModel.value.copy(exampleSource = it)

                    updateSampleResult(regexModel, sampleResult, isError)
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    WinCheckbox(
                        text = "Case insensitive",
                        isChecked = regexModel.value.isCaseInsensitive,
                        onCheckedChange = {
                            regexModel.value = regexModel.value.copy(isCaseInsensitive = it)
                            onRegexChanged(regexModel.value)

                            updateSampleResult(regexModel, sampleResult, isError)
                        }
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    WinCheckbox(
                        text = "Dot all",
                        isChecked = regexModel.value.isDotAll,
                        onCheckedChange = {
                            regexModel.value = regexModel.value.copy(isDotAll = it)
                            onRegexChanged(regexModel.value)

                            updateSampleResult(regexModel, sampleResult, isError)
                        }
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    WinCheckbox(
                        text = "Multiline",
                        isChecked = regexModel.value.isMultiline,
                        onCheckedChange = {
                            regexModel.value = regexModel.value.copy(isMultiline = it)
                            onRegexChanged(regexModel.value)

                            updateSampleResult(regexModel, sampleResult, isError)
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier,
                        text = sampleResult.value
                    )
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                WinButton(
                    modifier = Modifier
                        .weight(1f),
                    text = "Cancel",
                    onClick = {
                        onCancel()
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

                WinButton(
                    modifier = Modifier
                        .weight(1f),
                    text = "Save",
                    onClick = {
                        if (regexModel.value.name.isBlank()) {
                            ruleNameColor.value = Color.Red
                        } else if (regexModel.value.regex.isBlank()) {
                            regexColor.value = Color.Red
                        } else if (!isError.value) {
                            onSave(regexModel.value)
                        }
                    }
                )
            }
        }
    }
}

private fun updateSampleResult(
    regexModel: MutableState<RegexModel>,
    sampleResult: MutableState<String>,
    isError: MutableState<Boolean>
) {
    try {
        val pattern = createPattern(
            regexModel.value.regex,
            regexModel.value.isCaseInsensitive,
            regexModel.value.isDotAll,
            regexModel.value.isMultiline
        )
        sampleResult.value = pattern.matcher(regexModel.value.exampleSource).replaceAll(regexModel.value.replacement)
        isError.value = false
    } catch (e: PatternSyntaxException) {
        isError.value = true
    } catch (e: Exception) {
        e.printStackTrace()
        isError.value = true
    }
}