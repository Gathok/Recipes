package de.malteans.recipes.core.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipeId = MutableStateFlow<Long?>(null)

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)

    private val _recipe = combine(
        _recipeId,
        _selectedRecipe,
    ) { recipeId, selectedRecipe ->
        recipeId?.let { repository.getRecipeById(it) }
            ?: if (selectedRecipe != null && selectedRecipe.id != 0L) {
                _recipeId.value = selectedRecipe.id
                repository.getRecipeById(selectedRecipe.id)
            } else flowOf(selectedRecipe)
    }
        .flatMapLatest { it }

    private val _state = MutableStateFlow(DetailsState())

    val state = combine(
        _state,
        _recipe,
    ) { state, recipe ->

        state.copy(
            recipe = recipe,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsState()
    )

    fun setRecipeId(recipeId: Long) {
        _selectedRecipe.value = null
        _recipeId.value = recipeId
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            is DetailsAction.OnDelete -> {
                _state.update { it.copy(
                    isDeleting = true,
                ) }
                _selectedRecipe.update { state.value.recipe?.copy(id = 0L) }
                val recipeId = _recipeId.value ?: throw IllegalStateException("No recipeId set, for deletion")
                _recipeId.update { null }
                viewModelScope.launch {
                    repository.deleteRecipeById(recipeId)
                    _state.update { it.copy(
                        deleteFinished = true,
                    ) }
                }
            }
            is DetailsAction.OnSelectedRecipeChange -> {
                if (action.recipe.id != 0L) {
                    _selectedRecipe.value = null
                    _recipeId.value = action.recipe.id
                }
                else {
                    _recipeId.value = null
                    _selectedRecipe.value = action.recipe
                }
            }
            is DetailsAction.OnSave -> {
                val recipe = state.value.recipe ?: return
                viewModelScope.launch {
                    val id = repository.saveCloudRecipe(recipe)
                    setRecipeId(id)
                }
            }
            is DetailsAction.ShowPlanDialog -> {
                _state.update { it.copy(
                    showPlanDialog = true
                ) }
            }
            is DetailsAction.DismissPlanDialog -> {
                _state.update { it.copy(
                    showPlanDialog = false
                ) }
            }
            is DetailsAction.OnPlan -> {
                val recipe = state.value.recipe ?: return
                viewModelScope.launch {
                    repository.planRecipe(PlannedRecipe(
                        recipe = recipe,
                        date = action.date,
                        timeOfDay = action.timeOfDay,
                    ))
                }
            }

            is DetailsAction.OnTabSelected -> {
                _state.update { it.copy(
                    selectedTabIndex = action.index
                ) }
            }

            is DetailsAction.CustomServings -> {
                _state.update { it.copy(
                    customServings = action.servings
                ) }
            }
            else -> throw IllegalArgumentException("Action not implemented in ViewModel: $action")
        }
    }
}