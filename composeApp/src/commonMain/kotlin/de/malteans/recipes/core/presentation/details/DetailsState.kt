package de.malteans.recipes.core.presentation.details

import de.malteans.recipes.core.domain.Recipe

data class DetailsState(
    val recipe: Recipe? = null,

    val showPlanDialog: Boolean = false,
)
