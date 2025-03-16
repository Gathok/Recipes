package de.malteans.recipes.plan.presentation.plan

import de.malteans.recipes.core.domain.Recipe

data class PlanState(
    val planedRecipes: List<Pair<Long, Recipe>> = emptyList(), // Pair<DateTime, Recipe>
)
