@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.x256n.regexapplier.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.chrynan.navigation.compose.NavContainer
import com.chrynan.navigation.compose.goTo
import com.chrynan.navigation.compose.rememberNavigatorByKey
import com.x256n.regexapplier.desktop.config.ConfigManager
import com.x256n.regexapplier.desktop.di.ModulesInjection
import com.x256n.regexapplier.desktop.dialog.about.AboutDialog
import com.x256n.regexapplier.desktop.dialog.regex.RegexDialog
import com.x256n.regexapplier.desktop.dialog.regex.RegexViewModel
import com.x256n.regexapplier.desktop.dialog.settings.SettingsDialog
import com.x256n.regexapplier.desktop.navigation.Destination
import com.x256n.regexapplier.desktop.screen.component.MainMenu
import com.x256n.regexapplier.desktop.screen.home.HomeEvent
import com.x256n.regexapplier.desktop.screen.home.HomeScreen
import com.x256n.regexapplier.desktop.screen.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.java.KoinJavaComponent
import java.awt.Dimension
import kotlin.system.exitProcess

@ExperimentalMaterialApi
@ExperimentalNavigationApi
@ExperimentalComposeUiApi
fun main() {
    ConfigManager.reloadConfig()
    configureKoin()
    application {
        val state = rememberWindowState(
            width = 1024.dp,
            height = 680.dp,
            position = WindowPosition.PlatformDefault
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "Regex applier (by liosha) v1.0.2",
            icon = painterResource("icon.png"),
            resizable = true,
            state = state
        ) {
            this.window.minimumSize = Dimension(640, 480)
            this.window.size = Dimension(1024, 680)
            MaterialTheme {
                val navigator = rememberNavigatorByKey(initialContext = Destination.Home()) { dest ->

                    val homeViewModel by KoinJavaComponent.inject<HomeViewModel>(HomeViewModel::class.java)
                    rememberSaveable(dest) {
                        homeViewModel.onScreenDisplayed(dest)
                    }

                    MenuBar {
                        MainMenu(
                            onOpenFile = {
                                homeViewModel.onEvent(HomeEvent.OpenFile())
                            },
                            onClearPanels = {
                                homeViewModel.onEvent(HomeEvent.ClearPanels)
                            },
                            onExit = {
                                exitProcess(0)
                            },
                            onSettings = {
                                navigator.goTo(Destination.Settings)
                            },
                            onAbout = {
                                navigator.goTo(Destination.About)
                            },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {

                            HomeScreen(
                                action = if (dest is Destination.Home) dest.action else Destination.Home.Action.None,
                                state = homeViewModel.state.value,
                                sendEvent = homeViewModel::onEvent,
                                rootPanel = window.rootPane,
                                windowPositionX = state.position.x,
                                windowPositionY = state.position.y,
                                navigate = { navigator.goTo(it) }
                            )

                            when (dest) {
                                is Destination.Home -> { /* Active always, ignore */
                                }

                                is Destination.Settings -> {
                                    SettingsDialog(
                                        onCancel = {
                                            navigator.goBack()
                                        }
                                    )
                                }

                                is Destination.RegexDialog -> {
                                    val viewModel by KoinJavaComponent.inject<RegexViewModel>(RegexViewModel::class.java)
                                    val dialogState = remember {
                                        DialogState(
                                            position = WindowPosition.Aligned(Alignment.Center),
                                            width = 360.dp,
                                            height = 480.dp
                                        )
                                    }
                                    RegexDialog(
                                        action = dest.action,
                                        state = viewModel.state,
                                        dialogState = dialogState,
                                        sendEvent = viewModel::sendEvent,
                                        onRegexChange = {
                                            homeViewModel.onEvent(HomeEvent.RegexChanged(it))
                                        },
                                        onCancel = {
                                            navigator.goBack()
                                        },
                                        onSave = { regexModel ->
                                            navigator.goTo(
                                                Destination.Home(action = Destination.Home.Action.RegexChange(regexModel)),
                                                StackDuplicateContentStrategy.CLEAR_STACK
                                            )
                                        }
                                    )
                                }

                                is Destination.About -> {
                                    AboutDialog {
                                        navigator.goBack()
                                    }
                                }

                                else -> throw IllegalStateException("Unknown destination ($dest)! Check NavigationComponent.")
                            }
                        }

                    }
                }
                NavContainer(navigator)
            }
        }
    }
}

fun configureKoin() {
    startKoin {
        logger(PrintLogger())
        modules(ModulesInjection.viewmodelBeans)
        modules(ModulesInjection.usecaseBeans)
        modules(ModulesInjection.managerBeans)
        modules(ModulesInjection.otherBeans)
    }
}
