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
    val caseInsensitive: Boolean,
    val dotAll: Boolean,
    val multiline: Boolean,
    val enabled: Boolean,
)
