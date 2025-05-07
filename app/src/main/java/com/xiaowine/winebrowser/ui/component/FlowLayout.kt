package com.xiaowine.winebrowser.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }
        val placeableWidth = placeables.first().width
        val itemsPerRow = 1 + ((constraints.maxWidth - placeableWidth) / (placeableWidth + horizontalSpacingPx))

        val rows = placeables.chunked(itemsPerRow)

        val height = if (rows.isEmpty()) 0 else rows.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        } + (rows.size - 1) * verticalSpacingPx
        val width = constraints.maxWidth

        layout(width, height) {
            var y = 0

            rows.forEach { row ->
                val maxRowWidth = placeableWidth * itemsPerRow + (itemsPerRow - 1) * horizontalSpacingPx
                val spacing = if (row.isNotEmpty()) (width - maxRowWidth) / (itemsPerRow - 1) else 0
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + horizontalSpacingPx + spacing
                }

                y += (row.maxOfOrNull { it.height } ?: 0) + verticalSpacingPx
            }
        }
    }
}