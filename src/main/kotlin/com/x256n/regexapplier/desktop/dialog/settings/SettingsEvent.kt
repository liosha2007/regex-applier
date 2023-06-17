package com.x256n.regexapplier.desktop.dialog.settings

sealed class SettingsEvent {
    object SettingsDisplayed : SettingsEvent()
    data class IsDebugMode(val value: Boolean) : SettingsEvent()
    data class ProcessTimeout(val value: String) : SettingsEvent()
}
