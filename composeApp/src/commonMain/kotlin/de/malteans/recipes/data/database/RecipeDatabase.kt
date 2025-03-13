package de.malteans.recipes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.malteans.recipes.data.database.entities.IngredientEntity
import de.malteans.recipes.data.database.entities.RecipeEntity
import de.malteans.recipes.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.data.database.entities.RecipeStepEntity
import de.malteans.recipes.data.database.entities.StepIngredientEntity

@Database(
    entities = [IngredientEntity::class, RecipeEntity::class, RecipeIngredientEntity::class,
               RecipeStepEntity::class, StepIngredientEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class RecipeDatabase : RoomDatabase() {

    abstract val recipeDao: RecipeDao

    companion object {
        const val DB_NAME = "recipes.db"
    }
}