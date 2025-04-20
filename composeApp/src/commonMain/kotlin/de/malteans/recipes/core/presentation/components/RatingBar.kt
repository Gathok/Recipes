package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.recipes.core.domain.Recipe
import kotlin.math.roundToInt

@Composable
fun RatingBar(recipe: Recipe, small: Boolean = false) {
    Row {
        val rating = recipe.rating ?: recipe.onlineRating?.times(5)?.roundToInt()
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = if (rating == null) MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    else if (recipe.rating != null) MaterialTheme.colorScheme.tertiary.copy(if (i <= rating) 1f else 0.4f)
                    else MaterialTheme.colorScheme.secondary.copy(if (i <= rating) 1f else 0.4f),
                modifier = if (!small) Modifier
                    else Modifier.size(16.dp)
            )
        }
    }
}