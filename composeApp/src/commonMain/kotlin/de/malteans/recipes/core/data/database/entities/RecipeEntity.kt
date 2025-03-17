package de.malteans.recipes.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cloudId: Long? = null, // New field: holds the cloud recipe id if available
    val editedFromCloud: Boolean = false,
    val name: String,
    val description: String,
    val imageUrl: String,
    val workTime: Int?,
    val totalTime: Int?,
    val servings: Int?,
    val rating: Int?,
)
