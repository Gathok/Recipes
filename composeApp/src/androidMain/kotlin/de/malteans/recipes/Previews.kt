package de.malteans.recipes

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.details.DetailsScreen
import de.malteans.recipes.core.presentation.details.DetailsState
import de.malteans.recipes.core.presentation.search.components.RecipeListItem
import de.malteans.recipes.theme.RecipesTheme

val recipe = Recipe(
    name = "Spaghetti Carbonara",
    description = "A delicious pasta dish",
    imageUrl = "https://test.com",
    ingredients = mapOf(
        Ingredient(name = "Spaghetti", unit = "g") to Pair(200.0, null),
        Ingredient(name = "Bacon", unit = "g") to Pair(100.0, null),
        Ingredient(name = "Egg", unit = "Stück") to Pair(2.0, null),
        Ingredient(name = "Parmesan", unit = "g") to Pair(50.0, null),
        Ingredient(name = "Pepper", unit = "g") to Pair(2.0, null)
    ),
    steps = listOf(
        "Cook the spaghetti",
        "Fry the bacon",
        "Mix everything together"
    ),
    workTime = 35,
    totalTime = 35,
    servings = null,
    rating = 3
)

@Preview
@Composable
private fun DetailsScreenPreview() {
    RecipesTheme {
        DetailsScreen(
            state = DetailsState(
                recipe = recipe
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun RecipeListItemPreview() {
    RecipesTheme {
        RecipeListItem(
            recipe = recipe,
            onClick = {},
            onLongClick = {},
        )
    }
}