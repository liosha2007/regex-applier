package com.x256n.regexapplier.desktop.dialog.about

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

class AboutViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("AboutViewModel")

    private val _state = mutableStateOf(AboutState())
    val state: State<AboutState> = _state


    fun onEvent(event: AboutEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            when (event) {
                is AboutEvent.AboutDisplayed -> {
//                    _log.info(doSampleModel())
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
        }
    }
}