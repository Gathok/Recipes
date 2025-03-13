package de.malteans.recipes.presentation.search

sealed interface SearchAction{
    data class OnSearchQueryChange(val query: String): SearchAction
    data class OnTabSelected(val index: Int) : SearchAction

    data class OnRecipeClick(val recipeId: Long): SearchAction
}