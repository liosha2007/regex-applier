@file:OptIn(ExperimentalSerializationApi::class)

package com.x256n.prtassistant.desktop.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.prtassistant.desktop.model.RegexModel
import com.x256n.prtassistant.desktop.model.StorageModel
import com.x256n.prtassistant.desktop.navigation.Destinations
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

) : KoinComponent {
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state
    private var replaceJob: Job? = null

    fun onScreenDisplayed(dest: Destinations.Home) {
        CoroutineScope(Dispatchers.Main).launch {
            loadStorage()
            applyChanges()
        }
    }

    fun onEvent(event: HomeEvent) {
        CoroutineScope(Dispatchers.Main).launch {
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
                        add(event.item.copy(enabled = !event.item.enabled))
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
                        name = event.ruleName,
                        order = null,
                        regex = event.regex,
                        replacement = event.replacement,
                        exampleSource = event.exampleSource,
                        caseInsensitive = event.isCaseInsensitive,
                        dotAll = event.isDotAll,
                        multiline = event.isMultiline,
                        enabled = true,
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
                        regex = event.regex,
                        isCaseInsensitive = event.isCaseInsensitive,
                        isDotAll = event.isDotAll,
                        isMultiline = event.isMultiline
                    )?.let { textRange ->
                        _state.value = stateValue.copy(
                            sourceText = stateValue.sourceText.copy(
                                selection = textRange
                            )
                        )
                    }
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
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                BufferedOutputStream(FileOutputStream("storage.json")).use { stream ->
                    Json.encodeToStream(storage, stream)
                }
            }
        }
    }

    private suspend fun loadStorage() {
        val storage: StorageModel? = withContext(Dispatchers.IO) {
            val file = Paths.get("storage.json")
            if (Files.exists(file)) {
                BufferedInputStream(FileInputStream(file.toFile())).use { stream ->
                    Json.decodeFromStream(stream)
                }
            } else null
        }
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

    private suspend fun applyChanges() {
        withContext(Dispatchers.Default) {
            replaceJob = async {
                var processText = _state.value.sourceText.text
                _state.value.storage.regexs.filter { it.enabled }.sortedBy { it.order }.forEach {
                    val pattern = compilePattern(it.regex, it.caseInsensitive, it.dotAll, it.multiline)
                    processText = pattern.matcher(processText).replaceAll(it.replacement)
                }
                _state.value = _state.value.copy(
                    resultText = TextFieldValue(processText)
                )
                replaceJob = null
            }
            delay(3000)
            if (replaceJob != null) {
                replaceJob?.cancel()
                _state.value = _state.value.copy(
                    errorMessage = "It took more than 3 sec!"
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
