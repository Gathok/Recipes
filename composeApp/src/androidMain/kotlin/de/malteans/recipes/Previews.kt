package de.malteans.recipes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeIngredientItem
import de.malteans.recipes.core.presentation.details.DetailsScreen
import de.malteans.recipes.core.presentation.details.DetailsState
import de.malteans.recipes.core.presentation.plan.components.PlannedDayData
import de.malteans.recipes.core.presentation.plan.components.PlannedDayItem
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import de.malteans.recipes.core.presentation.search.components.RecipeListItem
import de.malteans.recipes.theme.RecipesTheme
import kotlinx.datetime.LocalDateTime

val recipe = Recipe(
    name = "Spaghetti Carbonara",
    description = "A delicious pasta dish",
    imageUrl = "https://test.com",
    ingredients = listOf(
        RecipeIngredientItem(Ingredient(name = "Spaghetti", unit = "g"), 200.0, null),
        RecipeIngredientItem(Ingredient(name = "Bacon", unit = "g"), 100.0, null),
        RecipeIngredientItem(Ingredient(name = "Egg", unit = "Stück"), 2.0, null),
        RecipeIngredientItem(Ingredient(name = "Parmesan", unit = "g"), 50.0, null),
        RecipeIngredientItem(Ingredient(name = "Pepper", unit = "g"), 2.0, null)
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

@Preview
@Composable
private fun PlannedDayItemPreview() {
    RecipesTheme(
        useDarkTheme = true
    ) {
        var isExpanded by remember { mutableStateOf(true) }

        val date = LocalDateTime(2025, 4, 1, 0, 0)
            .date
        PlannedDayItem(
            data = PlannedDayData(
                date = date,
                recipes = listOf(PlannedRecipe(
                    recipe = recipe,
                    date = date,
                    timeOfDay = TimeOfDay.LUNCH
                )),
                isExpanded = isExpanded
            ),
            onRecipeShow = { },
            onEditPlan = { },
            onExpand = { isExpanded = it },
        )
    }
}