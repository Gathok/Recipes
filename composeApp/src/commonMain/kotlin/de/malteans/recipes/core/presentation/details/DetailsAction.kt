package de.malteans.recipes.core.presentation.details

import de.malteans.recipes.core.domain.Recipe

sealed interface DetailsAction {
    data class OnSelectedRecipeChange(val recipe: Recipe) : DetailsAction

    data class OnBack(val onCloudRecipe: Boolean = false) : DetailsAction
    data object OnDelete : DetailsAction
    data object OnEdit : DetailsAction
    data object OnSave : DetailsAction  // New action: trigger saving the cloud recipe locally
}
