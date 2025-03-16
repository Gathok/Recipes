package de.malteans.recipes.core.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipesResponseDto(
    val recipes: List<RecipeDto>
)
