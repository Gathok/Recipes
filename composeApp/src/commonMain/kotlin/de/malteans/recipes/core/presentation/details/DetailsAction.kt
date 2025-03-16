package de.malteans.recipes.core.presentation.details

sealed interface DetailsAction {
    data object OnBack : DetailsAction
    data object OnDelete : DetailsAction
    data object OnEdit : DetailsAction
    data object OnSave : DetailsAction  // New action: trigger saving the cloud recipe locally
}
