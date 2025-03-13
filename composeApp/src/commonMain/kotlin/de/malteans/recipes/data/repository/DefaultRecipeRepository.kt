package de.malteans.recipes.data.repository

import de.malteans.recipes.data.database.RecipeDao
import de.malteans.recipes.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.data.database.entities.RecipeStepEntity
import de.malteans.recipes.data.mappers.toDomain
import de.malteans.recipes.data.mappers.toIngredientEntity
import de.malteans.recipes.data.mappers.toRecipeEntity
import de.malteans.recipes.domain.Ingredient
import de.malteans.recipes.domain.Recipe
import de.malteans.recipes.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultRecipeRepository(
    private val dao: RecipeDao,
) : RecipeRepository {

    override suspend fun upsertRecipe(recipe: Recipe): Long {
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
        // Each domain ingredient is a Pair<Pair<Int?, String?>, Ingredient>
        recipe.ingredients.forEach { mapEntry ->
            val (ingredient, amountPair) = mapEntry
            val (amount, overrideUnit) = amountPair
            val recipeIngredientEntity = RecipeIngredientEntity(
                recipeId = recipeId,
                ingredientId = ingredient.id,
                amount = amount,
                overrideUnit = overrideUnit
            )
            dao.upsertRecipeIngredient(recipeIngredientEntity)
        }
        // Insert recipe steps.
        // The domain recipe.steps is a list of step descriptions.
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

    override suspend fun deleteRecipeById(id: Long) {
        dao.deleteRecipe(id)
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
}
