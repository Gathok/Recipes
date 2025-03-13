package de.malteans.recipes.domain

data class Recipe(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val imageUrl: String,
    val ingredients: Map<Ingredient, Pair<Double?, String?>>, // Map<Ingredient, Pair<Amount?, OverrideUnit?>>
    val steps: List<String>,
    val workTime: Int?,
    val totalTime: Int?,
    val servings: Int?,
    val rating: Int?,
)