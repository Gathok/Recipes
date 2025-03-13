package de.malteans.recipes.app

import org.jetbrains.compose.resources.StringResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.de
import recipes.composeapp.generated.resources.en

enum class AppLang(
    val code: String,
    val StringRes: StringResource,
) {
    English("en", Res.string.en),
    German("de", Res.string.de),
}