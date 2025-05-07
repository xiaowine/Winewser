package com.xiaowine.winebrowser.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
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

        val rows = mutableListOf<MutableList<Placeable>>()
        val rowWidths = mutableListOf<Int>()

        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0

        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width + (if (currentRow.isEmpty()) 0 else horizontalSpacingPx) > constraints.maxWidth) {
                rows.add(currentRow)
                rowWidths.add(currentRowWidth)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }

            if (currentRow.isNotEmpty()) currentRowWidth += horizontalSpacingPx

            currentRow.add(placeable)
            currentRowWidth += placeable.width
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowWidths.add(currentRowWidth)
        }

        val height = if (rows.isEmpty()) 0 else rows.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        } + (rows.size - 1) * verticalSpacingPx
        val width = constraints.maxWidth

        layout(width, height) {
            var y = 0
            var spacing = 0
            rows.forEachIndexed { rowIndex, row ->
                val rowWidth = rowWidths[rowIndex]

                spacing = if (row.size > 1) {
                    if (rows.size > 1 && (rowIndex != rows.size - 1 || (rowIndex > 0 && row.size == rows[rowIndex - 1].size))) {
                        (width - rowWidth) / (row.size - 1)
                    } else {
                        horizontalSpacingPx
                    }
                } else {
                    0
                }

                var x = 0
                row.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + spacing + horizontalSpacingPx
                }

                y += (row.maxOfOrNull { it.height } ?: 0) + verticalSpacingPx
            }
        }
    }
}
