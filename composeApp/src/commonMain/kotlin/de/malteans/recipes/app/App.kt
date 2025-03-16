package de.malteans.recipes.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import de.malteans.recipes.core.presentation.main.MainScreenRoot
import de.malteans.recipes.theme.RecipesTheme

val LocalAppLocalization = compositionLocalOf {
    AppLang.English
}

@Composable
fun App() {
    val currentLanguage = rememberAppLocale()

    CompositionLocalProvider(LocalAppLocalization provides currentLanguage) {
        RecipesTheme {
            MainScreenRoot()
        }
    }
}