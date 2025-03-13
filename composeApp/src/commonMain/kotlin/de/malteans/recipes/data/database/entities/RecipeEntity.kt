package de.malteans.recipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val imageUrl: String,
    val workTime: Int?,
    val totalTime: Int?,
    val servings: Int?,
    val rating: Int?,
)