package de.malteans.recipes.domain

import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun upsertRecipe(recipe: Recipe): Long
    suspend fun deleteRecipeById(id: Long)
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Long): Flow<Recipe>

    suspend fun upsertIngredient(ingredient: Ingredient): Long
    suspend fun deleteIngredientById(id: Long)
    fun getAllIngredients(): Flow<List<Ingredient>>
}
