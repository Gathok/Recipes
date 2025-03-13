package de.malteans.recipes.presentation.details

sealed interface DetailsAction {
    data object OnBack : DetailsAction
    data object OnDelete : DetailsAction
    data object OnEdit : DetailsAction
}
