package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

fun migrate5_6(connection: SQLiteConnection) {
    // Add a new table for planed recipes
    connection.execSQL(
        """
        CREATE TABLE IF NOT EXISTS `PlanEntity` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `recipeId` INTEGER NOT NULL,
            `date` INTEGER NOT NULL,
            `timeOfDay` TEXT NOT NULL,
            FOREIGN KEY(`recipeId`) REFERENCES `RecipeEntity`(`id`) ON DELETE CASCADE ON UPDATE NO ACTION
        )
    """.trimIndent()
    )
    connection.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_PlanEntity_recipeId` ON `PlanEntity` (`recipeId`)"
    )

}

val RecipeDatabase.Companion.MIGRATION1_6: Migration
    get() = object : Migration(1, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate1_2(connection)
            migrate2_3(connection)
            migrate3_4(connection)
            migrate4_5(connection)
            migrate5_6(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION2_6: Migration
    get() = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            migrate2_3(connection)
            migrate3_4(connection)
            migrate4_5(connection)
            migrate5_6(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION3_6: Migration
    get() = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            migrate3_4(connection)
            migrate4_5(connection)
            migrate5_6(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION4_6: Migration
    get() = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            migrate4_5(connection)
            migrate5_6(connection)
        }
    }

val RecipeDatabase.Companion.MIGRATION5_6: Migration
    get() = object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            migrate5_6(connection)
        }
    }
