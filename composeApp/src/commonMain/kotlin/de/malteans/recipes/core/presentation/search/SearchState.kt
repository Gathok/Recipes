package de.malteans.recipes.core.presentation.search

import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.errorHandling.DataError

data class SearchState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val localRecipes: List<Recipe> = emptyList(),
    val cloudRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val cloudError: DataError? = null,
)
