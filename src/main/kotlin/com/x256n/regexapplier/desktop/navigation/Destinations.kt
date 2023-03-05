package com.x256n.regexapplier.desktop.navigation

import com.chrynan.navigation.NavigationContext

sealed class Destinations : NavigationContext<Destinations> {
    object Home : Destinations()
    data class About(val test: String) : Destinations()

    override val initialKey: Destinations
        get() = Home
}
