package de.malteans.recipes.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_2
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_3
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_3

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(RecipeDatabase.DB_NAME)
        return Room.databaseBuilder<RecipeDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .addMigrations(
                RecipeDatabase.Companion.MIGRATION1_2,
                RecipeDatabase.Companion.MIGRATION1_3, RecipeDatabase.Companion.MIGRATION2_3
            )
    }
}