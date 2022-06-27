package com.x256n.prtassistant.desktop.screen.home

import com.x256n.prtassistant.desktop.model.RegexModel
import com.x256n.prtassistant.desktop.model.StorageModel

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val storage: StorageModel = StorageModel(),
    val selectedIndex: Int = 0,

    val order: Int? = null,
    val name: String = "",
    val regex: String = "",
    val replacement: String = "",
    val caseInsensitive: Boolean = false,
    val dotAll: Boolean = false,
    val multiline: Boolean = false,

    val sourceText: String = "aaaaa  \r\nbbb",
    val resultText: String = "",
) {
    val selectedItem
        get() =
            storage.regexs[selectedIndex]

    fun createItem() =
        RegexModel(
            order = order,
            name = name,
            regex = regex,
            replacement = replacement,
            caseInsensitive = caseInsensitive,
            dotAll = dotAll,
            multiline = multiline,
            enabled = true
        )

    val hasData get() = storage.regexs.isNotEmpty()
}