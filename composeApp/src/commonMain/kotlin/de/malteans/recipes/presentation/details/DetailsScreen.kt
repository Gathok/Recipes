package de.malteans.recipes.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.presentation.details.components.CustomClockIcon
import de.malteans.recipes.presentation.details.components.ImageBackground
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.ingredients
import recipes.composeapp.generated.resources.min
import recipes.composeapp.generated.resources.preparation

@Composable
fun DetailsScreenRoot(
    viewModel: DetailsViewModel = koinViewModel(),
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    recipeId: Long,
) {
    viewModel.setRecipeId(recipeId)
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                DetailsAction.OnBack -> onBack()
                DetailsAction.OnEdit -> onEdit(recipeId)
                DetailsAction.OnDelete -> {
                    onBack()
                    viewModel.onAction(DetailsAction.OnDelete)
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun DetailsScreen(
    state: DetailsState,
    onAction: (DetailsAction) -> Unit
) {
    ImageBackground(
        imageUrl = state.recipe?.imageUrl,
        onBackClick = { onAction(DetailsAction.OnBack) },
        onEditClick = { onAction(DetailsAction.OnEdit) },
        onDeleteClick = { onAction(DetailsAction.OnDelete) }
    ) { enableScrolling, dragProgress ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
        ) {
            state.recipe?.let { recipe ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (recipe.description.isNotBlank()) {
                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f + 0.4f * dragProgress)
                        ),
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                val scrollState = rememberScrollState()

                LaunchedEffect(enableScrolling) {
                    if (enableScrolling) {
                        scrollState.animateScrollTo(0)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .verticalScroll(
                            state = scrollState,
                            enabled = enableScrolling,
                        ),
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "Rating: ${recipe.rating}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Servings: ${recipe.servings}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CustomClockIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(42.dp)
                            )
                            Column (
                                modifier = Modifier
                                    .padding(start = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (recipe.workTime != null && recipe.workTime != recipe.totalTime) {
                                    Text(
                                        text = "${recipe.workTime} ${stringResource(Res.string.min)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = if (recipe.totalTime != null) "${recipe.totalTime} ${stringResource(Res.string.min)}"
                                    else "–",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(Res.string.ingredients),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        recipe.ingredients.forEach { (ingredient, amountPair) ->
                            val (amount, overrideUnit) = amountPair
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(0.3f)
                                ) {
                                    Text(
                                        text = "${amount.toNiceString()}${overrideUnit ?: ingredient.unit}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(0.7f)
                                ) {
                                    Text(
                                        text = ingredient.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(Res.string.preparation),
                            style = MaterialTheme.typography.titleMedium
                        )
                        recipe.steps.forEachIndexed { index, step ->
                            Text(
                                text = "${index + 1}. $step",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            } ?: run {
                Text("Recipe not found", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun Double?.toNiceString(): String {
    if (this == null) return ""
    if (this % 1.0 == 0.0) {
        return "${this.toInt()} "
    }
    return "$this "
}
