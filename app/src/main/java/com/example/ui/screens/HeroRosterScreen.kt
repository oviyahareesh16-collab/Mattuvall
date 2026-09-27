package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HeroId
import com.example.data.model.HeroSkin
import com.example.ui.components.PixelHeroSprite
import com.example.ui.components.RetroBadge
import com.example.ui.components.RetroButton
import com.example.ui.components.RetroCard
import com.example.ui.theme.RetroCardBorder
import com.example.ui.theme.RetroDarkBg
import com.example.ui.theme.RetroGold
import com.example.ui.theme.RetroGoldDark
import com.example.ui.theme.RetroManaBlue
import com.example.ui.theme.RetroRuby
import com.example.ui.theme.RetroSurface
import com.example.ui.theme.RetroSurfaceVariant
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HeroRosterScreen(
    viewModel: GameViewModel,
    onOpenUpgrade: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val isPaid = profile?.isPaidVersion == true
    val activeHeroId = remember(profile?.activeHeroId) {
        HeroId.valueOf(profile?.activeHeroId ?: HeroId.KNIGHT.name)
    }

    var selectedHeroId by remember { mutableStateOf(activeHeroId) }
    val selectedHero = viewModel.getConstructedHero(selectedHeroId)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "HERO BATTALION & SKINS",
                color = RetroGold,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "All heroes are unlocked via in-game campaign progression.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Hero Selection Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HeroId.entries) { hId ->
                    val heroInstance = viewModel.getConstructedHero(hId)
                    val isSelected = selectedHeroId == hId
                    val isCurrentActive = activeHeroId == hId
                    val isUnlocked = heroInstance.isUnlocked

                    Box(
                        modifier = Modifier
                            .testTag("hero_selector_${hId.name.lowercase()}")
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) RetroGoldDark else RetroSurfaceVariant)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) RetroGold else if (isCurrentActive) RetroManaBlue else RetroCardBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedHeroId = hId }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box {
                                PixelHeroSprite(
                                    heroId = hId,
                                    skinId = heroInstance.equippedSkinId,
                                    size = 54.dp
                                )
                                if (!isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color(0xBB000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked Hero",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = hId.displayName.substringBefore(" "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) RetroGold else if (isUnlocked) Color(0xFFF1F5F9) else Color(0xFF64748B)
                            )
                            if (isCurrentActive) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = RetroManaBlue,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Detailed Profile Card
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = if (selectedHero.isUnlocked) RetroGoldDark else Color(0xFF334155)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PixelHeroSprite(
                            heroId = selectedHero.id,
                            skinId = selectedHero.equippedSkinId,
                            size = 80.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedHero.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = RetroGold
                                )
                            }
                            Text(
                                text = selectedHero.title,
                                fontSize = 12.sp,
                                color = RetroManaBlue,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedHero.isUnlocked) "Level ${selectedHero.level} • XP: ${selectedHero.xp}/${selectedHero.xpToNextLevel}" else "STATUS: LOCKED",
                                fontSize = 11.sp,
                                color = if (selectedHero.isUnlocked) Color(0xFFCBD5E1) else RetroRuby,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (selectedHero.isUnlocked && activeHeroId != selectedHero.id) {
                            RetroButton(
                                text = "SELECT",
                                onClick = { viewModel.switchActiveHero(selectedHero.id) },
                                primaryColor = RetroGold,
                                testTag = "select_active_hero_btn"
                            )
                        } else if (activeHeroId == selectedHero.id) {
                            RetroBadge("IN BATTLE", backgroundColor = Color(0xFF10B981), textColor = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = selectedHero.id.lore,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    if (!selectedHero.isUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2D1525))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "🔒 UNLOCK CONDITION: ${selectedHero.id.unlockRequirementText}",
                                color = RetroRuby,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Hero Base & Combat Stats Grid
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurfaceVariant,
                borderColor = RetroCardBorder
            ) {
                Column {
                    Text(
                        text = "HERO ATTRIBUTES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = RetroGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatTile("HP", "${selectedHero.maxHp}", Color(0xFF22C55E), Modifier.weight(1f))
                        StatTile("MP", "${selectedHero.maxMp}", RetroManaBlue, Modifier.weight(1f))
                        StatTile("ATK", "${selectedHero.attack}", Color(0xFFEF4444), Modifier.weight(1f))
                        StatTile("DEF", "${selectedHero.defense}", Color(0xFFF59E0B), Modifier.weight(1f))
                        StatTile("SPD", "${selectedHero.speed}", Color(0xFFA855F7), Modifier.weight(1f))
                    }
                }
            }
        }

        // Character Skins Gallery
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WARDROBE & EXCLUSIVE SKINS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = RetroGold
                )
                if (!isPaid) {
                    Text(
                        text = "PRO Required for [PRO] Skins",
                        fontSize = 10.sp,
                        color = RetroRuby,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { onOpenUpgrade() }
                    )
                }
            }
        }

        items(selectedHero.availableSkins) { skin ->
            val isEquipped = selectedHero.equippedSkinId == skin.id
            val isLockedPro = skin.isPaidExclusive && !isPaid

            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isEquipped) Color(0xFF1E1738) else RetroSurface,
                borderColor = if (isEquipped) RetroGold else if (skin.isPaidExclusive) Color(0xFF7C3AED) else RetroCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelHeroSprite(
                        heroId = selectedHero.id,
                        skinId = skin.id,
                        size = 50.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = skin.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (skin.isPaidExclusive) RetroRuby else Color(0xFFF1F5F9)
                            )
                            if (skin.isPaidExclusive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                RetroBadge("PAID PRO", backgroundColor = RetroRuby, textColor = Color.White)
                            }
                        }
                        Text(
                            text = skin.description,
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isEquipped) {
                        RetroBadge("EQUIPPED", backgroundColor = RetroGold, textColor = RetroDarkBg)
                    } else if (isLockedPro) {
                        RetroButton(
                            text = "UNLOCK PRO",
                            onClick = onOpenUpgrade,
                            primaryColor = RetroRuby,
                            textColor = Color.White,
                            testTag = "unlock_pro_skin_btn"
                        )
                    } else if (selectedHero.isUnlocked) {
                        RetroButton(
                            text = "EQUIP",
                            onClick = { viewModel.equipSkin(selectedHero.id, skin.id) },
                            primaryColor = RetroGold,
                            testTag = "equip_skin_${skin.id}_btn"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8), fontFamily = FontFamily.Monospace)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
    }
}
