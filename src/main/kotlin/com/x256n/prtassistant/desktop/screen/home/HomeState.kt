package com.x256n.prtassistant.desktop.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.prtassistant.desktop.model.RegexModel
import com.x256n.prtassistant.desktop.model.StorageModel

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val storage: StorageModel = StorageModel(),
    val selectedIndex: Int = 0,

    val order: Int? = null,

    val sourceText: TextFieldValue = TextFieldValue(""),
    val resultText: TextFieldValue = TextFieldValue(""),
) {
    val selectedItem
        get() =
            storage.regexs[selectedIndex]

    val hasData get() = storage.regexs.isNotEmpty()
}