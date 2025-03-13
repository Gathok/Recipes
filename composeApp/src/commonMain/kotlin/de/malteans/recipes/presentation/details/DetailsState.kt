package de.malteans.recipes.presentation.details

import de.malteans.recipes.domain.Recipe

data class DetailsState(
    val recipe: Recipe? = null,
)
