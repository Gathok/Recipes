package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.data.database.RecipeWithDetails
import de.malteans.recipes.core.data.database.entities.RecipeEntity
import de.malteans.recipes.core.domain.Recipe

// Mapping from domain Recipe to RecipeEntity.
fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        cloudId = this.cloudId,  // include the cloud id
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        workTime = this.workTime,
        totalTime = this.totalTime,
        servings = this.servings,
        rating = this.rating
    )
}

// Mapping from RecipeWithDetails (the DB join) to domain Recipe.
fun RecipeWithDetails.toDomain(): Recipe {
    return Recipe(
        id = recipe.id,
        cloudId = recipe.cloudId,  // include cloudId from the database
        name = recipe.name,
        description = recipe.description,
        imageUrl = recipe.imageUrl,
        workTime = recipe.workTime,
        totalTime = recipe.totalTime,
        servings = recipe.servings,
        rating = recipe.rating,
        ingredients = recipeIngredients.associate { ri ->
            ri.ingredient.toDomain() to Pair(ri.recipeIngredient.amount, ri.recipeIngredient.overrideUnit)
        },
        steps = recipeSteps.sortedBy { it.stepNumber }.map { it.description }
    )
}
