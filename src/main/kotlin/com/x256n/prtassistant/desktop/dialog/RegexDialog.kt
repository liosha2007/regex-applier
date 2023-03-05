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
import java.awt.Dimension

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun RegexDialog(
    dialogVisible: Boolean,
    ruleName: String = "",
    regex: String = "",
    replacement: String = "",
    exampleSource: String = "",
    isCaseInsensitive: Boolean = false,
    isDotAll: Boolean = false,
    isMultiline: Boolean = false,
    onCancel: () -> Unit,
    onSave: (
        ruleName: String,
        regex: String,
        replacement: String,
        exampleSource: String,
        isCaseInsensitive: Boolean,
        isDotAll: Boolean,
        isMultiline: Boolean
    ) -> Unit,
    onRegexChanged: (
        regex: String,
        isCaseInsensitive: Boolean,
        isDotAll: Boolean,
        isMultiline: Boolean
    ) -> Unit
) {
    val ruleNameValue = remember { mutableStateOf(ruleName) }
    val regexValue = remember { mutableStateOf(regex) }
    val replacementValue = remember { mutableStateOf(replacement) }
    val exampleSourceValue = remember { mutableStateOf(exampleSource) }
    val isCaseInsensitiveValue = remember { mutableStateOf(isCaseInsensitive) }
    val isDotAllValue = remember { mutableStateOf(isDotAll) }
    val isMultilineValue = remember { mutableStateOf(isMultiline) }

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
                    text = ruleNameValue.value
                ) {
                    ruleNameValue.value = it
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
                    text = regexValue.value,
                    singleLine = false,
                    maxLines = 3
                ) {
                    regexValue.value = it
                    regexColor.value = Color.Black
                    onRegexChanged(
                        regexValue.value,
                        isCaseInsensitiveValue.value,
                        isDotAllValue.value,
                        isMultilineValue.value,
                    )
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
                    text = replacementValue.value,
                    singleLine = false,
                    maxLines = 3
                ) {
                    replacementValue.value = it
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
                    text = exampleSourceValue.value,
                    singleLine = false,
                    maxLines = 3
                ) {
                    exampleSourceValue.value = it
                }

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Case insensitive",
                    isChecked = isCaseInsensitiveValue.value,
                    onCheckedChange = {
                        isCaseInsensitiveValue.value = it
                        onRegexChanged(
                            regexValue.value,
                            isCaseInsensitiveValue.value,
                            isDotAllValue.value,
                            isMultilineValue.value,
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Dot all",
                    isChecked = isDotAllValue.value,
                    onCheckedChange = {
                        isDotAllValue.value = it
                        onRegexChanged(
                            regexValue.value,
                            isCaseInsensitiveValue.value,
                            isDotAllValue.value,
                            isMultilineValue.value,
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(5.dp))

                WinCheckbox(
                    text = "Multiline",
                    isChecked = isMultilineValue.value,
                    onCheckedChange = {
                        isMultilineValue.value = it
                        onRegexChanged(
                            regexValue.value,
                            isCaseInsensitiveValue.value,
                            isDotAllValue.value,
                            isMultilineValue.value,
                        )
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
                        if (ruleNameValue.value.isBlank()) {
                            ruleNameColor.value = Color.Red
                        } else if (regexValue.value.isBlank()) {
                            regexColor.value = Color.Red
                        } else {
                            onSave(
                                ruleNameValue.value,
                                regexValue.value,
                                replacementValue.value,
                                exampleSourceValue.value,
                                isCaseInsensitiveValue.value,
                                isDotAllValue.value,
                                isMultilineValue.value
                            )
                        }
                    }
                )
            }
        }
    }
}