package com.x256n.prtassistant.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.NavContainer
import com.chrynan.navigation.compose.rememberNavigatorByKey
import com.x256n.prtassistant.desktop.screen.home.HomeScreen
import com.x256n.prtassistant.desktop.screen.home.HomeViewModel
import org.koin.java.KoinJavaComponent.inject

typealias Navigator<T> = ComposeNavigatorByKey<T, Destinations>

@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun NavigationComponent() {
    val navigator = rememberNavigatorByKey(initialContext = Destinations.Home) { dest ->
        when (dest) {
            is Destinations.Home -> {
                val viewModel by inject<HomeViewModel>(HomeViewModel::class.java)
                LaunchedEffect(Unit) {
                    viewModel.onScreenDisplayed(dest)
                }
                HomeScreen(viewModel, navigator)
            }
            else -> throw IllegalStateException("Unknown destination! Check NavigationComponent.")
        }
    }

    NavContainer(navigator)
}