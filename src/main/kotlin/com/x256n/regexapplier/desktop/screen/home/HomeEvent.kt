package com.x256n.regexapplier.desktop.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import com.x256n.regexapplier.desktop.model.RegexModel
import java.nio.file.Path

sealed class HomeEvent {
    data class SourceChanged(val value: TextFieldValue) : HomeEvent()
    data class RegexSelected(val value: RegexModel, val index: Int) : HomeEvent()
    data class EditRegexClicked(val item: RegexModel) : HomeEvent()
    data class DeleteClicked(val value: RegexModel) : HomeEvent()
    data class DeleteConfirmed(val value: RegexModel) : HomeEvent()

    object ResultFocused : HomeEvent()

    data class EnabledClicked(val item: RegexModel) : HomeEvent()

    data class UpClicked(val item: RegexModel) : HomeEvent()
    data class DownClicked(val item: RegexModel) : HomeEvent()
    data class SaveRegexClicked(
        val regexModel: RegexModel
    ) : HomeEvent()

    data class RegexChanged(val regexModel: RegexModel) : HomeEvent()

    object ResetError : HomeEvent()
    object RegexDialogShown : HomeEvent()

    data class OpenFile(val action: Action = Action.ShowFileChooserDialog) : HomeEvent() {
        sealed interface Action {
            object ShowFileChooserDialog : Action
            data class ProcessSelectedFile(val path: Path) : Action
        }
    }
    object ClearPanels : HomeEvent()
}
