package de.malteans.recipes.core.presentation.details

import de.malteans.recipes.core.domain.Recipe

data class DetailsState(
    val recipe: Recipe? = null,

    val deleteFinished: Boolean = false,
    val isDeleting: Boolean = false,

    val showPlanDialog: Boolean = false,
)
