package com.x256n.regexapplier.desktop.common

import java.util.regex.Pattern

fun createPattern(
    regex: String,
    isCaseInsensitive: Boolean,
    isDotAll: Boolean,
    isMultiline: Boolean
): Pattern {
    var mode = 0
    if (isCaseInsensitive) mode = mode or Pattern.CASE_INSENSITIVE
    if (isDotAll) mode = mode or Pattern.DOTALL
    if (isMultiline) mode = mode or Pattern.MULTILINE

    return Pattern.compile(regex, mode)
}