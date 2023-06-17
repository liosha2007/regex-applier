@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package com.x256n.regexapplier.desktop.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.regexapplier.desktop.common.DispatcherProvider
import com.x256n.regexapplier.desktop.config.ConfigManager
import com.x256n.regexapplier.desktop.model.RegexModel
import com.x256n.regexapplier.desktop.model.StorageModel
import com.x256n.regexapplier.desktop.navigation.Destination
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.component.KoinComponent
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class HomeViewModel(
    private val configManager: ConfigManager,
    private val dispatcherProvider: DispatcherProvider
) : KoinComponent {
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state
    private var replaceJob: Job? = null

    fun onScreenDisplayed(dest: Destination) {
        if (dest is Destination.Home && dest.action is Destination.Home.Action.None) {
            CoroutineScope(dispatcherProvider.default).launch {
                loadStorage()
                applyChanges()
            }
        }
//            if (dest is Destinations.Home && dest.action is Destinations.Home.Action.LoadProject) {
//                sendEvent(HomeEvent.LoadProject(dest.action.projectDirectory))
//            }
//            if (dest is Destinations.Home && dest.action is Destinations.Home.Action.YesCancelDialogResult) {
//                sendEvent(dest.action.targetEvent as HomeEvent)
//            }
//            if (dest is Destinations.Home && dest.action is Destinations.Home.Action.DeleteCaptionsConfirmationDialogResult) {
//                sendEvent(HomeEvent.DeleteAllCaptions(dest.action.isDeleteOnlyEmpty))
//            }
    }

    fun onEvent(event: HomeEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            val stateValue = _state.value
            when (event) {
                is HomeEvent.SourceChanged -> {
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                        sourceText = event.value
                    )
                    applyChanges()
                }

                is HomeEvent.RegexSelected -> {
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                        selectedIndex = event.index,
                        order = event.value.order
                    )
                }

                is HomeEvent.DeleteClicked -> {
                    _state.value = stateValue.copy(
                        itemToDelete = event.value
                    )
                }

                is HomeEvent.DeleteConfirmed -> {
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                        storage = stateValue.storage.copy(
                            regexs = stateValue.storage.regexs.toMutableList().apply {
                                remove(event.value)
                            }.sortedBy { it.order }.mapIndexed { index, model ->
                                model.copy(
                                    order = index
                                )
                            }
                        )
                    )
                    saveStorage()
                }

                is HomeEvent.ResultFocused -> {
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                        resultText = stateValue.resultText.copy(
                            selection = TextRange(0, _state.value.resultText.text.length)
                        )
                    )
                }

                is HomeEvent.EnabledClicked -> {
                    val regexs = stateValue.storage.regexs.toMutableList().apply {
                        remove(event.item)
                        add(event.item.copy(isEnabled = !event.item.isEnabled))
                    }.sortedBy { it.order }
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                        storage = stateValue.storage.copy(
                            regexs = regexs
                        ),
                    )
                    saveStorage()
                    applyChanges()
                }

                is HomeEvent.UpClicked -> {
                    val item = event.item
                    if (item.order != null && item.order > 0) {
                        val selected = stateValue.storage.regexs.first { it.order == item.order }
                        val prew = stateValue.storage.regexs.first { it.order == item.order - 1 }

                        val regexs = stateValue.storage.regexs.toMutableList().apply {
                            remove(selected)
                            remove(prew)
                            add(selected.copy(order = prew.order))
                            add(prew.copy(order = selected.order))
                        }.sortedBy { it.order }

                        _state.value = stateValue.copy(
                            itemToDelete = null, // Reset deletion confirmation
                            storage = stateValue.storage.copy(
                                regexs = regexs
                            ),
                            selectedIndex = stateValue.selectedIndex - 1
                        )
                        saveStorage()
                        applyChanges()
                    }
                }

                is HomeEvent.DownClicked -> {
                    val item = event.item
                    if (item.order != null && item.order < stateValue.storage.regexs.lastIndex) {
                        val selected = stateValue.storage.regexs.first { it.order == item.order }
                        val next = stateValue.storage.regexs.first { it.order == item.order + 1 }

                        val regexs = stateValue.storage.regexs.toMutableList().apply {
                            remove(selected)
                            remove(next)
                            add(selected.copy(order = next.order))
                            add(next.copy(order = selected.order))
                        }.sortedBy { it.order }

                        _state.value = stateValue.copy(
                            itemToDelete = null, // Reset deletion confirmation
                            storage = stateValue.storage.copy(
                                regexs = regexs
                            ),
                            selectedIndex = stateValue.selectedIndex + 1
                        )
                        saveStorage()
                        applyChanges()
                    }
                }

                is HomeEvent.SaveRegexClicked -> {
                    val newItem = RegexModel(
                        name = event.regexModel.name,
                        order = event.regexModel.order,
                        regex = event.regexModel.regex,
                        replacement = event.regexModel.replacement,
                        exampleSource = event.regexModel.exampleSource,
                        isCaseInsensitive = event.regexModel.isCaseInsensitive,
                        isDotAll = event.regexModel.isDotAll,
                        isMultiline = event.regexModel.isMultiline,
                        isEnabled = event.regexModel.isEnabled,
                    )
                    val storage = if (newItem.order == null) {
                        stateValue.storage.copy(
                            regexs = stateValue.storage.regexs.toMutableList().apply {
                                add(newItem.copy(order = stateValue.storage.regexs.size))
                            }.sortedBy { it.order }
                        )
                    } else {
                        stateValue.storage.copy(
                            regexs = stateValue.storage.regexs.toMutableList().apply {
                                removeIf { it.order == newItem.order }
                                add(newItem)
                            }.sortedBy { it.order }
                        )
                    }
                    val addedItem = storage.regexs.first { it.order == (newItem.order ?: storage.regexs.lastIndex) }
                    _state.value = stateValue.copy(
                        storage = storage,
                        selectedIndex = storage.regexs.indexOf(addedItem),
                        order = addedItem.order,
                    )
                    saveStorage()
                    applyChanges()
                }

                is HomeEvent.RegexChanged -> {
                    detectTextSelectionRange(
                        regex = event.regexModel.regex,
                        isCaseInsensitive = event.regexModel.isCaseInsensitive,
                        isDotAll = event.regexModel.isDotAll,
                        isMultiline = event.regexModel.isMultiline
                    )?.let { textRange ->
                        _state.value = stateValue.copy(
                            sourceText = stateValue.sourceText.copy(
                                selection = textRange
                            )
                        )
                    }
                }

                is HomeEvent.EditRegexClicked -> {
                    // Processed in HomeScreen
                }

                is HomeEvent.ResetError -> {
                    _state.value = stateValue.copy(
                        errorMessage = null
                    )
                }

                is HomeEvent.RegexDialogShown -> {
                    _state.value = stateValue.copy(
                        itemToDelete = null, // Reset deletion confirmation
                    )
                }

                is HomeEvent.OpenFile -> {
                    openFile(event.action)
                }

                is HomeEvent.ClearPanels -> {
                    clearPanels()
                }
            }
        }
    }

    private suspend fun clearPanels() {
        _state.value = state.value.copy(
            sourceText = TextFieldValue()
        )
        applyChanges()
    }

    private suspend fun openFile(action: HomeEvent.OpenFile.Action) {
        when (action) {
            is HomeEvent.OpenFile.Action.ShowFileChooserDialog ->
                _state.value = state.value.copy(isShowChooseProjectDirectoryDialog = true)

            is HomeEvent.OpenFile.Action.ProcessSelectedFile -> {
                CoroutineScope(dispatcherProvider.io).launch {
                    if (Files.exists(action.path) && Files.isRegularFile(action.path) && Files.isReadable(action.path)) {
                        val fileContent = Files.readString(action.path)
                        _state.value = state.value.copy(
                            isShowChooseProjectDirectoryDialog = false,
                            sourceText = TextFieldValue(fileContent)
                        )
                        applyChanges()
                    } else {
                        _state.value = state.value.copy(
                            errorMessage = "Can't read '${action.path}' file!"
                        )
                    }
                }
            }
        }

    }

    private fun detectTextSelectionRange(
        regex: String,
        isCaseInsensitive: Boolean,
        isDotAll: Boolean,
        isMultiline: Boolean
    ): TextRange? {
        if (regex.isNotBlank()) {
            val resultText = _state.value.resultText.text
            val resultCursor = _state.value.resultText.selection.start
            try {
                val pattern = compilePattern(
                    regex,
                    isCaseInsensitive,
                    isDotAll,
                    isMultiline
                )
                val matcher = pattern.matcher(resultText)
                if (matcher.find()) {
                    val found = matcher.group()
                    var start = resultText.indexOf(found, startIndex = resultCursor)
                    if (start == -1) {
                        start = resultText.indexOf(found)
                    }
                    return TextRange(start, start + found.length)
                }
            } catch (e: PatternSyntaxException) {
                // Entering not finished
            }
        }
        return null
    }

    private fun saveStorage() {
        val storage = _state.value.storage
        CoroutineScope(dispatcherProvider.io).launch {
            BufferedOutputStream(FileOutputStream("storage.json")).use { stream ->
                Json.encodeToStream(storage, stream)
            }
        }
    }

    private suspend fun loadStorage() {
        val storage: StorageModel? = withContext(dispatcherProvider.io) {
            val file = Paths.get("storage.json")
            if (Files.exists(file)) {
                BufferedInputStream(FileInputStream(file.toFile())).use { stream ->
                    Json.decodeFromStream(stream)
                }
            } else null
        }
        withContext(dispatcherProvider.main) {
            storage?.let {
                _state.value = _state.value.copy(
                    storage = storage,
                )
                if (_state.value.hasData) {
                    _state.value = _state.value.copy(
                        storage = storage,
                        order = _state.value.selectedItem.order,
                    )
                }
            }
        }
    }

    private suspend fun applyChanges() {
        withContext(dispatcherProvider.default) {
            replaceJob = async {
                var processText = _state.value.sourceText.text
                _state.value.storage.regexs.filter { it.isEnabled }.sortedBy { it.order }.forEach {
                    val pattern = compilePattern(it.regex, it.isCaseInsensitive, it.isDotAll, it.isMultiline)
                    processText = pattern.matcher(processText).replaceAll(it.replacement)
                }
                _state.value = _state.value.copy(
                    resultText = TextFieldValue(processText)
                )
                replaceJob = null
            }
            delay(configManager.processTimeout)
            if (replaceJob != null) {
                replaceJob?.cancel()
                _state.value = _state.value.copy(
                    errorMessage = "The process took more than ${configManager.processTimeout}ms!"
                )
            }
        }
    }

    private fun compilePattern(regex: String, caseInsensitive: Boolean, dotAll: Boolean, multiline: Boolean): Pattern {
        var mode = 0
        if (caseInsensitive) mode = mode or Pattern.CASE_INSENSITIVE
        if (dotAll) mode = mode or Pattern.DOTALL
        if (multiline) mode = mode or Pattern.MULTILINE

        return Pattern.compile(regex, mode)
    }
}
