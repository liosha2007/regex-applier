package com.x256n.regexapplier.desktop.navigation

import com.chrynan.navigation.NavigationContext

sealed class Destination : NavigationContext<Destination> {
    data class Home(val action: Action = Action.None) : Destination() {
        sealed interface Action {
            object None : Action
        }
    }

    object Settings : Destination()
    object About : Destination()

    override val initialKey: Destination
        get() = Home()
}

