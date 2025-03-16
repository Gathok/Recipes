package de.malteans.recipes.core.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipeId = MutableStateFlow(0L)

    private val _recipe = _recipeId
        .flatMapLatest { recipeId ->
            if (recipeId == 0L) flowOf(null)
            else repository.getRecipeById(recipeId)
        }

    private val _state = MutableStateFlow(DetailsState())

    val state = combine(
        _state,
        _recipe
    ) { state, recipe ->
        state.copy(recipe = recipe)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsState()
    )

    fun setRecipeId(recipeId: Long) {
        _recipeId.value = recipeId
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            is DetailsAction.OnDelete -> {
                val recipeId = _recipeId.value
                _recipeId.value = 0L
                viewModelScope.launch {
                    repository.deleteRecipeById(recipeId)
                }
            }
            is DetailsAction.OnSave -> {
                viewModelScope.launch {
                    val recipe = _recipe.firstOrNull()
                    // Only save if it’s a cloud recipe (local id still 0 but cloudId set)
                    if (recipe != null && recipe.id == 0L && recipe.cloudId != null) {
                        viewModelScope.launch {
                            val localId = repository.upsertRecipe(recipe)
                            // Update the state with the new local id
                            _recipeId.value = localId
                        }
                    }
                }
            }
            else -> { /* Other actions (OnBack, OnEdit) handled by the UI directly */ }
        }
    }
}