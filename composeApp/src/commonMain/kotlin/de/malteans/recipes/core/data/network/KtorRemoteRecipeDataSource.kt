package de.malteans.recipes.core.data.network

import de.malteans.recipes.core.data.network.dto.RecipeDto
import de.malteans.recipes.core.data.network.dto.RecipesResponseDto
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLBuilder

class KtorRemoteRecipeDataSource(
    private val client: HttpClient,
) : RemoteRecipeDataSource {

    override suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote> {
        val url = URLBuilder("https://apps.malteans.de/recipes.php").apply {
            parameters.append("query", query)
        }.buildString()
        return safeCall<RecipesResponseDto> {
            client.get(url)
        }.map { it.recipes }
    }
}
