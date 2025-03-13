package de.malteans.recipes.presentation.search

import de.malteans.recipes.domain.Recipe

data class SearchState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val localRecipes: List<Recipe> = emptyList(),
)
