package de.malteans.recipes.data.mappers

import de.malteans.recipes.data.database.RecipeWithDetails
import de.malteans.recipes.data.database.entities.RecipeEntity
import de.malteans.recipes.domain.Recipe

// Mapping from domain Recipe to RecipeEntity.
fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        title = this.name,
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
        name = recipe.title,
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