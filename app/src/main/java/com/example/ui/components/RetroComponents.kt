package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FloatingCombatText
import com.example.data.model.FloatingEffectType
import com.example.ui.theme.RetroCardBorder
import com.example.ui.theme.RetroDarkBg
import com.example.ui.theme.RetroGold
import com.example.ui.theme.RetroGoldDark
import com.example.ui.theme.RetroRuby
import com.example.ui.theme.RetroSurface
import com.example.ui.theme.RetroSurfaceVariant
import kotlin.math.roundToInt

@Composable
fun RetroCard(
    modifier: Modifier = Modifier,
    borderColor: Color = RetroCardBorder,
    backgroundColor: Color = RetroSurface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(12.dp),
        content = content
    )
}

@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primaryColor: Color = RetroGold,
    textColor: Color = RetroDarkBg,
    testTag: String = "retro_button"
) {
    val bg = if (enabled) primaryColor else Color(0xFF475569)
    val shadow = if (enabled) RetroGoldDark else Color(0xFF334155)

    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(4.dp))
            .background(shadow)
            .padding(bottom = 3.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun RetroGaugeBar(
    current: Int,
    max: Int,
    fillColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    height: Int = 12
) {
    val progress = (current.toFloat() / max.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$current/$max",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF94A3B8)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(height.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(fillColor.copy(alpha = 0.8f), fillColor)
                        )
                    )
            )
        }
    }
}

@Composable
fun CrtScanlinesOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 4f
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = Color(0x18000000),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.2f
            )
            y += step
        }
    }
}

@Composable
fun RetroBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = RetroGold,
    textColor: Color = RetroDarkBg
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun AnimatedFloatingText(
    floatingText: FloatingCombatText,
    onFinished: () -> Unit
) {
    val animOffsetY = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(floatingText.id) {
        animOffsetY.animateTo(
            targetValue = -60f,
            animationSpec = tween(900, easing = LinearEasing)
        )
        animAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(300)
        )
        onFinished()
    }

    val textColor = when (floatingText.type) {
        FloatingEffectType.CRITICAL_DAMAGE -> Color(0xFFFFEB3B)
        FloatingEffectType.PHYSICAL_DAMAGE -> Color(0xFFEF4444)
        FloatingEffectType.MAGIC_DAMAGE -> Color(0xFF38BDF8)
        FloatingEffectType.HEAL -> Color(0xFF22C55E)
        FloatingEffectType.STATUS_EFFECT -> Color(0xFFA855F7)
        FloatingEffectType.MISS -> Color(0xFF94A3B8)
    }

    Text(
        text = floatingText.text,
        color = textColor,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (floatingText.type == FloatingEffectType.CRITICAL_DAMAGE) 22.sp else 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .offset { IntOffset(0, animOffsetY.value.roundToInt()) }
    )
}
