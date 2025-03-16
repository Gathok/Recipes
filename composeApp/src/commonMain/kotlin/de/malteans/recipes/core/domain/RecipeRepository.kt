package de.malteans.recipes.core.domain

import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun upsertRecipe(recipe: Recipe, fromCloud: Boolean = false): Long
    suspend fun upsertOrOpenCloudRecipe(recipe: Recipe): Long  // New function for cloud recipes
    suspend fun deleteRecipeById(id: Long)
    suspend fun fetchLocalRecipes(query: String): List<Recipe>
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Long): Flow<Recipe>

    suspend fun upsertIngredient(ingredient: Ingredient): Long
    suspend fun deleteIngredientById(id: Long)
    fun getAllIngredients(): Flow<List<Ingredient>>

    suspend fun fetchCloudRecipes(query: String): Result<List<Recipe>, DataError.Remote>
}
