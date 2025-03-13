package de.malteans.recipes.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

@Entity(
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["ingredientId"]),
    ]
)
data class RecipeIngredientEntity(
    val recipeId: Long,
    val ingredientId: Long,
    val amount: Double?,
    val overrideUnit: String?,
)
