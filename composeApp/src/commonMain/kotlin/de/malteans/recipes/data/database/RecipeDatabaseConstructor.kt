package de.malteans.recipes.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object RecipeDatabaseConstructor: RoomDatabaseConstructor<RecipeDatabase> {
    override fun initialize(): RecipeDatabase
}