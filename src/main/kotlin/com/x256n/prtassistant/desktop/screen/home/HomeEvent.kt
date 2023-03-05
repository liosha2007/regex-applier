package com.x256n.prtassistant.desktop.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.prtassistant.desktop.model.RegexModel

sealed class HomeEvent {
    data class SourceChanged(val value: TextFieldValue) : HomeEvent()
    data class RegexSelected(val value: RegexModel, val index: Int) : HomeEvent()
    data class DeleteClicked(val value: RegexModel) : HomeEvent()

    object ResultFocused : HomeEvent()

    data class EnabledClicked(val item: RegexModel) : HomeEvent()

    object UpClicked : HomeEvent()
    object DownClicked : HomeEvent()
    data class SaveRegexClicked(
        val ruleName: String,
        val regex: String,
        val replacement: String,
        val exampleSource: String,
        val isCaseInsensitive: Boolean,
        val isDotAll: Boolean,
        val isMultiline: Boolean
    ) : HomeEvent()

    data class RegexChanged(
        val regex: String,
        val isCaseInsensitive: Boolean,
        val isDotAll: Boolean,
        val isMultiline: Boolean
    ) : HomeEvent()

    object ResetError : HomeEvent()
}
