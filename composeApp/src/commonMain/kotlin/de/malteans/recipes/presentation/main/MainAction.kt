package de.malteans.recipes.presentation.main

import de.malteans.recipes.presentation.main.components.Screen

sealed interface MainAction {
    data class SetScreen(val screen: Screen) : MainAction
}