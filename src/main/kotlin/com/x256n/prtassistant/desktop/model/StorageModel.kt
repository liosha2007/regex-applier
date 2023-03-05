package com.x256n.prtassistant.desktop.model

@kotlinx.serialization.Serializable
data class StorageModel(
    val regexs: List<RegexModel> = emptyList()
)

@kotlinx.serialization.Serializable
data class RegexModel(
    val name: String,
    val order: Int?,
    val regex: String,
    val replacement: String,
    val exampleSource: String,
    val isCaseInsensitive: Boolean,
    val isDotAll: Boolean,
    val isMultiline: Boolean,
    val isEnabled: Boolean,
) {
    companion object {
        val Empty get() = RegexModel("", null, "", "", "", false, false, false, true).copy()
    }
}
