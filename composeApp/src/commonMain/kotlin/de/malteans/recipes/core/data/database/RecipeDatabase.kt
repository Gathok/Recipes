package de.malteans.recipes.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.entities.IngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeEntity
import de.malteans.recipes.core.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeStepEntity
import de.malteans.recipes.core.data.database.entities.StepIngredientEntity

@Database(
    entities = [IngredientEntity::class, RecipeEntity::class, RecipeIngredientEntity::class,
        RecipeStepEntity::class, StepIngredientEntity::class],
    version = 3, // Updated from 2 to 3 to add the cloudId column
    exportSchema = false,
)
abstract class RecipeDatabase : RoomDatabase() {

    abstract val recipeDao: RecipeDao

    companion object {
        const val DB_NAME = "recipes.db"
    }
}
