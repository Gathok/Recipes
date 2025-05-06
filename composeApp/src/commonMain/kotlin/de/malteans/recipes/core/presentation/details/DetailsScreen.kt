package de.malteans.recipes.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.presentation.components.CustomPlanDialog
import de.malteans.recipes.core.presentation.components.RatingBar
import de.malteans.recipes.core.presentation.details.components.CustomClockIcon
import de.malteans.recipes.core.presentation.details.components.CustomCloudDownloadIcon
import de.malteans.recipes.core.presentation.details.components.CustomDownloadDoneIcon
import de.malteans.recipes.core.presentation.details.components.CustomOpenInBrowserIcon
import de.malteans.recipes.core.presentation.details.components.ImageBackground
import de.malteans.recipes.core.presentation.details.components.getState
import de.malteans.recipes.core.presentation.details.components.isCloudOnly
import de.malteans.recipes.core.presentation.plan.components.toUiText
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.ingredients
import recipes.composeapp.generated.resources.min
import recipes.composeapp.generated.resources.online
import recipes.composeapp.generated.resources.preparation
import recipes.composeapp.generated.resources.servings
import recipes.composeapp.generated.resources.step

@Composable
fun DetailsScreenRoot(
    viewModel: DetailsViewModel = koinViewModel(),
    onBack: (Pair<Long, Long?>?) -> Unit,
    onEdit: (Long) -> Unit,
    onSave: () -> Unit,
    recipeId: Long?,
) {
    if (recipeId != null) viewModel.setRecipeId(recipeId)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.deleteFinished) {
        if (state.deleteFinished) onBack(null)
    }

    DetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DetailsAction.OnBack -> onBack(state.recipe?.let { it.id to it.cloudId })
                is DetailsAction.OnEdit -> onEdit(recipeId ?: state.recipe?.id
                    ?: throw IllegalStateException("recipeId and recipe are null"))
                is DetailsAction.OnSave -> {
                    viewModel.onAction(DetailsAction.OnSave)
                    onSave()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onAction: (DetailsAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val localClipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    if (state.showPlanDialog && state.recipe != null) {
        CustomPlanDialog(
            onDismiss = { onAction(DetailsAction.DismissPlanDialog) },
            onSubmit = { onAction(DetailsAction.OnPlan(it.date, it.timeOfDay)) },
            initialRecipe = state.recipe,
        )
    }

    val pagerState = rememberPagerState { 2 }
    val ingredientListState = rememberScrollState()
    val preparationListState = rememberScrollState()

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(DetailsAction.OnTabSelected(pagerState.currentPage))
    }

    LaunchedEffect(state.recipe) {
        if (state.recipe != null) {
            ingredientListState.scrollTo(0)
            preparationListState.scrollTo(0)
        }
    }

    // Dialog to disable UI
    AnimatedVisibility(
        visible = state.isDeleting,
        enter = fadeIn(animationSpec = tween(200)),
    ) {
        Dialog(
            onDismissRequest = {  },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            )
        ) { }
    }

    ImageBackground(
        imageUrl = state.recipe?.imageUrl,
        onBackClick = { onAction(DetailsAction.OnBack) },
        rightIcons = @Composable {
            if (state.recipe?.sourceUrl != null && state.recipe.sourceUrl.isNotBlank() && state.recipe.sourceUrl.startsWith("https://")) {
                IconButton(
                    onClick = { uriHandler.openUri(state.recipe.sourceUrl) },
                ) {
                    Icon(
                        imageVector = CustomOpenInBrowserIcon,
                        contentDescription = "Open Source",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (state.recipe?.isCloudOnly() == false) {
                val deleteClicked = remember { mutableStateOf(false) }
                LaunchedEffect(deleteClicked.value) {
                    if (deleteClicked.value) {
                        delay(3000)
                        deleteClicked.value = false
                    }
                }
                IconButton(
                    onClick = { onAction(DetailsAction.OnEdit) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Recipe",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = {
                        if (!deleteClicked.value) {
                            deleteClicked.value = true
                        } else {
                            onAction(DetailsAction.OnDelete)
                            deleteClicked.value = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Recipe",
                        tint = if (deleteClicked.value) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            AnimatedVisibility(
                visible = state.recipe?.isCloudOnly() == true,
                enter = EnterTransition.None,
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(
                        delayMillis = 2000,
                        durationMillis = 300,
                    )
                ),
            ) {
                IconButton(
                    onClick = { onAction(DetailsAction.OnSave) }
                ) {
                    Icon(
                        imageVector = if (state.recipe?.isCloudOnly() == true) CustomCloudDownloadIcon
                            else CustomDownloadDoneIcon,
                        contentDescription = "Save Recipe locally",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
    ) { enableScrolling, dragProgress ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
        ) {
            state.recipe?.let { recipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
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
                        Text(
                            text = recipe.getState().uiText.asString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (recipe.description.isNotBlank()) {
                            Text(
                                text = recipe.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (recipe.sourceUrl != null && recipe.sourceUrl.isNotBlank() && recipe.sourceUrl.startsWith("https://")) {
                            Icon(
                                imageVector = CustomOpenInBrowserIcon,
                                contentDescription = "Open Source",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .combinedClickable (
                                        onClick = { uriHandler.openUri(recipe.sourceUrl) },
                                        onLongClick = { // copy sourceUrl to clipboard
                                            localClipboardManager.setText(
                                                AnnotatedString(recipe.sourceUrl)
                                            )
                                        }
                                    )
                                    .size(28.dp)
                            )
                        }
                        if (recipe.isCloudOnly()) {
                            Icon(
                                imageVector = CustomCloudDownloadIcon,
                                contentDescription = "Save Recipe locally",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.OnSave) }
                                    .size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Recipe",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.OnEdit) }
                                    .size(28.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Plan Recipe",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.ShowPlanDialog) }
                                    .size(28.dp)
                            )
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

                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row {
                                RatingBar(recipe)
                                Text(
                                    text = if (recipe.rating == null && recipe.onlineRating != null)
                                        "(${stringResource(Res.string.online)})"
                                    else
                                        "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "${stringResource(Res.string.servings)}: ${recipe.servings ?: "–"}",
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
                            Column(
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
                                    text = if (recipe.totalTime != null) "${recipe.totalTime} ${
                                        stringResource(
                                            Res.string.min
                                        )
                                    }"
                                    else "–",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    TabRow(
                        selectedTabIndex = state.selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                            )
                        },
                    ) {
                        Tab(
                            selected = state.selectedTabIndex == 0,
                            onClick = { onAction(DetailsAction.OnTabSelected(0)) },
                            modifier = Modifier
                                .weight(1f),
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(Res.string.ingredients),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 4.dp,
                                    )
                            )
                        }
                        Tab(
                            selected = state.selectedTabIndex == 1,
                            onClick = { onAction(DetailsAction.OnTabSelected(1)) },
                            modifier = Modifier
                                .weight(1f),
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(Res.string.preparation),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 4.dp,
                                    )
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            when (pageIndex) {
                                0 -> { // Ingredients
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(
                                                state = ingredientListState,
                                                enabled = enableScrolling,
                                            )
                                    ) {
                                        recipe.ingredients.forEach { (ingredient, amount, overrideUnit) ->
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
                                }
                                1 -> { // Preparation
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(
                                                state = preparationListState,
                                                enabled = enableScrolling,
                                            )
                                    ) {
                                        recipe.steps.forEachIndexed { index, step ->
                                            Text(
                                                text = stringResource(Res.string.step, index + 1),
                                                style = MaterialTheme.typography.titleSmall,
                                            )
                                            Text(
                                                text = step,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            if (index != recipe.steps.size - 1) { // Don't show divider after last step
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp)
                                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                                ){
                                                    Spacer(modifier = Modifier.height(1.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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

@Composable
fun LocalDate.asString(): String {
    return "${this.dayOfWeek.toUiText().asString()}, " +
            "${this.dayOfMonth.toString().padStart(2, '0')}.${this.monthNumber.toString().padStart(2, '0')}.${this.year}"
}