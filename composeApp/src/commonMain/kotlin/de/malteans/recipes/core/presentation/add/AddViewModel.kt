package de.malteans.recipes.core.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeIngredientItem
import de.malteans.recipes.core.domain.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _allIngredients = repository
        .getAllIngredients()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _state = MutableStateFlow(AddState())

    val state = combine(
        _state,
        _allIngredients
    ) { state, allIngredients ->
        state.copy(
            allIngredients = allIngredients
        )
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    suspend fun onRecipeAdd(): Long {
        var recipe = _state.value.editingRecipe
            ?: Recipe()
        recipe = recipe.copy (
            name = _state.value.name,
            description = _state.value.description,
            imageUrl = _state.value.imageUrl,
            ingredients = _state.value.ingredients.map { RecipeIngredientItem(it.key, it.value.first, it.value.second) },
            steps = _state.value.steps,
            servings = _state.value.servings,
            workTime = _state.value.workTime,
            totalTime = _state.value.totalTime,
            rating = _state.value.rating,
        )
        return repository.upsertRecipe(recipe)
    }

    fun loadRecipeForEditing(recipeId: Long) {
        if (recipeId == _state.value.editingRecipe?.id && _state.value.editingRecipe?.id != 0L)
            return
        viewModelScope.launch {
            val recipe = repository.getRecipeById(recipeId).first()
            _state.update {
                it.copy(
                    editingRecipe = recipe,
                    name = recipe.name,
                    description = recipe.description,
                    imageUrl = recipe.imageUrl,
                    ingredients = recipe.ingredients.associate { it.ingredient to (it.amount to it.overrideUnit) },
                    steps = recipe.steps,
                    workTime = recipe.workTime,
                    totalTime = recipe.totalTime,
                    servings = recipe.servings,
                    rating = recipe.rating,
                )
            }
        }
    }

    fun onAction(action: AddAction) {
        when (action) {
            is AddAction.OnNameChange -> {
                _state.update {
                    it.copy(name = action.name)
                }
            }
            is AddAction.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = action.description)
                }
            }
            is AddAction.OnImageUrlChange -> {
                _state.update {
                    it.copy(imageUrl = action.imageUrl)
                }
            }
            is AddAction.OnIngredientCreate -> {
                viewModelScope.launch {
                    repository.upsertIngredient(action.ingredient)
                }
            }
            is AddAction.OnIngredientAdd -> {
                _state.update {
                    it.copy(
                        showIngredientDialog = true,
                        currentIngredient = action.ingredient,
                    )
                }
            }
            is AddAction.OnIngredientEdit -> {
                _state.update {
                    it.copy(
                        showIngredientDialog = true,
                        currentIngredient = action.ingredient,
                        isEditingIngredient = true,
                    )
                }
            }
            is AddAction.OnIngredientChange -> {
                _state.update {
                    val newIngredients = it.ingredients.toMutableMap()
                    var ingredient = action.ingredient
                    if (ingredient.id == 0L) {
                        ingredient = _allIngredients.value.first { it.name == ingredient.name }
                    }
                    newIngredients[ingredient] = Pair(action.amount, action.overrideUnit)
                    it.copy(
                        ingredients = newIngredients
                    )
                }
            }
            is AddAction.OnIngredientRemove -> {
                _state.update {
                    val newIngredients = it.ingredients.toMutableMap()
                    newIngredients.remove(action.ingredient)
                    it.copy(
                        ingredients = newIngredients
                    )
                }
            }
            is AddAction.OnRatingChange -> {
                _state.update {
                    it.copy(rating = action.rating)
                }
            }
            is AddAction.OnServingsChange -> {
                _state.update {
                    it.copy(servings = action.servings)
                }
            }
            is AddAction.OnStepAdd -> {
                _state.update {
                    val newSteps = it.steps.toMutableList()
                    newSteps.add(action.index, "")
                    it.copy(
                        steps = newSteps
                    )
                }
            }
            is AddAction.OnStepChange -> {
                _state.update {
                    val newSteps = it.steps.toMutableList()
                    newSteps[action.index] = action.newValue
                    it.copy(
                        steps = newSteps
                    )
                }
            }
            is AddAction.OnStepRemove -> {
                _state.update {
                    val newSteps = it.steps.toMutableList()
                    newSteps.removeAt(action.index)
                    it.copy(
                        steps = newSteps
                    )
                }
            }
            is AddAction.OnTotalTimeChange -> {
                _state.update {
                    it.copy(totalTime = action.time)
                }
            }
            is AddAction.OnWorkTimeChange -> {
                _state.update {
                    it.copy(workTime = action.time)
                }
            }
            is AddAction.OnTabSelect -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
            AddAction.OnClear -> {
                _state.update {
                    AddState()
                }
            }
            AddAction.OnIngredientDialogDismiss -> {
                _state.update {
                    it.copy(
                        showIngredientDialog = false,
                        currentIngredient = null
                    )
                }
            }
            else -> throw IllegalArgumentException("ViewModel does not support this action: $action")
        }
    }
}