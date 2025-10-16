package com.testflight.ssgtestapp.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.os.Build
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.testflight.ssgtestapp.ui.theme.interSemiBold

/**
 * Modern animated glass-style button with gradient background and ultra-smooth press animations.
 *
 * Features physics-based spring animations for natural, fluid motion at 120fps.
 * Uses hardware-accelerated canvas drawing and efficient state management.
 *
 * @param text Button label text
 * @param onClick Callback invoked when button is tapped
 * @param modifier Layout modifier for positioning and sizing
 * @param onPressChange Optional callback notifying press state changes (true = pressed, false = released)
 * @param baseColor Base color for the gradient background
 * @param pressedBackgroundColor Background color when button is pressed
 * @param normalGradientAlphas Gradient alpha values (top, bottom) in normal state
 * @param pressedGradientAlphas Gradient alpha values (top, bottom) in pressed state
 * @param normalBorderColor Border color in normal state
 * @param pressedBorderColor Border color in pressed state
 * @param borderWidth Width of the border stroke
 * @param cornerRadius Radius of button corners
 * @param minWidth Minimum button width constraint
 * @param minHeight Minimum button height constraint
 * @param horizontalPadding Horizontal padding around text
 * @param verticalPadding Vertical padding around text
 * @param shadowElevation Distance of the shadow effect
 * @param pressOffset Vertical offset applied when button is pressed
 * @param animationDuration Duration of the animation in milliseconds (default: 150ms)
 * @param fontSize Size of the button text
 * @param fontWeight Weight of the button text
 * @param textColor Color of the button text
 */
@Composable
fun SecondaryGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPressChange: ((Boolean) -> Unit)? = null,
    baseColor: Color = Color(0xFF6883A9),
    pressedBackgroundColor: Color = Color(0xFF6883A9),
    normalGradientAlphas: Pair<Float, Float> = 0.35f to 0.25f,
    pressedGradientAlphas: Pair<Float, Float> = 0.2f to 0.15f,
    normalBorderColor: Color = Color.White,
    pressedBorderColor: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    minWidth: Dp = 150.dp,
    minHeight: Dp = 52.dp,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 14.dp,
    shadowElevation: Dp = 4.dp,
    pressOffset: Dp = 2.dp,
    animationDuration: Int = 150,
    fontFamily: FontFamily = interSemiBold,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val progress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(isPressed) {
        onPressChange?.invoke(isPressed)
        progress.animateTo(
            targetValue = if (isPressed) 1f else 0f,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, fontFamily, fontSize, fontWeight, textColor) {
        textMeasurer.measure(
            text = text,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = textColor
            )
        )
    }

    val animatedProgress = progress.value

    val animatedOffset = pressOffset * animatedProgress

    Box(
        modifier = modifier
            .widthIn(min = minWidth)
            .heightIn(min = minHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset(0, animatedOffset.roundToPx()) }
        ) {
            val textSize = textLayoutResult.size
            val horizontalPaddingPx = horizontalPadding.toPx()
            val verticalPaddingPx = verticalPadding.toPx()
            val buttonWidth = maxOf(
                textSize.width + horizontalPaddingPx * 2f,
                minWidth.toPx()
            )
            val buttonHeight = maxOf(
                textSize.height + verticalPaddingPx * 2f,
                minHeight.toPx()
            )
            val radiusPx = cornerRadius.toPx()
            val strokeWidthPx = borderWidth.toPx()
            val halfStrokeWidth = strokeWidthPx / 2f

            val shadowAlpha = 1f - animatedProgress
            val topAlpha = lerp(
                normalGradientAlphas.first,
                pressedGradientAlphas.first,
                animatedProgress
            )
            val bottomAlpha = lerp(
                normalGradientAlphas.second,
                pressedGradientAlphas.second,
                animatedProgress
            )

            val currentBaseColor = lerp(
                start = baseColor,
                stop = pressedBackgroundColor,
                fraction = animatedProgress
            )
            val currentBorderColor = lerp(
                start = normalBorderColor.copy(alpha = 0.05f),
                stop = pressedBorderColor.copy(alpha = 0.7f),
                fraction = animatedProgress
            )

            // Shadow
            if (shadowAlpha > 0.01f) {
                val shadowOffsetPx = shadowElevation.toPx()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    drawIntoCanvas { canvas ->
                        val shadowPaint = Paint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.argb(
                                (shadowAlpha * 64).toInt(),
                                0, 0, 0
                            )
                            maskFilter = BlurMaskFilter(
                                shadowOffsetPx,
                                BlurMaskFilter.Blur.NORMAL
                            )
                        }
                        canvas.nativeCanvas.drawRoundRect(
                            0f, shadowOffsetPx,
                            buttonWidth, buttonHeight + shadowOffsetPx,
                            radiusPx, radiusPx, shadowPaint
                        )
                    }
                } else {
                    val shadowLayers = 5
                    for (i in 0 until shadowLayers) {
                        val layerOffset = shadowOffsetPx * (i + 1) / shadowLayers
                        val layerAlpha =
                            shadowAlpha * (1f - i.toFloat() / shadowLayers) * 0.15f
                        drawRoundRect(
                            color = Color.Black.copy(alpha = layerAlpha),
                            topLeft = Offset(0f, layerOffset),
                            size = Size(buttonWidth, buttonHeight),
                            cornerRadius = CornerRadius(radiusPx)
                        )
                    }
                }
            }

            // Background gradient
            val gradientTopColor = currentBaseColor.copy(alpha = topAlpha)
            val gradientBottomColor = currentBaseColor.copy(alpha = bottomAlpha)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(gradientTopColor, gradientBottomColor)
                ),
                size = Size(buttonWidth, buttonHeight),
                cornerRadius = CornerRadius(radiusPx)
            )

            // Border
            drawRoundRect(
                color = currentBorderColor,
                topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
                size = Size(
                    buttonWidth - strokeWidthPx,
                    buttonHeight - strokeWidthPx
                ),
                cornerRadius = CornerRadius(radiusPx),
                style = Stroke(width = strokeWidthPx)
            )

            // Text
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    (buttonWidth - textSize.width) / 2f,
                    (buttonHeight - textSize.height) / 2f
                )
            )
        }
    }
}

