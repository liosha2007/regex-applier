package com.x256n.regexapplier.desktop.dialog.settings

data class SettingsState(
    val errorMessage: String? = null,
    val isDebugMode: Boolean = false,
    val processTimeout: Long = 3000,
)
