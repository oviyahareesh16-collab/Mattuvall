package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.EnemyType
import com.example.data.model.HeroId

@Composable
fun PixelHeroSprite(
    heroId: HeroId,
    skinId: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    isLunging: Boolean = false,
    isHit: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_idle")
    val idleBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(modifier = modifier.size(size)) {
        val lungeOffsetX = if (isLunging) 24f else 0f
        val hitColor = if (isHit) Color.White else null

        // Ground shadow
        drawOval(
            color = Color(0x66000000),
            topLeft = Offset(this.size.width * 0.15f + lungeOffsetX, this.size.height * 0.85f),
            size = Size(this.size.width * 0.7f, this.size.height * 0.12f)
        )

        drawHeroSprite(
            heroId = heroId,
            skinId = skinId,
            offsetY = idleBob,
            offsetX = lungeOffsetX,
            overrideColor = hitColor
        )
    }
}

private fun DrawScope.drawHeroSprite(
    heroId: HeroId,
    skinId: String,
    offsetY: Float,
    offsetX: Float,
    overrideColor: Color?
) {
    val pixelSize = size.width / 16f
    val baseStartX = (size.width - (16 * pixelSize)) / 2f + offsetX
    val baseStartY = (size.height - (16 * pixelSize)) / 2f - offsetY

    fun drawPixel(gridX: Int, gridY: Int, color: Color) {
        val c = overrideColor ?: color
        drawRect(
            color = c,
            topLeft = Offset(baseStartX + gridX * pixelSize, baseStartY + gridY * pixelSize),
            size = Size(pixelSize, pixelSize)
        )
    }

    val isCyber = skinId.contains("cyber")
    val isGold = skinId.contains("gold") || skinId.contains("solar")
    val isStarlight = skinId.contains("starlight")
    val isPhantom = skinId.contains("phantom")

    val armorColor = when {
        isCyber -> Color(0xFF06B6D4)
        isGold -> Color(0xFFFBBF24)
        isStarlight -> Color(0xFF8B5CF6)
        isPhantom -> Color(0xFF1E293B)
        else -> when (heroId) {
            HeroId.KNIGHT -> Color(0xFF94A3B8)
            HeroId.MAGE -> Color(0xFFDC2626)
            HeroId.ROGUE -> Color(0xFF334155)
            HeroId.PALADIN -> Color(0xFFF59E0B)
            HeroId.NECROMANCER -> Color(0xFF475569)
            HeroId.VALKYRIE -> Color(0xFF3B82F6)
        }
    }

    val accentColor = when {
        isCyber -> Color(0xFFF43F5E)
        isGold -> Color(0xFFFEF08A)
        isPhantom -> Color(0xFF06B6D4)
        else -> Color(0xFFFFD700)
    }

    val skinTone = Color(0xFFFFD1A4)

    when (heroId) {
        HeroId.KNIGHT -> {
            // Helmet
            for (x in 6..9) for (y in 2..5) drawPixel(x, y, armorColor)
            // Visor slit
            drawPixel(8, 4, Color(0xFF1E293B))
            drawPixel(9, 4, accentColor)
            // Plume
            drawPixel(6, 1, accentColor)
            drawPixel(7, 1, accentColor)
            // Torso armor
            for (x in 5..10) for (y in 6..10) drawPixel(x, y, armorColor)
            // Belt / golden buckle
            for (x in 6..9) drawPixel(x, 10, accentColor)
            // Legs / boots
            for (y in 11..14) {
                drawPixel(6, y, Color(0xFF475569))
                drawPixel(7, y, Color(0xFF475569))
                drawPixel(9, y, Color(0xFF475569))
                drawPixel(10, y, Color(0xFF475569))
            }
            // Sword blade
            for (y in 4..11) drawPixel(12, y, if (isCyber) Color(0xFF00FFFF) else Color(0xFFE2E8F0))
            drawPixel(12, 12, accentColor) // hilt
            drawPixel(11, 11, accentColor)
            drawPixel(13, 11, accentColor)
            // Shield on left arm
            for (x in 3..5) for (y in 7..11) drawPixel(x, y, accentColor)
        }
        HeroId.MAGE -> {
            // Pointy Wizard Hat
            drawPixel(8, 1, armorColor)
            for (x in 7..9) drawPixel(x, 2, armorColor)
            for (x in 6..10) drawPixel(x, 3, armorColor)
            for (x in 5..11) drawPixel(x, 4, accentColor) // Hat brim
            // Face
            for (x in 7..9) for (y in 5..6) drawPixel(x, y, skinTone)
            drawPixel(9, 5, Color(0xFF1E293B)) // eye
            // Robe body
            for (x in 5..10) for (y in 7..13) drawPixel(x, y, armorColor)
            // Trim
            for (x in 5..10) drawPixel(x, 14, accentColor)
            // Magical Staff
            for (y in 4..13) drawPixel(12, y, Color(0xFF78350F))
            drawPixel(12, 3, if (isStarlight) Color(0xFF38BDF8) else Color(0xFF67E8F9)) // glowing orb
            drawPixel(11, 3, accentColor)
            drawPixel(13, 3, accentColor)
        }
        HeroId.ROGUE -> {
            // Stealth Hood
            for (x in 6..10) for (y in 2..5) drawPixel(x, y, armorColor)
            // Mask & Glowing eye
            drawPixel(8, 4, skinTone)
            drawPixel(9, 4, accentColor) // glowing ocular visor/eye
            // Scarf / Cloak
            for (x in 5..10) for (y in 6..11) drawPixel(x, y, armorColor)
            // Twin Daggers
            drawPixel(12, 8, Color(0xFF34D399)) // poison tip
            drawPixel(12, 9, Color(0xFFE2E8F0))
            drawPixel(12, 10, Color(0xFF1E293B))
            drawPixel(4, 9, Color(0xFF34D399))
            drawPixel(4, 10, Color(0xFFE2E8F0))
            // Boots
            for (y in 12..14) {
                drawPixel(6, y, Color(0xFF0F172A))
                drawPixel(9, y, Color(0xFF0F172A))
            }
        }
        else -> {
            // General Paladin / Valkyrie / Necro
            // Crown / Wing helm
            drawPixel(6, 2, accentColor)
            drawPixel(9, 2, accentColor)
            for (x in 6..9) for (y in 3..5) drawPixel(x, y, armorColor)
            drawPixel(8, 4, skinTone)
            drawPixel(9, 4, Color(0xFF1E293B))
            // Body plate
            for (x in 5..10) for (y in 6..11) drawPixel(x, y, armorColor)
            for (x in 6..9) drawPixel(x, 8, accentColor)
            // Spear or Hammer
            for (y in 2..13) drawPixel(12, y, Color(0xFFCBD5E1))
            drawPixel(12, 2, accentColor)
            drawPixel(11, 2, accentColor)
            drawPixel(13, 2, accentColor)
            // Boots
            for (y in 12..14) {
                drawPixel(6, y, Color(0xFF1E293B))
                drawPixel(9, y, Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun PixelEnemySprite(
    type: EnemyType,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    isBoss: Boolean = false,
    isLunging: Boolean = false,
    isHit: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "enemy_idle")
    val idleBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isBoss) 8f else 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "enemy_bob"
    )

    Canvas(modifier = modifier.size(size)) {
        val lungeOffsetX = if (isLunging) -24f else 0f
        val hitColor = if (isHit) Color.White else null

        // Shadow
        drawOval(
            color = Color(0x66000000),
            topLeft = Offset(this.size.width * 0.15f + lungeOffsetX, this.size.height * 0.85f),
            size = Size(this.size.width * 0.7f, this.size.height * 0.12f)
        )

        drawEnemySprite(
            type = type,
            isBoss = isBoss,
            offsetY = idleBob,
            offsetX = lungeOffsetX,
            overrideColor = hitColor
        )
    }
}

private fun DrawScope.drawEnemySprite(
    type: EnemyType,
    isBoss: Boolean,
    offsetY: Float,
    offsetX: Float,
    overrideColor: Color?
) {
    val pixelSize = size.width / 16f
    val baseStartX = (size.width - (16 * pixelSize)) / 2f + offsetX
    val baseStartY = (size.height - (16 * pixelSize)) / 2f - offsetY

    fun drawPixel(gridX: Int, gridY: Int, color: Color) {
        val c = overrideColor ?: color
        drawRect(
            color = c,
            topLeft = Offset(baseStartX + gridX * pixelSize, baseStartY + gridY * pixelSize),
            size = Size(pixelSize, pixelSize)
        )
    }

    when (type) {
        EnemyType.SLIME -> {
            val slimeGreen = Color(0xFF22C55E)
            val darkGreen = Color(0xFF15803D)
            val eyeWhite = Color.White
            val pupil = Color(0xFF0F172A)

            for (x in 6..9) drawPixel(x, 6, slimeGreen)
            for (x in 4..11) drawPixel(x, 7, slimeGreen)
            for (x in 3..12) for (y in 8..12) drawPixel(x, y, slimeGreen)
            for (x in 4..11) drawPixel(x, 13, darkGreen)
            // Big Retro Eyes
            drawPixel(5, 8, eyeWhite)
            drawPixel(6, 8, pupil)
            drawPixel(9, 8, eyeWhite)
            drawPixel(10, 8, pupil)
            // Highlight
            drawPixel(5, 7, Color(0xFF86EFAC))
        }

        EnemyType.GOBLIN -> {
            val skin = Color(0xFF84CC16)
            val cloth = Color(0xFF78350F)
            // Pointy ears
            drawPixel(4, 4, skin)
            drawPixel(11, 4, skin)
            // Head
            for (x in 5..10) for (y in 4..7) drawPixel(x, y, skin)
            // Red eyes
            drawPixel(6, 5, Color(0xFFEF4444))
            drawPixel(9, 5, Color(0xFFEF4444))
            // Body
            for (x in 5..10) for (y in 8..11) drawPixel(x, y, cloth)
            // Weapon
            for (y in 6..12) drawPixel(3, y, Color(0xFF94A3B8))
            drawPixel(3, 5, Color(0xFFEF4444)) // blood tip
            // Legs
            drawPixel(6, 12, Color(0xFF475569))
            drawPixel(6, 13, Color(0xFF475569))
            drawPixel(9, 12, Color(0xFF475569))
            drawPixel(9, 13, Color(0xFF475569))
        }

        EnemyType.SKELETON -> {
            val bone = Color(0xFFF1F5F9)
            val dark = Color(0xFF0F172A)
            // Skull
            for (x in 6..9) for (y in 3..6) drawPixel(x, y, bone)
            // Eye sockets
            drawPixel(7, 4, Color(0xFFEF4444))
            drawPixel(9, 4, Color(0xFFEF4444))
            // Ribs
            for (x in 6..9) drawPixel(x, 8, bone)
            for (x in 6..9) drawPixel(x, 10, bone)
            drawPixel(7, 9, dark)
            drawPixel(8, 9, dark)
            // Sword
            for (y in 5..12) drawPixel(3, y, Color(0xFF94A3B8))
            // Legs
            drawPixel(6, 12, bone)
            drawPixel(6, 13, bone)
            drawPixel(9, 12, bone)
            drawPixel(9, 13, bone)
        }

        EnemyType.FLAME_DRAKE, EnemyType.ORC, EnemyType.VOID_DEMON, EnemyType.SHADOW_SPECTER, EnemyType.LICH_KING -> {
            val mainColor = when (type) {
                EnemyType.FLAME_DRAKE -> Color(0xFFEF4444)
                EnemyType.ORC -> Color(0xFF15803D)
                EnemyType.SHADOW_SPECTER -> Color(0xFF6366F1)
                EnemyType.VOID_DEMON -> Color(0xFF7C3AED)
                EnemyType.LICH_KING -> Color(0xFF0EA5E9)
                else -> Color(0xFFEF4444)
            }
            val hornColor = Color(0xFFFBBF24)

            // Horns / Wings
            drawPixel(3, 2, hornColor)
            drawPixel(12, 2, hornColor)
            drawPixel(4, 3, hornColor)
            drawPixel(11, 3, hornColor)

            // Boss Crown / Aura if Boss
            if (isBoss) {
                for (x in 6..9) drawPixel(x, 1, Color(0xFFFFD700))
                drawPixel(5, 2, Color(0xFFFFD700))
                drawPixel(10, 2, Color(0xFFFFD700))
            }

            // Head / Torso
            for (x in 5..10) for (y in 4..7) drawPixel(x, y, mainColor)
            // Glowing fiery eyes
            drawPixel(6, 5, Color(0xFFFFEB3B))
            drawPixel(9, 5, Color(0xFFFFEB3B))
            // Glowing mouth / breath
            drawPixel(7, 6, Color(0xFFF97316))
            drawPixel(8, 6, Color(0xFFF97316))

            // Body
            for (x in 4..11) for (y in 8..12) drawPixel(x, y, mainColor)
            // Armor / Scales
            drawPixel(7, 9, Color(0xFF1E293B))
            drawPixel(8, 9, Color(0xFF1E293B))
            drawPixel(7, 11, Color(0xFF1E293B))
            drawPixel(8, 11, Color(0xFF1E293B))

            // Feet / Claws
            drawPixel(5, 13, Color(0xFF0F172A))
            drawPixel(6, 13, Color(0xFF0F172A))
            drawPixel(9, 13, Color(0xFF0F172A))
            drawPixel(10, 13, Color(0xFF0F172A))
        }
    }
}
