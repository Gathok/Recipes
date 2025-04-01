package de.malteans.recipes.core.presentation.plan.components

import de.malteans.recipes.core.presentation.components.UiText
import kotlinx.datetime.DayOfWeek
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.friday
import recipes.composeapp.generated.resources.monday
import recipes.composeapp.generated.resources.saturday
import recipes.composeapp.generated.resources.sunday
import recipes.composeapp.generated.resources.thursday
import recipes.composeapp.generated.resources.tuesday
import recipes.composeapp.generated.resources.wednesday

fun DayOfWeek.toUiText(): UiText {
    return UiText.FromStringResource(when (this) {
        DayOfWeek.MONDAY -> Res.string.monday
        DayOfWeek.TUESDAY -> Res.string.tuesday
        DayOfWeek.WEDNESDAY -> Res.string.wednesday
        DayOfWeek.THURSDAY -> Res.string.thursday
        DayOfWeek.FRIDAY -> Res.string.friday
        DayOfWeek.SATURDAY -> Res.string.saturday
        DayOfWeek.SUNDAY -> Res.string.sunday
        else -> throw IllegalArgumentException("Invalid day of week: $this")
    })
}