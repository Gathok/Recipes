package de.malteans.recipes.core.data.repository

import de.malteans.recipes.core.data.database.RecipeDao
import de.malteans.recipes.core.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeStepEntity
import de.malteans.recipes.core.data.mappers.toDomain
import de.malteans.recipes.core.data.mappers.toIngredientEntity
import de.malteans.recipes.core.data.mappers.toRecipeEntity
import de.malteans.recipes.core.data.network.RemoteRecipeDataSource
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeRepository
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DefaultRecipeRepository(
    private val dao: RecipeDao,
    private val remoteDataSource: RemoteRecipeDataSource
) : RecipeRepository {

    override suspend fun upsertRecipe(recipe: Recipe, fromCloud: Boolean): Long {
        // Map domain Recipe to RecipeEntity
        val recipeEntity = recipe.toRecipeEntity()
        // Upsert the recipe and get its ID (using upsert as a combined insert/update)
        var recipeId = dao.upsertRecipe(recipeEntity)
        if (recipeId == -1L) {
            recipeId = recipe.id
        }
        // Remove existing ingredient and step relations
        dao.deleteRecipeIngredientsForRecipe(recipeId)
        dao.deleteRecipeStepsForRecipe(recipeId)
        // Insert recipe ingredients based on the domain recipe.ingredients list.
        // If the recipe is from the cloud, find ingredients by name or insert them.
        val localIngredients = dao.getAllIngredients().first().associate { it.name.lowercase() to it }
        recipe.ingredients.forEach { mapEntry ->
            val (ingredient, amountPair) = mapEntry
            val (amount, overrideUnit) = amountPair
            // If from cloud, check for ingredient with same name in local db.
            // -> If found, use the local ingredient, otherwise insert it.
            val localIngredient: Ingredient = if (fromCloud) localIngredients[ingredient.name.lowercase()]?.toDomain()
                ?: upsertIngredient(ingredient.copy(id = 0L)).let { ingredient.copy(id = it) }
            else ingredient
            val recipeIngredientEntity = RecipeIngredientEntity(
                recipeId = recipeId,
                ingredientId = localIngredient.id,
                amount = amount,
                overrideUnit = overrideUnit
                    ?: if (ingredient.unit != localIngredient.unit) ingredient.unit
                    else null
            )
            dao.upsertRecipeIngredient(recipeIngredientEntity)
        }
        // Insert recipe steps.
        recipe.steps.forEachIndexed { index, step ->
            val recipeStepEntity = RecipeStepEntity(
                recipeId = recipeId,
                stepNumber = index,
                description = step,
                duration = null // TODO: No duration info provided; defaulting to null.
            )
            dao.upsertRecipeStep(recipeStepEntity)
        }
        return recipeId
    }

    // New function: If the recipe is a cloud recipe and has not been saved locally,
    // check if a recipe with the same cloudId exists and return its id.
    // Otherwise, upsert the recipe.
    override suspend fun saveCloudRecipe(recipe: Recipe): Long {
        return upsertRecipe(recipe, fromCloud = true)
    }

    override suspend fun deleteRecipeById(id: Long) {
        dao.deleteRecipe(id)
    }

    // Updated to perform search directly in the database.
    override suspend fun fetchLocalRecipes(query: String): List<Recipe> {
        return dao.searchRecipes(query)
            .first()
            .map { it.toDomain() }
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipesWithDetails().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecipeById(id: Long): Flow<Recipe> {
        return dao.getRecipeWithDetails(id).map { it.toDomain() }
    }

    override suspend fun upsertIngredient(ingredient: Ingredient): Long {
        return dao.upsertIngredient(ingredient.toIngredientEntity())
    }

    override suspend fun deleteIngredientById(id: Long) {
        dao.deleteRecipe(id)
    }

    override fun getAllIngredients(): Flow<List<Ingredient>> {
        return dao.getAllIngredients().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun fetchCloudRecipes(query: String): Result<List<Recipe>, DataError.Remote> {
        return remoteDataSource
            .fetchRecipes(query)
            .map { dtoList ->
                val allLocalRecipes = dao.getAllRecipes().associateBy { it.cloudId }
                dtoList
                    .map { dto -> dto.toDomain() }
                    .map { cloudRecipe ->
                        val localRecipe = allLocalRecipes[cloudRecipe.cloudId]
                        if (localRecipe != null) {
                            cloudRecipe.copy(
                                id = localRecipe.id,
                                editedFromCloud = localRecipe.editedFromCloud
                            )
                        } else {
                            cloudRecipe
                        }
                    }
            }
    }
}
