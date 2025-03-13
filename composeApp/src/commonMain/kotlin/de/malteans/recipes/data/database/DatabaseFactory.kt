package de.malteans.recipes.data.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<RecipeDatabase>
}