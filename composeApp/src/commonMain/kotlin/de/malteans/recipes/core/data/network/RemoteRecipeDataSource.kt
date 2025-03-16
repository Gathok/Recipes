package de.malteans.recipes.core.data.network

import de.malteans.recipes.core.data.network.dto.RecipeDto
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result

interface RemoteRecipeDataSource {

    suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote>
}