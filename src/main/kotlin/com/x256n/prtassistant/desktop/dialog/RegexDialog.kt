package com.x256n.prtassistant.desktop.dialog

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.x256n.prtassistant.desktop.component.WinCheckbox
import com.x256n.prtassistant.desktop.model.RegexModel
import java.awt.Dimension

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun RegexDialog(
    dialogVisible: Boolean,
    regexModel: RegexModel = RegexModel.Empty,
    onCancel: () -> Unit,
    onSave: (
        regexModel: RegexModel
    ) -> Unit,
    onRegexChanged: (regexModel: RegexModel) -> Unit
) {
    val regexModelValue = remember { mutableStateOf(regexModel.copy()) }

    val ruleNameColor = remember { mutableStateOf(Color.Black) }
    val regexColor = remember { mutableStateOf(Color.Black) }

    Dialog(
        undecorated = false,
        resizable = true,
        visible = dialogVisible,
        state = DialogState(width = 240.dp, height = 400.dp),
        onCloseRequest = {
            onCancel()
        }
    ) {
        this.window.minimumSize = Dimension(240, 400)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
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
                    text = regexModelValue.value.name
                ) {
                    regexModelValue.value = regexModelValue.value.copy(name = it)
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
                    text = regexModelValue.value.regex,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexModelValue.value = regexModelValue.value.copy(regex = it)
                    regexColor.value = Color.Black
                    onRegexChanged(regexModelValue.value)
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
                    text = regexModelValue.value.replacement,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexModelValue.value = regexModelValue.value.copy(replacement = it)
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
                    text = regexModelValue.value.exampleSource,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexModelValue.value = regexModelValue.value.copy(exampleSource = it)
                }

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Case insensitive",
                    isChecked = regexModelValue.value.isCaseInsensitive,
                    onCheckedChange = {
                        regexModelValue.value = regexModelValue.value.copy(isCaseInsensitive = it)
                        onRegexChanged(regexModelValue.value)
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Dot all",
                    isChecked = regexModelValue.value.isDotAll,
                    onCheckedChange = {
                        regexModelValue.value = regexModelValue.value.copy(isDotAll = it)
                        onRegexChanged(regexModelValue.value)
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Multiline",
                    isChecked = regexModelValue.value.isMultiline,
                    onCheckedChange = {
                        regexModelValue.value = regexModelValue.value.copy(isMultiline = it)
                        onRegexChanged(regexModelValue.value)
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

            }
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
                        if (regexModelValue.value.name.isBlank()) {
                            ruleNameColor.value = Color.Red
                        } else if (regexModelValue.value.regex.isBlank()) {
                            regexColor.value = Color.Red
                        } else {
                            onSave(regexModelValue.value)
                        }
                    }
                )
            }
        }
    }
}