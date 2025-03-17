package de.malteans.recipes.core.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.presentation.add.components.IngredientDropdown
import de.malteans.recipes.core.presentation.add.components.IngredientListItem
import de.malteans.recipes.core.presentation.components.CustomDialog
import de.malteans.recipes.core.presentation.components.SnackbarManager
import de.malteans.recipes.core.domain.Ingredient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.add_dialog_title
import recipes.composeapp.generated.resources.add_ingredient
import recipes.composeapp.generated.resources.add_recipe
import recipes.composeapp.generated.resources.amount
import recipes.composeapp.generated.resources.description
import recipes.composeapp.generated.resources.edit_dialog_title
import recipes.composeapp.generated.resources.edit_recipe
import recipes.composeapp.generated.resources.general
import recipes.composeapp.generated.resources.ingredients
import recipes.composeapp.generated.resources.min
import recipes.composeapp.generated.resources.name
import recipes.composeapp.generated.resources.no_ingredients
import recipes.composeapp.generated.resources.preparation
import recipes.composeapp.generated.resources.recipe_added_snackbar
import recipes.composeapp.generated.resources.show
import recipes.composeapp.generated.resources.step
import recipes.composeapp.generated.resources.total_time
import recipes.composeapp.generated.resources.unit
import recipes.composeapp.generated.resources.work_time

@Composable
fun AddScreenRoot(
    viewModel: AddViewModel = koinViewModel(),
    onRecipeShow: (Long) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AddAction.OnRecipeShow -> onRecipeShow(action.id)
                else -> viewModel.onAction(action)
            }
        },
        onRecipeAdd = { viewModel.onRecipeAdd() },
    )
}

@Composable
fun AddScreen(
    state: AddState,
    onAction: (AddAction) -> Unit,
    onRecipeAdd: suspend () -> Long,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var validToAdd by remember { mutableStateOf(false) }

    LaunchedEffect(state.name) {
        validToAdd = state.name.isNotBlank()
    }

    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { (currentPage, isScrolling) ->
                if (!isScrolling) {
                    onAction(AddAction.OnTabSelect(currentPage))
                }
            }
    }

    if (state.showIngredientDialog) {
        var amount by remember { mutableStateOf(state.ingredients[state.currentIngredient!!]?.first?.toString() ?: "") }
        var unit by remember { mutableStateOf(state.ingredients[state.currentIngredient!!]?.second ?: state.currentIngredient.unit) }

        var isValid by remember { mutableStateOf(false) }

        var onEndRequest: (Boolean) -> Unit = { valid ->
            if (valid) {
                onAction(AddAction.OnIngredientDialogDismiss)
                onAction(AddAction.OnIngredientChange(state.currentIngredient!!,
                    if (amount.isBlank()) null else amount.toDouble(), unit))
            }
        }

        LaunchedEffect(amount, unit) {
            isValid = unit.isNotBlank() && (amount.isBlank() || (amount.toDoubleOrNull() != null && amount.toDouble() > 0))
        }

        CustomDialog(
            title = { Text(
                text = stringResource(
                    if (state.isEditingIngredient) Res.string.edit_dialog_title
                        else Res.string.add_dialog_title,
                    state.currentIngredient!!.name),
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            ) },
            rightIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = if (isValid) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable { onEndRequest(isValid) }
                )
            },
            onDismissRequest = {
                onAction(AddAction.OnIngredientDialogDismiss)
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                        },
                        singleLine = true,
                        label = {
                            Text(stringResource(Res.string.amount))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onEndRequest(isValid)
                            }
                        ),
                        isError = !(amount.isBlank() || (amount.toDoubleOrNull() != null && amount.toDouble() > 0))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {
                            unit = it
                        },
                        singleLine = true,
                        label = {
                            Text(stringResource(Res.string.unit))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        isError = unit.isBlank(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onEndRequest(isValid)
                            }
                        ),
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                if (state.editingRecipeId == null) { // Is Add
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear the form",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable {
                                onAction(AddAction.OnClear)
                                focusManager.clearFocus()
                            }
                    )
                } else { // Is Edit
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back without saving",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable {
                                onAction(AddAction.OnRecipeShow(state.editingRecipeId))
                                focusManager.clearFocus()
                            }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if(state.editingRecipeId != null) stringResource(Res.string.edit_recipe)
                        else stringResource(Res.string.add_recipe),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                val resMessage = stringResource(Res.string.recipe_added_snackbar, "%NAME%")
                val resShow = stringResource(Res.string.show)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Add the recipe",
                    tint = if (validToAdd) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable {
                            if (validToAdd) {
                                focusManager.clearFocus()
                                scope.launch {
                                    val id = onRecipeAdd()
                                    onAction(AddAction.OnClear)
                                    if (state.editingRecipeId != null) {
                                        onAction(AddAction.OnRecipeShow(id))
                                    } else {
                                        SnackbarManager.showSnackbar(
                                            message = resMessage.replace("%NAME%", state.name),
                                            actionLabel = resShow,
                                            onAction = { onAction(AddAction.OnRecipeShow(id)) },
                                            duration = SnackbarDuration.Long,
                                            withDismissAction = true,
                                        )
                                    }
                                }
                            }
                        }
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth(),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = {
                            onAction(AddAction.OnTabSelect(0))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.general),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            onAction(AddAction.OnTabSelect(1))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.ingredients),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 2,
                        onClick = {
                            onAction(AddAction.OnTabSelect(2))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.preparation),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {pageIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (pageIndex) {
                            0 -> { // General -----------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxSize()
                                ) {
                                    // Name -------------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = state.name,
                                            onValueChange = {
                                                onAction(AddAction.OnNameChange(it))
                                            },
                                            singleLine = true,
                                            label = {
                                                Text(stringResource(Res.string.name))
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                imeAction = ImeAction.Next
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            )
                                        )
                                    }
                                    // Description ------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = state.description,
                                            onValueChange = {
                                                onAction(AddAction.OnDescriptionChange(it))
                                            },
                                            label = {
                                                Text(stringResource(Res.string.description))
                                            },
                                            minLines = 3,
                                            maxLines = 5,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next,
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            )
                                        )
                                    }
                                    // Times ------------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.workTime?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if (int != null || str.isBlank()) {
                                                        onAction(AddAction.OnWorkTimeChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.work_time))
                                                },
                                                suffix = {
                                                    Text(stringResource(Res.string.min))
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.moveFocus(FocusDirection.Right)
                                                    }
                                                ),
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.totalTime?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if (int != null || str.isBlank()) {
                                                        onAction(AddAction.OnTotalTimeChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.total_time))
                                                },
                                                suffix = {
                                                    Text(stringResource(Res.string.min))
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.clearFocus()
                                                        onAction(AddAction.OnTabSelect(1))
                                                    }
                                                ),
                                            )
                                        }
                                    }
                                    // Picture Link -----------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        OutlinedTextField(
                                            value = state.imageUrl,
                                            onValueChange = {
                                                onAction(AddAction.OnImageUrlChange(it))
                                            },
                                            singleLine = true,
                                            label = {
                                                Text("Image URL")
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Uri,
                                                imeAction = ImeAction.Next
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.clearFocus()
                                                    onAction(AddAction.OnTabSelect(1))
                                                }
                                            ),
                                        )
                                    }
                                }
                            }
                            1 -> { // Ingredients -------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize()
                                ) {
                                    IngredientDropdown(
                                        options = state.allIngredients.associate { it to it.name },
                                        selectedOption = Pair("", ""),
                                        onValueChanged = {
                                            val ingredient = it as Ingredient
                                            onAction(AddAction.OnIngredientAdd(ingredient))
                                        },
                                        onValueAdded = {
                                            val ingredient = Ingredient(
                                                name = it,
                                                unit = "g",
                                            )
                                            onAction(AddAction.OnIngredientCreate(ingredient))
                                            onAction(AddAction.OnIngredientAdd(ingredient))
                                        },
                                        label = stringResource(Res.string.add_ingredient) + "…",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    if (state.ingredients.isNotEmpty()) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .padding(top = 12.dp)
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            items(state.ingredients.map { Triple(it.key, it.value.first, it.value.second) })
                                            { triple ->
                                                val ingredient = triple.first
                                                val amount = triple.second
                                                val unit = if (triple.third != null && triple.third != ingredient.unit) triple.third
                                                    else ingredient.unit
                                                IngredientListItem(
                                                    ingredient = ingredient,
                                                    amount = amount,
                                                    unit = unit,
                                                    onEdit = {
                                                        onAction(AddAction.OnIngredientEdit(ingredient))
                                                    },
                                                    onDelete = {
                                                        onAction(AddAction.OnIngredientRemove(ingredient))
                                                    }
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(Res.string.no_ingredients),
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(top = 16.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            2 -> { // Preparation -------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxSize()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        item {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp)
                                                    .clickable {
                                                        onAction(AddAction.OnStepAdd(0))
                                                    },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add step",
                                                )
                                            }
                                        }
                                        items(state.steps.size) { index ->
                                            val deleteClicked = remember { mutableStateOf(false) }
                                            LaunchedEffect(state.steps[index]) {
                                                deleteClicked.value = false
                                            }
                                            LaunchedEffect(deleteClicked.value) {
                                                if (deleteClicked.value) {
                                                    delay(3000)
                                                    deleteClicked.value = false
                                                }
                                            }

                                            Row (
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    OutlinedTextField(
                                                        value = state.steps[index],
                                                        onValueChange = {
                                                            onAction(AddAction.OnStepChange(
                                                                index = index,
                                                                newValue = it
                                                            ))
                                                        },
                                                        label = {
                                                            Text(stringResource(Res.string.step, index + 1))
                                                        },
                                                        minLines = 3,
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                    )
                                                }
                                                Column (
                                                    modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .clickable {
                                                            if (state.steps.size > 1) {
                                                                if (!deleteClicked.value) {
                                                                    deleteClicked.value = true
                                                                } else {
                                                                    onAction(AddAction.OnStepRemove(index))
                                                                    deleteClicked.value = false
                                                                }
                                                            }
                                                        },
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Remove step",
                                                        tint = if (state.steps.size > 1 && deleteClicked.value) MaterialTheme.colorScheme.error
                                                            else if (state.steps.size > 1) MaterialTheme.colorScheme.onSurface
                                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                    )
                                                }
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp)
                                                    .clickable {
                                                        onAction(AddAction.OnStepAdd(index + 1))
                                                    },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add step",
                                                    tint = MaterialTheme.colorScheme.onSurface,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

