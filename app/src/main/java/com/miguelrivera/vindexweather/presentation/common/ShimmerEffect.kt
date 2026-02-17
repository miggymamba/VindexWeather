package com.miguelrivera.vindexweather.presentation.common
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.miguelrivera.vindexweather.presentation.theme.Dimens

/**
 * Applies a shimmer animation effect to a composable, typically used for loading states.
 *
 * This modifier creates a linear gradient that moves across the component, simulating
 * a "skeleton" loading view. It automatically handles sizing and positioning.
 *
 * @param shimmerColor The base color of the shimmer (usually a light gray).
 * @param durationMillis The duration of one complete shimmer cycle.
 */
fun Modifier.shimmerEffect(
    shimmerColor: Color = Color.LightGray.copy(alpha = 0.6f),
    durationMillis: Int = 1000
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis)
        ),
        label = "ShimmerOffset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor,
                Color.Transparent
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        ),
        shape = RoundedCornerShape(Dimens.ShimmerCornerRadius)
    ).onGloballyPositioned {
        size = it.size
    }
}