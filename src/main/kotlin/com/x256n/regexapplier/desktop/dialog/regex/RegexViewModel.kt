package com.x256n.regexapplier.desktop.dialog.regex

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.x256n.regexapplier.desktop.common.DispatcherProvider
import com.x256n.regexapplier.desktop.common.StandardDispatcherProvider
import com.x256n.regexapplier.desktop.common.createPattern
import com.x256n.regexapplier.desktop.config.ConfigManager
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.util.regex.PatternSyntaxException

class RegexViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager
) : KoinComponent {
    private val _log = LoggerFactory.getLogger(this::class.java)
    private var regexJob: Job? = null
    private var timerJob: Job? = null

    var state by mutableStateOf(RegexState())
        private set

    fun sendEvent(event: RegexEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            try {
                onEvent(event)
            } catch (e: Exception) {
                _log.error("Error while handling event!", e)
                state = state.copy(
                    isError = true
                )
            }
        }
    }

    private suspend fun onEvent(event: RegexEvent) =
        when (event) {
            is RegexEvent.RegexDisplayed -> {
                _log.debug("RegexViewModel")
            }

            is RegexEvent.UpdateSampleResult -> handleUpdateSampleResult(event)

            else -> {
                TODO("Not implemented: $event")
            }
        }

    private suspend fun handleUpdateSampleResult(event: RegexEvent.UpdateSampleResult) {
        _log.debug("handleUpdateSampleResult: '${event.regex}'")
        return withContext(dispatcherProvider.default) {
            timerJob?.cancel()
            regexJob?.cancel()

            regexJob = async {
                try {
                    val pattern = createPattern(
                        event.regex,
                        event.isCaseInsensitive,
                        event.isDotAll,
                        event.isMultiline
                    )
                    state = state.copy(
                        isError = false,
                        exampleResult = pattern.matcher(event.exampleSource).replaceAll(event.replacement)
                    )
                } catch (e: PatternSyntaxException) {
                    state = state.copy(
                        isError = true,
                    )
                } catch (e: IllegalArgumentException) {
                    state = state.copy(
                        isError = true,
                    )
                } catch (e: Exception) {
                    if (e !is IndexOutOfBoundsException || e.message?.contains("No Group") != true) {
                        _log.error("Regex error", e)
                    }
                    state = state.copy(
                        isError = true,
                    )
                }
                regexJob = null
            }
            timerJob = async {
                delay(configManager.processTimeout)
                if (isActive && regexJob != null && regexJob?.isActive == true) {
                    regexJob?.cancel()
                    state = state.copy(
                        isError = true,
                    )
                }
            }

        }
    }
}
