package de.malteans.recipes.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.malteans.recipes.core.data.network.KtorRemoteRecipeDataSource
import de.malteans.recipes.core.data.network.HttpClientFactory
import de.malteans.recipes.core.data.database.DatabaseFactory
import de.malteans.recipes.core.data.database.RecipeDatabase
import de.malteans.recipes.core.data.network.RemoteRecipeDataSource
import de.malteans.recipes.core.data.repository.DefaultRecipeRepository
import de.malteans.recipes.core.domain.RecipeRepository
import de.malteans.recipes.core.presentation.SelectedRecipeViewModel
import de.malteans.recipes.core.presentation.main.MainViewModel
import de.malteans.recipes.core.presentation.search.SearchViewModel
import de.malteans.recipes.plan.presentation.plan.PlanViewModel
import de.malteans.recipes.core.presentation.add.AddViewModel
import de.malteans.recipes.core.presentation.details.DetailsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<RecipeDatabase>().recipeDao }
    single<RemoteRecipeDataSource> { KtorRemoteRecipeDataSource(get()) }

    single<RecipeRepository> { DefaultRecipeRepository(get(), get()) }

    viewModel { MainViewModel(get()) }
    viewModel { PlanViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SelectedRecipeViewModel() }
    viewModel { AddViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}
