package com.x256n.regexapplier.desktop.dialog.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.regexapplier.desktop.common.DispatcherProvider
import com.x256n.regexapplier.desktop.common.StandardDispatcherProvider
import com.x256n.regexapplier.desktop.config.ConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class SettingsViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("SettingsViewModel")

    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state


    fun onEvent(event: SettingsEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is SettingsEvent.SettingsDisplayed -> {
                    _state.value = state.value.copy(
                        isDebugMode = configManager.isDebugMode,
                        processTimeout = configManager.processTimeout,
                    )
                }

                is SettingsEvent.IsDebugMode -> {
                    configManager.isDebugMode = event.value
                    _state.value = state.value.copy(
                        isDebugMode = event.value
                    )
                }

                is SettingsEvent.ProcessTimeout -> {
                    if (event.value.matches(Regex("\\d+"))) {
                        configManager.processTimeout = event.value.toLong()
                        _state.value = state.value.copy(
                            processTimeout = event.value.toLong()
                        )
                    }
                }

                else -> {
                    _log.warn("Unknown event: $event")
                }
            }
        }
    }
}