package com.testflight.ssgtestapp.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.testflight.ssgtestapp.ui.theme.interSemiBold
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
 * @param springDampingRatio Damping ratio for spring animation (0.5-1.0, lower = more bounce)
 * @param springStiffness Stiffness of spring animation (higher = faster, 100-1000 recommended)
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
    springDampingRatio: Float = 0.7f,
    springStiffness: Float = 150f,
    fontFamily: FontFamily = interSemiBold,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor: Color = Color.White
) {
    // Track press state
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Notify parent component of press state changes
    LaunchedEffect(isPressed) {
        onPressChange?.invoke(isPressed)
    }

    // Measure text once and cache result for performance
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

    // Spring animation specification for smooth, natural motion
    val springSpec = remember(springDampingRatio, springStiffness) {
        spring<Float>(
            dampingRatio = springDampingRatio,
            stiffness = springStiffness
        )
    }

    // Animatable values for ultra-smooth transitions
    val shadowAlphaAnimatable = remember { Animatable(1f) }
    val verticalOffsetAnimatable = remember { Animatable(0f) }
    val topAlphaAnimatable = remember { Animatable(normalGradientAlphas.first) }
    val bottomAlphaAnimatable = remember { Animatable(normalGradientAlphas.second) }
    val borderAlphaAnimatable = remember { Animatable(0.05f) }
    val borderColorProgressAnimatable = remember { Animatable(0f) }
    val backgroundColorProgressAnimatable = remember { Animatable(0f) }

    // Animate all values simultaneously when press state changes
    LaunchedEffect(isPressed) {
        coroutineScope.launch {
            launch {
                shadowAlphaAnimatable.animateTo(
                    targetValue = if (isPressed) 0f else 1f,
                    animationSpec = springSpec
                )
            }
            launch {
                verticalOffsetAnimatable.animateTo(
                    targetValue = if (isPressed) pressOffset.value else 0f,
                    animationSpec = springSpec
                )
            }
            launch {
                topAlphaAnimatable.animateTo(
                    targetValue = if (isPressed) pressedGradientAlphas.first else normalGradientAlphas.first,
                    animationSpec = springSpec
                )
            }
            launch {
                bottomAlphaAnimatable.animateTo(
                    targetValue = if (isPressed) pressedGradientAlphas.second else normalGradientAlphas.second,
                    animationSpec = springSpec
                )
            }
            launch {
                borderAlphaAnimatable.animateTo(
                    targetValue = if (isPressed) 0.7f else 0.05f,
                    animationSpec = springSpec
                )
            }
            launch {
                borderColorProgressAnimatable.animateTo(
                    targetValue = if (isPressed) 1f else 0f,
                    animationSpec = springSpec
                )
            }
            launch {
                backgroundColorProgressAnimatable.animateTo(
                    targetValue = if (isPressed) 1f else 0f,
                    animationSpec = springSpec
                )
            }
        }
    }

    Box(
        modifier = modifier
            .widthIn(min = minWidth)
            .heightIn(min = minHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = (verticalOffsetAnimatable.value * density).roundToInt()
                    )
                }
        ) {
            // Calculate dimensions
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

            // Get current animated values
            val currentShadowAlpha = shadowAlphaAnimatable.value
            val currentTopAlpha = topAlphaAnimatable.value
            val currentBottomAlpha = bottomAlphaAnimatable.value
            val currentBorderAlpha = borderAlphaAnimatable.value
            val currentBorderProgress = borderColorProgressAnimatable.value
            val currentBackgroundProgress = backgroundColorProgressAnimatable.value

            // Draw shadow with compatibility for all Android versions
            if (currentShadowAlpha > 0.01f) {
                val shadowOffsetPx = shadowElevation.toPx()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Use BlurMaskFilter for API 28+
                    drawIntoCanvas { canvas ->
                        val shadowPaint = Paint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.argb(
                                (currentShadowAlpha * 64).toInt(),
                                0, 0, 0
                            )
                            maskFilter = BlurMaskFilter(
                                shadowOffsetPx,
                                BlurMaskFilter.Blur.NORMAL
                            )
                        }

                        canvas.nativeCanvas.drawRoundRect(
                            0f,
                            shadowOffsetPx,
                            buttonWidth,
                            buttonHeight + shadowOffsetPx,
                            radiusPx,
                            radiusPx,
                            shadowPaint
                        )
                    }
                } else {
                    // Fallback: multiple shadow layers for older versions
                    val shadowLayers = 5
                    for (i in 0 until shadowLayers) {
                        val layerOffset = shadowOffsetPx * (i + 1) / shadowLayers
                        val layerAlpha =
                            currentShadowAlpha * (1f - i.toFloat() / shadowLayers) * 0.15f

                        drawRoundRect(
                            color = Color.Black.copy(alpha = layerAlpha),
                            topLeft = Offset(0f, layerOffset),
                            size = Size(buttonWidth, buttonHeight),
                            cornerRadius = CornerRadius(radiusPx)
                        )
                    }
                }
            }

            // Interpolate between base color and pressed background color
            val currentBaseColor = lerp(
                start = baseColor,
                stop = pressedBackgroundColor,
                fraction = currentBackgroundProgress
            )

            // Draw background with vertical gradient
            val gradientTopColor = currentBaseColor.copy(alpha = currentTopAlpha)
            val gradientBottomColor = currentBaseColor.copy(alpha = currentBottomAlpha)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTopColor, gradientBottomColor)
                ),
                topLeft = Offset.Zero,
                size = Size(buttonWidth, buttonHeight),
                cornerRadius = CornerRadius(radiusPx)
            )

            // Draw border with smooth color transition
            val currentBorderColor = lerp(
                start = normalBorderColor.copy(alpha = currentBorderAlpha),
                stop = pressedBorderColor.copy(alpha = currentBorderAlpha),
                fraction = currentBorderProgress
            )

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

            // Draw centered text
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = (buttonWidth - textSize.width) / 2f,
                    y = (buttonHeight - textSize.height) / 2f
                )
            )
        }
    }
}
