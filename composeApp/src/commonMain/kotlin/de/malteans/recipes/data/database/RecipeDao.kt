package de.malteans.recipes.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import de.malteans.recipes.data.database.entities.IngredientEntity
import de.malteans.recipes.data.database.entities.RecipeEntity
import de.malteans.recipes.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.data.database.entities.RecipeStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Recipe Methods ---------------------------------------------------
    @Upsert
    suspend fun upsertRecipe(recipe: RecipeEntity): Long

    @Query("DELETE FROM RecipeEntity WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Long)

    @Upsert
    suspend fun upsertRecipeIngredient(recipeIngredient: RecipeIngredientEntity)

    @Upsert
    suspend fun upsertRecipeStep(recipeStep: RecipeStepEntity)

    @Query("DELETE FROM RecipeIngredientEntity WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredientsForRecipe(recipeId: Long)

    @Query("DELETE FROM RecipeStepEntity WHERE recipeId = :recipeId")
    suspend fun deleteRecipeStepsForRecipe(recipeId: Long)

    // Get recipe details with ingredients and steps
    @Transaction
    @Query("SELECT * FROM RecipeEntity WHERE id = :recipeId")
    fun getRecipeWithDetails(recipeId: Long): Flow<RecipeWithDetails>

    @Transaction
    @Query("SELECT * FROM RecipeEntity")
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>

    // Ingredient Methods ----------------------------------------------
    @Upsert
    suspend fun upsertIngredient(ingredient: IngredientEntity): Long

    @Query("DELETE FROM IngredientEntity WHERE id = :ingredientId")
    suspend fun deleteIngredient(ingredientId: Long)

    @Query("SELECT * FROM ingrediententity")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

}
