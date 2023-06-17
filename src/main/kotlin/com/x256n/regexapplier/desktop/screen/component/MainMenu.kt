package com.x256n.regexapplier.desktop.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBarScope

@Composable
fun MenuBarScope.MainMenu(
    onOpenFile: () -> Unit = {},
    onClearPanels: () -> Unit = {},
    onExit: () -> Unit = {},
    onSettings: () -> Unit = {},
    onAbout: () -> Unit = {},
) {
    Menu("File") {
        Item(
            "Open file...",
            mnemonic = 'O',
            onClick = onOpenFile
        )
        Item(
            "Clear panels",
            mnemonic = 'C',
            onClick = onClearPanels
        )
        Item(
            "Exit",
            mnemonic = 'Q',
            onClick = onExit
        )
    }

    Menu("Options") {
        Item(
            "Settings...",
            mnemonic = 's',
            onClick = onSettings
        )
    }

    Menu("Help") {
        Item(
            "About",
            mnemonic = 'A',
            onClick = onAbout
        )
    }
}