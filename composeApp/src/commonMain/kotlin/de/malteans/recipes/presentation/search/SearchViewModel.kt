package de.malteans.recipes.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.domain.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _localRecipes = repository
        .getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _state = MutableStateFlow(SearchState())

    val state = combine(
        _state,
        _localRecipes.onStart { emit(emptyList()) },
    ) { state, localRecipes ->
        state.copy(
            localRecipes = localRecipes.filter { recipe ->
                recipe.name.contains(state.searchQuery, ignoreCase = true)
            }
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchState()
        )

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }
            is SearchAction.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
            else -> {}
        }
    }
}