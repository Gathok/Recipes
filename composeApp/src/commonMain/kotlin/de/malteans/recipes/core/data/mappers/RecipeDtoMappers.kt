package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.data.network.dto.IngredientDto
import de.malteans.recipes.core.data.network.dto.RecipeDto
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe


fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        unit = this.unit ?: ""
    )
}

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = 0L,                // Not saved locally yet, so local id remains 0
        cloudId = this.id,       // Use the cloud id from the DTO
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        ingredients = this.ingredients.associate { ingredientDto ->
            ingredientDto.toDomain() to Pair(ingredientDto.amount, null)
        },
        steps = this.steps.sortedBy { it.stepNumber }.map { it.description },
        workTime = this.workTime,
        totalTime = this.totalTime,
        servings = this.servings,
        rating = this.rating
    )
}
