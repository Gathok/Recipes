package de.malteans.recipes.presentation.main

import de.malteans.recipes.presentation.main.components.Screen

data class MainState(
    val curScreen: Screen = Screen.Search,
)
