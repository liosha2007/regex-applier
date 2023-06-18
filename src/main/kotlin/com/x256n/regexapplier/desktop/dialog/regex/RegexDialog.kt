@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class
)

package com.x256n.regexapplier.desktop.dialog.regex

import WinButton
import WinTextField
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.lthwords.desktop.theme.LTHWordsTheme
import com.x256n.lthwords.desktop.theme.spaces
import com.x256n.regexapplier.desktop.component.WinCheckbox
import com.x256n.regexapplier.desktop.model.RegexModel
import com.x256n.regexapplier.desktop.navigation.Destination
import org.slf4j.LoggerFactory
import java.awt.Dimension

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun RegexDialog(
    action: Destination.RegexDialog.Action,
    state: RegexState,
    dialogState: DialogState,
    sendEvent: (RegexEvent) -> Unit = {},
    onCancel: () -> Unit = {},
    onRegexChange: (RegexModel) -> Unit = {},
    onSave: (RegexModel) -> Unit = {},
) {
    val log = remember { LoggerFactory.getLogger("RegexDialog") }
    Dialog(
        title = "Create regex",
        undecorated = false,
        resizable = true,
        visible = true,
        state = dialogState,
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCancel()
                true
            } else false
        },
        onCloseRequest = {
            onCancel()
        }
    ) {
        this.window.minimumSize = Dimension(360, 480)
        DialogContent(
            state = state,
            regexModel = if (action is Destination.RegexDialog.Action.Edit) action.regex else null,
            sendEvent = sendEvent,
            onCancel = onCancel,
            onRegexChange = onRegexChange,
            onSave = onSave
        )
    }

}

@Composable
fun DialogContent(
    state: RegexState,
    regexModel: RegexModel? = null,
    sendEvent: (RegexEvent) -> Unit = {},
    onCancel: () -> Unit = {},
    onRegexChange: (RegexModel) -> Unit = {},
    onSave: (RegexModel) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf(regexModel?.name ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var regex by remember { mutableStateOf(regexModel?.regex ?: "\\d+(\\w+)\\d+") }
    var regexError by remember { mutableStateOf(false) }
    var replacement by remember { mutableStateOf(regexModel?.replacement ?: "--\$1--") }
    var exampleSource by remember { mutableStateOf(regexModel?.exampleSource ?: "sdf3344ref44") }
    var isCaseInsensitive by remember { mutableStateOf(regexModel?.isCaseInsensitive ?: false) }
    var isDotAll by remember { mutableStateOf(regexModel?.isDotAll ?: false) }
    var isMultiline by remember { mutableStateOf(regexModel?.isMultiline ?: false) }
    LaunchedEffect(true) {
        // Focus first field by default
        focusManager.moveFocus(FocusDirection.Next)
        if (regex.isNotEmpty()) {
            sendEvent(
                RegexEvent.UpdateSampleResult(
                    regex,
                    replacement,
                    exampleSource,
                    isCaseInsensitive,
                    isDotAll,
                    isMultiline
                )
            )
            onRegexChange(
                RegexModel(
                    name = name,
                    null,
                    regex = regex,
                    replacement = replacement,
                    exampleSource = exampleSource,
                    isCaseInsensitive = isCaseInsensitive,
                    isDotAll = isDotAll,
                    isMultiline = isMultiline,
                    isEnabled = true
                )
            )
        }
    }
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .border(
                width = 1.dp,
                color = if (state.isError) Color.Red else Color.Transparent
            )
            .fillMaxSize()
            .padding(MaterialTheme.spaces.extraSmall)
            .onPreviewKeyEvent {
                if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                    if (it.isShiftPressed) {
                        focusManager.moveFocus(FocusDirection.Previous)
                    } else {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                    true
                } else if (it.key == Key.Enter && it.type == KeyEventType.KeyDown && it.isCtrlPressed) {
                    if (name.isBlank()) {
                        nameError = true
                    } else if (regex.isEmpty()) {
                        regexError = true
                    } else {
                        onSave(
                            RegexModel(
                                name = name,
                                null,
                                regex = regex,
                                replacement = replacement,
                                exampleSource = exampleSource,
                                isCaseInsensitive = isCaseInsensitive,
                                isDotAll = isDotAll,
                                isMultiline = isMultiline,
                                isEnabled = true
                            )
                        )
                    }
                    true
                } else {
                    false
                }
            },
//        horizontalAlignment = Alignment.Start,
//        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier,
            text = "Rule name:",
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            textAlign = TextAlign.Center
        )
        WinTextField(
            modifier = Modifier
                .fillMaxWidth(),
            fieldModifier = Modifier
                .fillMaxWidth(),
            text = name,
            isError = nameError,
            maxLines = 1,
            singleLine = true,
            onValueChange = {
                name = it
            }
        )
        Spacer(
            modifier = Modifier
                .height(MaterialTheme.spaces.small)
        )
        Text(
            modifier = Modifier,
            text = "Regex:",
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            textAlign = TextAlign.Center
        )
        WinTextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            fieldModifier = Modifier
                .defaultMinSize(minHeight = 72.dp)
                .fillMaxSize(),
            text = regex,
            isError = regexError,
            maxLines = 5,
            onValueChange = {
                regex = it
                sendEvent(
                    RegexEvent.UpdateSampleResult(
                        regex,
                        replacement,
                        exampleSource,
                        isCaseInsensitive,
                        isDotAll,
                        isMultiline
                    )
                )
                onRegexChange(
                    RegexModel(
                        name = name,
                        null,
                        regex = regex,
                        replacement = replacement,
                        exampleSource = exampleSource,
                        isCaseInsensitive = isCaseInsensitive,
                        isDotAll = isDotAll,
                        isMultiline = isMultiline,
                        isEnabled = true
                    )
                )
            }
        )
        Spacer(
            modifier = Modifier
                .height(MaterialTheme.spaces.small)
        )
        Text(
            modifier = Modifier,
            text = "Replacement:",
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            textAlign = TextAlign.Center
        )
        WinTextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            fieldModifier = Modifier
                .defaultMinSize(minHeight = 72.dp)
                .fillMaxSize(),
            text = replacement,
            maxLines = 5,
            onValueChange = {
                replacement = it
                sendEvent(
                    RegexEvent.UpdateSampleResult(
                        regex,
                        replacement,
                        exampleSource,
                        isCaseInsensitive,
                        isDotAll,
                        isMultiline
                    )
                )
                onRegexChange(
                    RegexModel(
                        name = name,
                        null,
                        regex = regex,
                        replacement = replacement,
                        exampleSource = exampleSource,
                        isCaseInsensitive = isCaseInsensitive,
                        isDotAll = isDotAll,
                        isMultiline = isMultiline,
                        isEnabled = true
                    )
                )
            }
        )
        Spacer(
            modifier = Modifier
                .height(MaterialTheme.spaces.small)
        )
        Text(
            modifier = Modifier,
            text = "Source text:",
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            textAlign = TextAlign.Center
        )
        WinTextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            fieldModifier = Modifier
                .defaultMinSize(minHeight = 72.dp)
                .fillMaxSize(),
            text = exampleSource,
            maxLines = 5,
            onValueChange = {
                exampleSource = it
                sendEvent(
                    RegexEvent.UpdateSampleResult(
                        regex,
                        replacement,
                        exampleSource,
                        isCaseInsensitive,
                        isDotAll,
                        isMultiline
                    )
                )
                onRegexChange(
                    RegexModel(
                        name = name,
                        null,
                        regex = regex,
                        replacement = replacement,
                        exampleSource = exampleSource,
                        isCaseInsensitive = isCaseInsensitive,
                        isDotAll = isDotAll,
                        isMultiline = isMultiline,
                        isEnabled = true
                    )
                )
            }
        )
        Spacer(
            modifier = Modifier
                .height(MaterialTheme.spaces.small)
        )
        Row {
            Column {
                WinCheckbox(
                    text = "Is case insensitive",
                    isChecked = isCaseInsensitive,
                    onCheckedChange = {
                        isCaseInsensitive = it
                    }
                )
                WinCheckbox(
                    text = "Is dot all",
                    isChecked = isDotAll,
                    onCheckedChange = {
                        isDotAll = it
                    }
                )
                WinCheckbox(
                    text = "Is multiline",
                    isChecked = isMultiline,
                    onCheckedChange = {
                        isMultiline = it
                    }
                )
            }
            Spacer(
                modifier = Modifier
                    .width(MaterialTheme.spaces.small)
            )
            Column(
                modifier = Modifier
                    .height(86.dp)
                    .drawBehind {
                        // Left border, 1px
                        val strokeWidth = 1 * density
                        drawLine(
                            Color.DarkGray,
                            Offset(0f, 0f),
                            Offset(0f, size.height),
                            strokeWidth
                        )
                    }
                    .padding(MaterialTheme.spaces.extraSmall)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    modifier = Modifier
                        .verticalScroll(state = scrollState),
                    text = state.exampleResult,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(MaterialTheme.spaces.small)
        )
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spaces.extraSmall)
        ) {
            WinButton(
                modifier = Modifier
                    .weight(0.5f),
                text = "Cancel",
                onClick = {
                    onCancel()
                }
            )
            Spacer(
                modifier = Modifier
                    .width(MaterialTheme.spaces.extraSmall)
            )
            WinButton(
                modifier = Modifier
                    .weight(0.5f),
                text = "Save",
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else if (regex.isEmpty()) {
                        regexError = true
                    } else {
                        onSave(
                            RegexModel(
                                name = name,
                                null,
                                regex = regex,
                                replacement = replacement,
                                exampleSource = exampleSource,
                                isCaseInsensitive = isCaseInsensitive,
                                isDotAll = isDotAll,
                                isMultiline = isMultiline,
                                isEnabled = true
                            )
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun RegexDialogPreview() {
    MaterialTheme {
        LTHWordsTheme {
            DialogContent(
                state = RegexState(),
            )
        }
    }
}
