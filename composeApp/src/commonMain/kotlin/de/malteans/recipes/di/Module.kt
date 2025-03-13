package de.malteans.recipes.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.malteans.recipes.data.api.HttpClientFactory
import de.malteans.recipes.data.database.DatabaseFactory
import de.malteans.recipes.data.database.RecipeDatabase
import de.malteans.recipes.data.repository.DefaultRecipeRepository
import de.malteans.recipes.domain.RecipeRepository
import de.malteans.recipes.presentation.main.MainViewModel
import de.malteans.recipes.presentation.search.SearchViewModel
import de.malteans.recipes.presentation.plan.PlanViewModel
import de.malteans.recipes.presentation.add.AddViewModel
import de.malteans.recipes.presentation.details.DetailsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<RecipeDatabase>().recipeDao }

    single<RecipeRepository> { DefaultRecipeRepository(get()) }

    viewModel { MainViewModel(get()) }
    viewModel { PlanViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { AddViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}