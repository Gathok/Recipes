package de.malteans.recipes.data.mappers

import de.malteans.recipes.data.database.entities.IngredientEntity
import de.malteans.recipes.domain.Ingredient

// Mapping from IngredientEntity to domain Ingredient.
fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        unit = this.unit
    )
}

fun Ingredient.toIngredientEntity(): IngredientEntity {
    return IngredientEntity(
        id = this.id,
        name = this.name,
        unit = this.unit
    )
}