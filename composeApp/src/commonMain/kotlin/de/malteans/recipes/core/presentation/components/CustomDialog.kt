package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    leftIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier
                .clickable { onDismissRequest() }
        )
    },
    rightIcon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = modifier
        ) {
            Column (
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Header area with a custom Layout to properly size and position the icons and title.
                Layout(
                    content = {
                        // Wrap each in a Box to ensure proper measurement
                        Box { leftIcon() }
                        Box { title() }
                        Box { rightIcon() }
                    },
                ) { measurables, constraints ->
                    // Measure the left and right icons first.
                    val leftPlaceable = measurables[0].measure(constraints)
                    val rightPlaceable = measurables[2].measure(constraints)

                    // Determine the maximum width of the icons.
                    val maxSideWidth = maxOf(leftPlaceable.width, rightPlaceable.width)
                    // Calculate the maximum width available for the title.
                    // This prevents the title from overlapping the icons while keeping it centered.
                    val titleMaxWidth = (constraints.maxWidth - 2 * maxSideWidth).coerceAtLeast(0)
                    val titleConstraints = constraints.copy(maxWidth = titleMaxWidth)
                    val titlePlaceable = measurables[1].measure(titleConstraints)

                    // The header height is the maximum height among the icons and the title.
                    val headerHeight = listOf(
                        leftPlaceable.height,
                        titlePlaceable.height,
                        rightPlaceable.height
                    ).maxOrNull() ?: 0

                    layout(constraints.maxWidth, headerHeight) {
                        // Place left icon at the left edge, centered vertically.
                        leftPlaceable.placeRelative(
                            x = 0,
                            y = (headerHeight - leftPlaceable.height) / 2
                        )
                        // Place right icon at the right edge, centered vertically.
                        rightPlaceable.placeRelative(
                            x = constraints.maxWidth - rightPlaceable.width,
                            y = (headerHeight - rightPlaceable.height) / 2
                        )
                        // Center the title horizontally within the entire header.
                        val titleX = (constraints.maxWidth - titlePlaceable.width) / 2
                        titlePlaceable.placeRelative(
                            x = titleX,
                            y = (headerHeight - titlePlaceable.height) / 2
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    content()
                }
            }
        }
    }
}