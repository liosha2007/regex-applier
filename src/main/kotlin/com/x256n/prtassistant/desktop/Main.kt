package com.x256n.prtassistant.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.prtassistant.desktop.di.ModulesInjection
import com.x256n.prtassistant.desktop.navigation.NavigationComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import java.awt.Dimension

@ExperimentalMaterialApi
@ExperimentalNavigationApi
@ExperimentalComposeUiApi
fun main() {

    configureKoin()

    application {
        val state = rememberWindowState(
            width = 400.dp,
            height = 680.dp,
            position = WindowPosition.PlatformDefault
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "Porting Assistant",
//        icon = painterResource("icon.png"),
            resizable = true,
            state = state
        ) {
            this.window.minimumSize = Dimension(640, 480)
            this.window.size = Dimension(1024, 680)
            MaterialTheme {
                Column {
                    Box(modifier = Modifier.weight(1f)) {
                        NavigationComponent(state)
                    }
                }
            }
        }
    }
}

fun configureKoin() {
    startKoin {
        logger(PrintLogger())
        modules(ModulesInjection.viewmodelBeans)
        modules(ModulesInjection.usecaseBeans)
        modules(ModulesInjection.daoBeans)
    }
}
