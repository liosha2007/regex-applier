package com.x256n.prtassistant.desktop.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.prtassistant.desktop.model.RegexModel

sealed class HomeEvent {
    data class SourceChanged(val value: TextFieldValue) : HomeEvent()
    data class RegexSelected(val value: RegexModel, val index: Int) : HomeEvent()
    data class DeleteClicked(val value: RegexModel) : HomeEvent()

    data class NameChanged(val value: String) : HomeEvent()
    data class RegexChanged(val value: String) : HomeEvent()
    data class ReplacementChanged(val value: String) : HomeEvent()

    data class CaseInsensitiveChanged(val value: Boolean) : HomeEvent()
    data class DotAllChanged(val value: Boolean) : HomeEvent()
    data class MultilineChanged(val value: Boolean) : HomeEvent()

    data class EnabledClicked(val item: RegexModel) : HomeEvent()

    object UpClicked : HomeEvent()
    object DownClicked : HomeEvent()
    object AddRegexClicked : HomeEvent()
    object SaveRegexClicked : HomeEvent()

    object ExpandedChanged : HomeEvent()

    object ResetError : HomeEvent()
}
