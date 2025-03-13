package de.malteans.recipes.presentation.plan

import de.malteans.recipes.domain.Recipe

data class PlanState(
    val planedRecipes: List<Pair<Long, Recipe>> = emptyList(), // Pair<DateTime, Recipe>
)
