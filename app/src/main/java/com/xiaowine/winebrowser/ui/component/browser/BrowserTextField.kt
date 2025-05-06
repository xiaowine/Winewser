package com.xiaowine.winebrowser.ui.component.browser

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * A [BrowserTextField] component with Miuix style.
 *
 * @param value The input [TextFieldValue] to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates values in
 *   [TextFieldValue]. An updated [TextFieldValue] comes as a parameter of the callback.
 * @param modifier The modifier to be applied to the [BrowserTextField].
 * @param insideMargin The margin inside the [BrowserTextField].
 * @param backgroundColor The background color of the [BrowserTextField].
 * @param cornerRadius The corner radius of the [BrowserTextField].
 * @param label The label to be displayed when the [BrowserTextField] is empty.
 * @param labelColor The color of the label.
 * @param useLabelAsPlaceholder Whether to use the label as a placeholder.
 * @param enabled Whether the [BrowserTextField] is enabled.
 * @param readOnly Whether the [BrowserTextField] is read-only.
 * @param textStyle The text style to be applied to the [BrowserTextField].
 * @param keyboardOptions The keyboard options to be applied to the [BrowserTextField].
 * @param keyboardActions The keyboard actions to be applied to the [BrowserTextField].
 * @param leadingIcon The leading icon to be displayed in the [BrowserTextField].
 * @param trailingIcon The trailing icon to be displayed in the [BrowserTextField].
 * @param singleLine Whether the text field is single line.
 * @param maxLines The maximum number of lines allowed to be displayed in [BrowserTextField].
 * @param minLines The minimum number of lines allowed to be displayed in [BrowserTextField]. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param visualTransformation The visual transformation to be applied to the [BrowserTextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [BrowserTextField].
 */
@Composable
fun BrowserTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = DpSize(16.dp, 16.dp),
    backgroundColor: Color = MiuixTheme.colorScheme.secondaryContainer,
    cornerRadius: Dp = 18.dp,
    label: String = "",
    labelColor: Color = MiuixTheme.colorScheme.onSecondaryContainer,
    useLabelAsPlaceholder: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MiuixTheme.textStyles.main,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val paddingModifier = remember(insideMargin, leadingIcon, trailingIcon) {
        if (leadingIcon == null && trailingIcon == null) Modifier.padding(horizontal = insideMargin.width)
        else if (leadingIcon == null) Modifier
            .padding(start = insideMargin.width)
        else if (trailingIcon == null) Modifier
            .padding(end = insideMargin.width)
        else Modifier
    }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderWidth by animateDpAsState(if (isFocused) 2.dp else 0.dp)
    val borderColor by animateColorAsState(if (isFocused) MiuixTheme.colorScheme.primary else backgroundColor)
    val labelOffsetY by animateDpAsState(if (value.text.isNotEmpty() && !useLabelAsPlaceholder) -(insideMargin.height / 2) else 0.dp)
    val innerTextOffsetY by animateDpAsState(if (value.text.isNotEmpty() && !useLabelAsPlaceholder) (insideMargin.height / 2) else 0.dp)
    val labelFontSize by animateDpAsState(if (value.text.isNotEmpty() && !useLabelAsPlaceholder) 10.dp else 17.dp)
    val border = Modifier.border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
    val labelOffset = if (label != "" && !useLabelAsPlaceholder) Modifier.offset(y = labelOffsetY) else Modifier
    val innerTextOffset = if (label != "" && !useLabelAsPlaceholder) Modifier.offset(y = innerTextOffsetY) else Modifier

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        decorationBox =
            @Composable { innerTextField ->
                val shape = remember { derivedStateOf { SmoothRoundedCornerShape(cornerRadius) } }
                Box(
                    modifier = Modifier
                        .background(
                            color = backgroundColor,
                            shape = shape.value
                        )
                        .then(border),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon()
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(paddingModifier),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = if (useLabelAsPlaceholder) value.text.isEmpty() else true,
                                enter = fadeIn(
                                    spring(stiffness = 2500f)
                                ),
                                exit = fadeOut(
                                    spring(stiffness = 5000f)
                                )
                            ) {
                                Text(
                                    text = label,
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = labelFontSize.value.sp,
                                    modifier = Modifier.then(labelOffset),
                                    color = labelColor
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 55.dp)
                                    .then(innerTextOffset),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                            }
                        }
                        if (trailingIcon != null) {
                            trailingIcon()
                        }
                    }
                }
            }
    )
}