package com.x256n.regexapplier.desktop.navigation

import com.chrynan.navigation.NavigationContext
import com.x256n.regexapplier.desktop.model.RegexModel

sealed class Destination : NavigationContext<Destination> {
    data class Home(val action: Action = Action.None) : Destination() {
        sealed interface Action {
            object None : Action
            data class RegexChange(val regex: RegexModel) : Action
        }
    }

    object Settings : Destination()
    data class RegexDialog(val action: Action = Action.Create) : Destination() {
        sealed interface Action {
            object Create : Action
            data class Edit(val regex: RegexModel) : Action
        }
    }
    object About : Destination()

    override val initialKey: Destination
        get() = Home()
}

