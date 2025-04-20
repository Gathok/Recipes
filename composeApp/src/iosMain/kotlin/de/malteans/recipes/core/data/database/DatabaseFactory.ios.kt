package de.malteans.recipes.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_2
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_3
import de.malteans.recipes.core.data.database.migrations.MIGRATION3_4
import de.malteans.recipes.core.data.database.migrations.MIGRATION4_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION5_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION6_7
import de.malteans.recipes.core.data.database.migrations.MIGRATION7_8
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
                RecipeDatabase.Companion.MIGRATION2_3,
                RecipeDatabase.Companion.MIGRATION3_4,
                RecipeDatabase.Companion.MIGRATION4_5,
                RecipeDatabase.Companion.MIGRATION5_6,
                RecipeDatabase.Companion.MIGRATION6_7,
                RecipeDatabase.Companion.MIGRATION7_8,
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