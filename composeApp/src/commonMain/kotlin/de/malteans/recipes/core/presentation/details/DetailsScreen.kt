package de.malteans.recipes.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.details.components.CustomClockIcon
import de.malteans.recipes.core.presentation.details.components.CustomCloudDownloadIcon
import de.malteans.recipes.core.presentation.details.components.CustomDownloadDoneIcon
import de.malteans.recipes.core.presentation.details.components.ImageBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.cloud_recipe
import recipes.composeapp.generated.resources.edited_cloud_recipe
import recipes.composeapp.generated.resources.ingredients
import recipes.composeapp.generated.resources.local_recipe
import recipes.composeapp.generated.resources.min
import recipes.composeapp.generated.resources.preparation
import recipes.composeapp.generated.resources.saved_cloud_recipe

@Composable
fun DetailsScreenRoot(
    viewModel: DetailsViewModel = koinViewModel(),
    onBack: (Boolean) -> Unit,
    onEdit: (Long) -> Unit,
    onSave: () -> Unit,
    recipeId: Long?,
) {
    if (recipeId != null) viewModel.setRecipeId(recipeId)
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DetailsAction.OnBack -> onBack(action.onCloudRecipe)
                DetailsAction.OnEdit -> onEdit(recipeId ?: state.recipe?.id
                    ?: throw IllegalStateException("recipeId and recipe are null"))
                DetailsAction.OnDelete -> {
                    onBack(false)
                    viewModel.onAction(DetailsAction.OnDelete)
                }
                is DetailsAction.OnSave -> {
                    viewModel.onAction(DetailsAction.OnSave)
                    onSave()
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
    val scope = rememberCoroutineScope()

    ImageBackground(
        imageUrl = state.recipe?.imageUrl,
        onBackClick = { onAction(DetailsAction.OnBack(state.recipe?.isCloudOnly() == true)) },
        onEditClick = if ((state.recipe?.id ?: 0L) == 0L) null
            else { { onAction(DetailsAction.OnEdit) } },
        onDeleteClick = if ((state.recipe?.id ?: 0L) == 0L) null
            else { { onAction(DetailsAction.OnDelete) } }
    ) { enableScrolling, dragProgress ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
        ) {
            state.recipe?.let { recipe ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
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
                        Text(
                            text = if (recipe.isCloudOnly()) stringResource(Res.string.cloud_recipe)
                                else if (recipe.isLocalOnly()) stringResource(Res.string.local_recipe)
                                else if (recipe.editedFromCloud) stringResource(Res.string.edited_cloud_recipe)
                                else stringResource(Res.string.saved_cloud_recipe),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    AnimatedVisibility(
                        visible = recipe.isCloudOnly(),
                        enter = EnterTransition.None,
                        exit = fadeOut(animationSpec = tween(
                            delayMillis = 3000,
                            durationMillis = 500
                        ))
                    ) {
                        Column {
                            IconButton( onClick = { onAction(DetailsAction.OnSave) } ) {
                                Icon(
                                    imageVector = if (recipe.isCloudOnly()) CustomCloudDownloadIcon
                                        else CustomDownloadDoneIcon,
                                    contentDescription = "Save cloud recipe locally",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(32.dp)
                                )
                            }
                        }
                    }
                }
                // -------------------------------------------
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

fun Recipe.isCloudOnly(): Boolean {
    return this.id == 0L && this.cloudId != null
}
fun Recipe.isLocalOnly(): Boolean {
    return this.id != 0L && this.cloudId == null
}
