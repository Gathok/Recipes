package de.malteans.recipes.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_2
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_3
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_4
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_3
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_4
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION3_4
import de.malteans.recipes.core.data.database.migrations.MIGRATION3_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION3_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION4_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION4_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION5_6
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val dbFilePath = documentDirectory() + "/${RecipeDatabase.DB_NAME}"
        return Room.databaseBuilder<RecipeDatabase>(
            name = dbFilePath,
        )
            .addMigrations(
                RecipeDatabase.Companion.MIGRATION1_2,
                RecipeDatabase.Companion.MIGRATION1_3, RecipeDatabase.Companion.MIGRATION2_3,
                RecipeDatabase.Companion.MIGRATION1_4, RecipeDatabase.Companion.MIGRATION2_4,
                    RecipeDatabase.Companion.MIGRATION3_4,
                RecipeDatabase.Companion.MIGRATION1_5, RecipeDatabase.Companion.MIGRATION2_5,
                    RecipeDatabase.Companion.MIGRATION3_5, RecipeDatabase.Companion.MIGRATION4_5,
                RecipeDatabase.Companion.MIGRATION1_6, RecipeDatabase.Companion.MIGRATION2_6,
                    RecipeDatabase.Companion.MIGRATION3_6, RecipeDatabase.Companion.MIGRATION4_6,
                    RecipeDatabase.Companion.MIGRATION5_6,
            )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.Companion.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}