package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

fun migrate2_3(connection: SQLiteConnection) {
    // Add a new nullable column "cloudId" to store the cloud recipe id.
    connection.execSQL("ALTER TABLE RecipeEntity ADD COLUMN cloudId INTEGER")
}

val RecipeDatabase.Companion.MIGRATION1_3: Migration
    get() = object : Migration(1, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate1_2(connection)
            migrate2_3(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION2_3: Migration
    get() = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate2_3(connection)
        }
    }
