package de.malteans.recipes.core.presentation.add

import de.malteans.recipes.core.domain.Ingredient

data class AddState(
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: Map<Ingredient, Pair<Double?, String?>> = emptyMap(),
    val steps: List<String> = listOf(""),
    val workTime: Int? = null,
    val totalTime: Int? = null,
    val servings: Int? = null,
    val rating: Int? = null,

    val selectedTabIndex: Int = 0,
    val showIngredientDialog: Boolean = false,
    val currentIngredient: Ingredient? = null,
    val isEditingIngredient: Boolean = false,

    val allIngredients: List<Ingredient> = emptyList(),

    // Fields for Editing
    val editingRecipeId: Long? = null,
    val cloudId: Long? = null,
)
