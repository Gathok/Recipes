package de.malteans.recipes.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.MIGRATION1_2
import de.malteans.recipes.core.data.database.migrations.MIGRATION2_3
import de.malteans.recipes.core.data.database.migrations.MIGRATION3_4
import de.malteans.recipes.core.data.database.migrations.MIGRATION4_5
import de.malteans.recipes.core.data.database.migrations.MIGRATION5_6
import de.malteans.recipes.core.data.database.migrations.MIGRATION6_7
import de.malteans.recipes.core.data.database.migrations.MIGRATION7_8

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
                RecipeDatabase.Companion.MIGRATION2_3,
                RecipeDatabase.Companion.MIGRATION3_4,
                RecipeDatabase.Companion.MIGRATION4_5,
                RecipeDatabase.Companion.MIGRATION5_6,
                RecipeDatabase.Companion.MIGRATION6_7,
                RecipeDatabase.Companion.MIGRATION7_8,
            )
    }
}