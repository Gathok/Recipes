package de.malteans.recipes.core.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.RecipeRepository
import de.malteans.recipes.core.domain.errorHandling.onError
import de.malteans.recipes.core.domain.errorHandling.onSuccess
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: RecipeRepository,
) : ViewModel() {

    private var _searchJob: Job? = null

    private val _state = MutableStateFlow(SearchState())

    val state = _state
        .onStart {
            observerSearchQuery()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchState()
        )

    // Navigation events: will emit the local recipe id to navigate with
    private val _navigationEvent = MutableSharedFlow<Long>()
    val navigationEvent = _navigationEvent

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query, cloudError = null)
                }
            }
            is SearchAction.OnTabSelected -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            selectedTabIndex = action.index,
                            cloudError = null,
                        )
                    }
                    _searchJob?.cancel()
                    _searchJob = when (action.index) {
                        0 -> fetchLocalRecipes(state.value.searchQuery)
                        1 -> fetchCloudRecipes(state.value.searchQuery)
                        else -> throw IllegalStateException("Invalid tab index: ${action.index}")
                    }
                }
            }
            is SearchAction.OnRecipeClick -> {
                viewModelScope.launch {
                    val localId = if (action.recipe.cloudId != null && action.recipe.id == 0L) {
                        // For cloud recipes not yet saved locally,
                        // check if it exists (open it) or upsert if not.
                        repository.upsertOrOpenCloudRecipe(action.recipe)
                    } else {
                        action.recipe.id
                    }
                    _navigationEvent.emit(localId)
                }
            }
        }
    }

    private fun observerSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                _searchJob?.cancel()
                _searchJob = when (state.value.selectedTabIndex) {
                    0 -> fetchLocalRecipes(query)
                    1 -> fetchCloudRecipes(query)
                    else -> throw IllegalStateException("Invalid tab index: ${state.value.selectedTabIndex}")
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchLocalRecipes(query: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        _state.update {
            it.copy(
                localRecipes = repository.fetchLocalRecipes(query),
                isLoading = false
            )
        }
    }

    private fun fetchCloudRecipes(query: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        repository.fetchCloudRecipes(query)
            .onSuccess { recipes ->
                _state.update { it.copy(cloudRecipes = recipes, isLoading = false) }
            }
            .onError { error ->
                _state.update { it.copy(cloudError = error, isLoading = false) }
            }
    }
}
