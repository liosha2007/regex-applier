package com.x256n.regexapplier.desktop.dialog.regex

sealed interface RegexEvent {
    object RegexDisplayed : RegexEvent

    data class UpdateSampleResult(
        val regex: String,
        val replacement: String,
        val exampleSource: String,
        val isCaseInsensitive: Boolean,
        val isDotAll: Boolean,
        val isMultiline: Boolean
    ) : RegexEvent
}
