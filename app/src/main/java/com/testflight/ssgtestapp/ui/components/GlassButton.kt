package com.testflight.ssgtestapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// Arc drawing configuration
private const val ARC_ANGLE = 90f // Quarter circle for rounded corners
private const val ARC_SEGMENTS = 50 // Number of segments for smooth arc rendering
private const val FADE_CURVE_POWER = 1.5f // Exponential curve power for stroke width fade effect
private const val MIN_STROKE_WIDTH = 0.1f // Minimum stroke width to prevent complete fade to zero

// Button state styling constants
private const val DISABLED_DARKEN_FACTOR = -0.18f // Darkening factor for disabled state
private const val DISABLED_ALPHA = 0.8f // Opacity for disabled state
private const val PRESSED_DARKEN_FACTOR = -0.06f // Darkening factor for pressed state
private const val DISABLED_TEXT_ALPHA = 0.6f // Text opacity for disabled state

/**
 * A glass-morphism button component with 3D depth effect and edge highlights.
 *
 * Features:
 * - 3D depth effect with animated shadow
 * - Glass-morphism style with gradient background
 * - Edge highlights that fade on corners
 * - Press animation with depth reduction
 * - Disabled state support
 * - Customizable colors, sizes, and styling
 *
 * @param text Button label text
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param backgroundColor Base color of the button (default: #50B58D green)
 * @param textColor Color of the text (default: White)
 * @param cornerRadius Corner radius for rounded corners (default: 19dp)
 * @param depth 3D depth effect in unpressed state (default: 12dp)
 * @param shadowHorizontalExpansion Shadow expansion on left/right sides (default: 2dp)
 * @param shadowVerticalExpansion Shadow expansion on bottom (default: 12dp)
 * @param shadowPressedExpansion Shadow expansion when pressed (default: 1dp)
 * @param shadowColor Optional custom shadow color (default: darkened backgroundColor)
 * @param shadowDarkenAmount Factor to darken backgroundColor for shadow (default: 0.28)
 * @param edgeHighlightColor Color of edge highlights (default: White)
 * @param topEdgeHighlightOpacity Opacity of top edge highlight (default: 0.15)
 * @param bottomEdgeHighlightOpacity Opacity of bottom edge highlight (default: 0.35)
 * @param edgeHighlightStrokeWidth Width of edge highlight lines (default: 4dp)
 * @param textSize Font size of the text (default: 48sp)
 * @param textWeight Font weight of the text (default: Medium)
 * @param textAlign Text alignment (default: Center)
 * @param maxLines Maximum number of text lines (default: unlimited)
 * @param overflow Text overflow behavior (default: Ellipsis)
 * @param textShadowColor Color of text shadow (default: Black)
 * @param textShadowOpacity Opacity of text shadow (default: 0.35)
 * @param textShadowOffset Offset of text shadow (default: (0, 1.25))
 * @param textShadowBlurRadius Blur radius of text shadow (default: 2.5)
 * @param contentPadding Padding around the text content (default: 48dp)
 * @param pressAnimationDuration Duration of press animation in milliseconds (default: 150ms)
 * @param enabled Whether the button is enabled and clickable (default: true)
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF50B58D),
    textColor: Color = Color.White,
    cornerRadius: Dp = 19.dp,
    depth: Dp = 12.dp,
    shadowHorizontalExpansion: Dp = 2.dp,
    shadowVerticalExpansion: Dp = 12.dp,
    shadowPressedExpansion: Dp = 1.dp,
    shadowColor: Color? = null,
    shadowDarkenAmount: Float = 0.28f,
    edgeHighlightColor: Color = Color.White,
    topEdgeHighlightOpacity: Float = 0.15f,
    bottomEdgeHighlightOpacity: Float = 0.35f,
    edgeHighlightStrokeWidth: Dp = 4.dp,
    textSize: TextUnit = 48.sp,
    textWeight: FontWeight = FontWeight.Medium,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textShadowColor: Color = Color.Black,
    textShadowOpacity: Float = 0.35f,
    textShadowOffset: Offset = Offset(0f, 1.25f),
    textShadowBlurRadius: Float = 2.5f,
    contentPadding: Dp = 48.dp,
    pressAnimationDuration: Int = 150,
    enabled: Boolean = true,
) {
    // Track button press state
    var isPressed by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    // Prepare derived states and values
    val buttonState = ButtonState(isPressed, enabled)
    val animatedValues = rememberAnimatedValues(
        buttonState, depth, shadowHorizontalExpansion,
        shadowVerticalExpansion, shadowPressedExpansion, pressAnimationDuration
    )
    val highlightPixelValues =
        rememberHighlightPixelValues(density, cornerRadius, edgeHighlightStrokeWidth)
    val colors = rememberButtonColors(backgroundColor, shadowColor, shadowDarkenAmount, buttonState)

    Box(
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp) // Minimum touch target size
            .offset(y = animatedValues.yOffset) // Vertical offset for press animation
            .drawBehind {
                // Draw 3D shadow behind the button
                drawShadow(
                    color = colors.shadow,
                    horizontalExpansion = animatedValues.shadowHorizontal.toPx(),
                    verticalExpansion = animatedValues.shadowVertical.toPx(),
                    depth = animatedValues.depth.toPx(),
                    cornerRadius = highlightPixelValues.cornerRadius
                )
            }
            .clip(RoundedCornerShape(cornerRadius)) // Clip content to rounded shape
            .background(colors.backgroundBrush) // Apply gradient background
            .drawBehind {
                // Draw edge highlights on top and bottom
                drawEdgeHighlights(
                    color = edgeHighlightColor,
                    topOpacity = topEdgeHighlightOpacity,
                    bottomOpacity = bottomEdgeHighlightOpacity,
                    strokeWidth = highlightPixelValues.highlightStrokeWidth,
                    cornerRadius = highlightPixelValues.cornerRadius
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                // Handle press gestures with state tracking
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            tryAwaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            }
            .padding(contentPadding), // Internal padding for content
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else textColor.copy(alpha = DISABLED_TEXT_ALPHA),
            fontSize = textSize,
            fontWeight = textWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow,
            style = TextStyle(
                shadow = Shadow(
                    color = textShadowColor.copy(alpha = textShadowOpacity),
                    offset = textShadowOffset,
                    blurRadius = textShadowBlurRadius
                )
            )
        )
    }
}

/**
 * Represents the current state of the button.
 *
 * @property isPressed Whether the button is currently being pressed
 * @property enabled Whether the button is enabled and interactive
 * @property isPressedAndEnabled True if button is pressed AND enabled
 * @property isPressedOrDisabled True if button is pressed OR disabled
 */
private data class ButtonState(val isPressed: Boolean, val enabled: Boolean) {
    val isPressedAndEnabled = isPressed && enabled
    val isPressedOrDisabled = isPressed || !enabled
}

/**
 * Container for all animated dimension values.
 *
 * @property depth Current animated 3D depth value
 * @property yOffset Current animated vertical offset value
 * @property shadowHorizontal Current animated horizontal shadow expansion
 * @property shadowVertical Current animated vertical shadow expansion
 */
private data class AnimatedValues(
    val depth: Dp,
    val yOffset: Dp,
    val shadowHorizontal: Dp,
    val shadowVertical: Dp
)

/**
 * Container for values converted to pixels.
 *
 * @property cornerRadius Corner radius in pixels
 * @property highlightStrokeWidth Highlight stroke width in pixels
 */
private data class HighlightPixelValues(
    val cornerRadius: Float,
    val highlightStrokeWidth: Float
)

/**
 * Container for computed button colors.
 *
 * @property shadow Shadow color
 * @property backgroundBrush Background gradient brush
 */
private data class ButtonColors(
    val shadow: Color,
    val backgroundBrush: Brush
)

/**
 * Remembers and returns animated values based on button state.
 * All values animate smoothly when button state changes.
 *
 * @return AnimatedValues containing current animated dimension values
 */
@Composable
private fun rememberAnimatedValues(
    state: ButtonState,
    depth: Dp,
    shadowHorizontalExpansion: Dp,
    shadowVerticalExpansion: Dp,
    shadowPressedExpansion: Dp,
    duration: Int
): AnimatedValues {
    // Animate depth: collapse to 0 when pressed or disabled
    val animatedDepth by animateDpAsState(
        targetValue = if (state.isPressedOrDisabled) 0.dp else depth,
        animationSpec = tween(duration),
        label = "depth"
    )

    // Animate vertical offset: move down by depth amount when pressed
    val animatedYOffset by animateDpAsState(
        targetValue = if (state.isPressedAndEnabled) depth else 0.dp,
        animationSpec = tween(duration),
        label = "yOffset"
    )

    // Animate horizontal shadow: reduce when pressed
    val animatedShadowHorizontal by animateDpAsState(
        targetValue = if (state.isPressedAndEnabled) shadowPressedExpansion else shadowHorizontalExpansion,
        animationSpec = tween(duration),
        label = "shadowHorizontal"
    )

    // Animate vertical shadow: reduce when pressed
    val animatedShadowVertical by animateDpAsState(
        targetValue = if (state.isPressedAndEnabled) shadowPressedExpansion else shadowVerticalExpansion,
        animationSpec = tween(duration),
        label = "shadowVertical"
    )

    return AnimatedValues(
        animatedDepth,
        animatedYOffset,
        animatedShadowHorizontal,
        animatedShadowVertical
    )
}

/**
 * Converts Dp values to pixels using current density.
 *
 * @return PixelValues containing converted pixel values
 */
@Composable
private fun rememberHighlightPixelValues(
    density: androidx.compose.ui.unit.Density,
    cornerRadius: Dp,
    highlightStrokeWidth: Dp
): HighlightPixelValues {
    return with(density) {
        HighlightPixelValues(
            cornerRadius = cornerRadius.toPx(),
            highlightStrokeWidth = highlightStrokeWidth.toPx()
        )
    }
}

/**
 * Computes button colors based on state.
 * Creates gradient with lighter edges and darker center for glass effect.
 *
 * @return ButtonColors containing shadow color and background gradient
 */
@Composable
private fun rememberButtonColors(
    backgroundColor: Color,
    shadowColor: Color?,
    shadowDarkenAmount: Float,
    state: ButtonState
): ButtonColors {
    // Use provided shadow color or darken background color
    val calculatedShadowColor = shadowColor ?: backgroundColor.adjustBrightness(-shadowDarkenAmount)

    val backgroundBrush = when {
        // Disabled state: uniform darkened color with transparency
        !state.enabled -> {
            val disabledColor = backgroundColor.adjustBrightness(DISABLED_DARKEN_FACTOR)
                .copy(alpha = DISABLED_ALPHA)
            Brush.verticalGradient(listOf(disabledColor, disabledColor))
        }
        // Pressed state: simple top-to-bottom darkening gradient
        state.isPressed -> Brush.verticalGradient(
            listOf(backgroundColor, backgroundColor.adjustBrightness(PRESSED_DARKEN_FACTOR))
        )
        // Normal state: glass-morphism gradient (lighter at edges, darker in center)
        else -> Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to backgroundColor.adjustBrightness(0.15f),  // Top: lighter (+15%)
                0.25f to backgroundColor.adjustBrightness(0.08f), // Upper middle: slightly lighter (+8%)
                0.5f to backgroundColor,                           // Center: base color (darkest)
                0.75f to backgroundColor.adjustBrightness(0.15f), // Lower middle: lighter (+15%)
                1.0f to backgroundColor.adjustBrightness(0.30f)   // Bottom: lightest (+30%)
            )
        )
    }

    return ButtonColors(calculatedShadowColor, backgroundBrush)
}

/**
 * Draws the 3D shadow behind the button.
 * Shadow expands beyond button bounds to create depth illusion.
 *
 * @param color Shadow color
 * @param horizontalExpansion How much shadow extends left and right
 * @param verticalExpansion How much shadow extends downward
 * @param depth Vertical depth of the shadow
 * @param cornerRadius Corner radius matching the button
 */
private fun DrawScope.drawShadow(
    color: Color,
    horizontalExpansion: Float,
    verticalExpansion: Float,
    depth: Float,
    cornerRadius: Float
) {
    // Translate to account for shadow expansion
    translate(left = -horizontalExpansion, top = -horizontalExpansion) {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(
                width = size.width + horizontalExpansion * 2, // Expand left and right
                height = size.height + horizontalExpansion + verticalExpansion + depth // Expand bottom with depth
            ),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}

/**
 * Draws edge highlights on top and bottom edges of the button.
 * Creates glass-like appearance with light reflection effect.
 *
 * @param color Highlight color
 * @param topOpacity Opacity of top edge highlight
 * @param bottomOpacity Opacity of bottom edge highlight
 * @param strokeWidth Width of highlight lines
 * @param cornerRadius Corner radius for proper edge rendering
 */
private fun DrawScope.drawEdgeHighlights(
    color: Color,
    topOpacity: Float,
    bottomOpacity: Float,
    strokeWidth: Float,
    cornerRadius: Float
) {
    // Draw top edge (y = 0)
    drawEdge(color, topOpacity, strokeWidth, 0f, cornerRadius, isTop = true)
    // Draw bottom edge (y = height)
    drawEdge(color, bottomOpacity, strokeWidth, size.height, cornerRadius, isTop = false)
}

/**
 * Draws a single edge (top or bottom) with fade effects on corners.
 * Edge consists of: left arc → horizontal line → right arc
 *
 * @param color Highlight color
 * @param opacity Edge opacity
 * @param strokeWidth Line width
 * @param y Vertical position of the edge
 * @param radius Corner radius
 * @param isTop True for top edge, false for bottom edge
 */
private fun DrawScope.drawEdge(
    color: Color,
    opacity: Float,
    strokeWidth: Float,
    y: Float,
    radius: Float,
    isTop: Boolean
) {
    // Determine arc angles based on edge position
    val angles = if (isTop) EdgeAngles(180f, 270f) else EdgeAngles(90f, 0f)
    val centerY = if (isTop) y + radius else y - radius

    // Left corner arc
    // Top edge: fade IN (from 0 to full width)
    // Bottom edge: fade OUT (from full width to 0)
    drawArcWithFade(
        color,
        opacity,
        strokeWidth,
        radius,
        centerY,
        radius,
        angles.left,
        fadeIn = isTop
    )

    // Center horizontal line with full stroke width
    drawLine(
        color.copy(alpha = opacity),
        Offset(radius, y),
        Offset(size.width - radius, y),
        strokeWidth
    )

    // Right corner arc
    // Top edge: fade OUT (from full width to 0)
    // Bottom edge: fade IN (from 0 to full width)
    drawArcWithFade(
        color,
        opacity,
        strokeWidth,
        size.width - radius,
        centerY,
        radius,
        angles.right,
        fadeIn = !isTop
    )
}

/**
 * Container for arc start and end angles.
 *
 * @property left Left corner arc start angle
 * @property right Right corner arc start angle
 */
private data class EdgeAngles(val left: Float, val right: Float)

/**
 * Draws an arc with gradually fading stroke width.
 * Creates smooth transition from full width to zero (or vice versa).
 *
 * @param color Line color
 * @param opacity Line opacity
 * @param maxStrokeWidth Maximum stroke width
 * @param centerX Arc center X coordinate
 * @param centerY Arc center Y coordinate
 * @param radius Arc radius
 * @param startAngle Starting angle in degrees
 * @param fadeIn True to fade in (0 → max), false to fade out (max → 0)
 */
private fun DrawScope.drawArcWithFade(
    color: Color,
    opacity: Float,
    maxStrokeWidth: Float,
    centerX: Float,
    centerY: Float,
    radius: Float,
    startAngle: Float,
    fadeIn: Boolean
) {
    // Draw arc as series of small line segments
    repeat(ARC_SEGMENTS) { i ->
        val progress = i.toFloat() / ARC_SEGMENTS
        val nextProgress = (i + 1).toFloat() / ARC_SEGMENTS

        // Calculate points on the arc
        val start = angleToPoint(centerX, centerY, radius, startAngle + ARC_ANGLE * progress)
        val end = angleToPoint(centerX, centerY, radius, startAngle + ARC_ANGLE * nextProgress)

        // Calculate faded stroke width for this segment
        val strokeWidth = calculateFadedStrokeWidth(progress, nextProgress, maxStrokeWidth, fadeIn)

        // Draw segment with faded width
        drawLine(color.copy(alpha = opacity), start, end, strokeWidth)
    }
}

/**
 * Calculates faded stroke width for an arc segment.
 * Uses exponential curve for smooth, natural-looking fade.
 *
 * @param progress Current segment progress (0.0 to 1.0)
 * @param nextProgress Next segment progress
 * @param maxWidth Maximum stroke width
 * @param fadeIn True for fade in, false for fade out
 * @return Calculated stroke width for this segment
 */
private fun calculateFadedStrokeWidth(
    progress: Float,
    nextProgress: Float,
    maxWidth: Float,
    fadeIn: Boolean
): Float {
    // Apply exponential curve to progress for smoother fade
    val factor =
        if (fadeIn) progress.pow(FADE_CURVE_POWER) else (1f - progress).pow(FADE_CURVE_POWER)
    val nextFactor =
        if (fadeIn) nextProgress.pow(FADE_CURVE_POWER) else (1f - nextProgress).pow(FADE_CURVE_POWER)

    // Average the factors and multiply by max width, ensuring minimum width
    return ((factor + nextFactor) / 2 * maxWidth).coerceAtLeast(MIN_STROKE_WIDTH)
}

/**
 * Converts angle and radius to Cartesian coordinates.
 * Used for calculating points on circular arcs.
 *
 * @param centerX Circle center X coordinate
 * @param centerY Circle center Y coordinate
 * @param radius Circle radius
 * @param angleDegrees Angle in degrees (0° = right, 90° = down, 180° = left, 270° = up)
 * @return Offset representing the point on the circle
 */
private fun angleToPoint(
    centerX: Float,
    centerY: Float,
    radius: Float,
    angleDegrees: Float
): Offset {
    val angleRad = angleDegrees * PI.toFloat() / 180f // Convert degrees to radians
    return Offset(
        x = centerX + radius * cos(angleRad),
        y = centerY + radius * sin(angleRad)
    )
}

/**
 * Adjusts the brightness of a color by a given factor.
 * Positive factor lightens, negative factor darkens.
 *
 * @param factor Brightness adjustment factor (-1.0 to 1.0)
 *               Positive: lighten (move towards white)
 *               Negative: darken (move towards black)
 * @return New color with adjusted brightness, alpha unchanged
 */
private fun Color.adjustBrightness(factor: Float): Color {
    val adjustedFactor = if (factor >= 0) factor else -factor

    return if (factor >= 0) {
        // Lighten: interpolate towards white (1.0)
        Color(
            red = (red + (1f - red) * adjustedFactor).coerceIn(0f, 1f),
            green = (green + (1f - green) * adjustedFactor).coerceIn(0f, 1f),
            blue = (blue + (1f - blue) * adjustedFactor).coerceIn(0f, 1f),
            alpha = alpha
        )
    } else {
        // Darken: interpolate towards black (0.0)
        Color(
            red = (red * (1f - adjustedFactor)).coerceIn(0f, 1f),
            green = (green * (1f - adjustedFactor)).coerceIn(0f, 1f),
            blue = (blue * (1f - adjustedFactor)).coerceIn(0f, 1f),
            alpha = alpha
        )
    }
}
