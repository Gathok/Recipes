package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

fun migrate3_4(connection: SQLiteConnection) {
    // Add a new nullable column "cloudId" to store the cloud recipe id.
    connection.execSQL("ALTER TABLE RecipeEntity ADD COLUMN editedFromCloud INTEGER DEFAULT 0 NOT NULL")
}

val RecipeDatabase.Companion.MIGRATION1_4: Migration
    get() = object : Migration(1, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate1_2(connection)
            migrate2_3(connection)
            migrate3_4(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION2_4: Migration
    get() = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate2_3(connection)
            migrate3_4(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION3_4: Migration
    get() = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            migrate3_4(connection)
        }
    }
