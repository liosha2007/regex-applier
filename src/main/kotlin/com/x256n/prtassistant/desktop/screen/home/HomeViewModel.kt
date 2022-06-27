@file:OptIn(ExperimentalSerializationApi::class)

package com.x256n.prtassistant.desktop.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
                        sourceText = event.value
                    )
                    applyChanges()
                }
                is HomeEvent.RegexSelected -> {
                    _state.value = stateValue.copy(
                        selectedIndex = event.index,
                        order = event.value.order,
                        name = event.value.name,
                        regex = event.value.regex,
                        replacement = event.value.replacement,
                        caseInsensitive = event.value.caseInsensitive,
                        dotAll = event.value.dotAll,
                        multiline = event.value.multiline,
                    )
                }
                is HomeEvent.DeleteClicked -> {
                    _state.value = stateValue.copy(
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
                is HomeEvent.NameChanged -> {
                    _state.value = stateValue.copy(
                        name = event.value
                    )
                }
                is HomeEvent.RegexChanged -> {
                    _state.value = stateValue.copy(
                        regex = event.value
                    )
                }
                is HomeEvent.ReplacementChanged -> {
                    _state.value = stateValue.copy(
                        replacement = event.value
                    )
                }
                is HomeEvent.DotAllChanged -> {
                    _state.value = stateValue.copy(
                        dotAll = event.value
                    )
                }
                is HomeEvent.MultilineChanged -> {
                    _state.value = stateValue.copy(
                        multiline = event.value
                    )
                }
                is HomeEvent.CaseInsensitiveChanged -> {
                    _state.value = stateValue.copy(
                        caseInsensitive = event.value
                    )
                }
                is HomeEvent.EnabledClicked -> {
                    val regexs = stateValue.storage.regexs.toMutableList().apply {
                        remove(event.item)
                        add(event.item.copy(enabled = !event.item.enabled))
                    }.sortedBy { it.order }
                    _state.value = stateValue.copy(
                        storage = stateValue.storage.copy(
                            regexs = regexs
                        ),
                    )
                    saveStorage()
                    applyChanges()
                }
                is HomeEvent.UpClicked -> {
                    val item = stateValue.selectedItem
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
                    val item = stateValue.selectedItem
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
                            storage = stateValue.storage.copy(
                                regexs = regexs
                            ),
                            selectedIndex = stateValue.selectedIndex + 1
                        )
                        saveStorage()
                        applyChanges()
                    }
                }
                is HomeEvent.AddRegexClicked -> {
                    _state.value = stateValue.copy(
                        order = null,
                        name = "",
                        regex = "",
                        replacement = "",
                        caseInsensitive = false,
                        dotAll = false,
                        multiline = false,
                    )
                }
                is HomeEvent.SaveRegexClicked -> {
                    val newItem = stateValue.createItem()
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
                        name = addedItem.name,
                        regex = addedItem.regex,
                        replacement = addedItem.replacement,
                        caseInsensitive = addedItem.caseInsensitive,
                        dotAll = addedItem.dotAll,
                        multiline = addedItem.multiline,
                    )
                    saveStorage()
                    applyChanges()
                }
                is HomeEvent.ExpandedChanged -> {
                    _state.value = stateValue.copy(
                        storage = stateValue.storage.copy(
                            expanded = !stateValue.storage.expanded
                        )
                    )
                    saveStorage()
                }
                is HomeEvent.ResetError -> {
                    _state.value = stateValue.copy(
                        errorMessage = null
                    )
                }
            }
        }
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
                        name = _state.value.selectedItem.name,
                        regex = _state.value.selectedItem.regex,
                        replacement = _state.value.selectedItem.replacement,
                        caseInsensitive = _state.value.selectedItem.caseInsensitive,
                        dotAll = _state.value.selectedItem.dotAll,
                        multiline = _state.value.selectedItem.multiline,
                    )
                }
            }
    }

    private suspend fun applyChanges() {
        withContext(Dispatchers.Default) {
            replaceJob = async {
                var processText = _state.value.sourceText
                _state.value.storage.regexs.filter { it.enabled }.sortedBy { it.order }.forEach {
                    var mode = 0
                    if (it.caseInsensitive) mode = mode or Pattern.CASE_INSENSITIVE
                    if (it.dotAll) mode = mode or Pattern.DOTALL
                    if (it.multiline) mode = mode or Pattern.MULTILINE

                    val pattern = Pattern.compile(it.regex, mode)
                    processText = pattern.matcher(processText).replaceAll(it.replacement)
                }
                _state.value = _state.value.copy(
                    resultText = processText
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
}
