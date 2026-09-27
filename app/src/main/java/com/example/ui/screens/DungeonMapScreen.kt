package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HeroId
import com.example.data.model.Stage
import com.example.data.model.StageDatabase
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
fun DungeonMapScreen(
    viewModel: GameViewModel,
    onStartStage: (Stage) -> Unit,
    onOpenUpgrade: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val stagesProgress by viewModel.stagesProgress.collectAsState()
    var selectedRealm by remember { mutableIntStateOf(1) }

    val activeHeroId = remember(profile?.activeHeroId) {
        HeroId.valueOf(profile?.activeHeroId ?: HeroId.KNIGHT.name)
    }
    val hero = viewModel.getConstructedHero(activeHeroId)

    val realms = listOf(
        Pair(1, "Realm 1: Whispering Woods"),
        Pair(2, "Realm 2: Cursed Crypt"),
        Pair(3, "Realm 3: Molten Caldera"),
        Pair(4, "Realm 4: Void Citadel")
    )

    val realmStages = remember(selectedRealm, stagesProgress) {
        StageDatabase.allStages.filter { it.realmId == selectedRealm }.map { s ->
            val p = stagesProgress.find { it.stageNumber == s.stageNumber }
            s.copy(
                isCompleted = p?.isCompleted ?: false,
                isUnlocked = p?.isUnlocked ?: (s.stageNumber == 1),
                stars = p?.stars ?: 0
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Hero Visual Banner
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Black,
                borderColor = RetroCardBorder
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_intrast_banner),
                        contentDescription = "Intrast Dungeon Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                    )
                    // Gradient overlay with title
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x770D0B18))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(
                                text = "INTRAST: RETRO RPG",
                                color = RetroGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Turn-Based Tactical Dungeon Crawler",
                                color = Color(0xFFF1F5F9),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Active Hero Mini Card & Pro Banner
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = RetroGoldDark
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelHeroSprite(
                        heroId = hero.id,
                        skinId = hero.equippedSkinId,
                        size = 64.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = hero.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = RetroGold
                            )
                            if (profile?.isPaidVersion == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                RetroBadge("PRO ACTIVE", backgroundColor = RetroRuby, textColor = Color.White)
                            }
                        }
                        Text(
                            text = "Level ${hero.level} ${hero.title}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "🪙 ${profile?.gold ?: 0}", fontSize = 11.sp, color = RetroGold)
                            Text(text = "💎 ${profile?.gems ?: 0}", fontSize = 11.sp, color = RetroManaBlue)
                            Text(text = "Points: ${hero.skillPoints}", fontSize = 11.sp, color = Color(0xFFA855F7))
                        }
                    }

                    if (profile?.isPaidVersion != true) {
                        RetroButton(
                            text = "UPGRADE",
                            onClick = onOpenUpgrade,
                            primaryColor = RetroRuby,
                            textColor = Color.White,
                            testTag = "dungeon_upgrade_btn"
                        )
                    }
                }
            }
        }

        // Realm Navigation Carousel
        item {
            Text(
                text = "CAMPAIGN REALMS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = RetroGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(realms) { (id, name) ->
                    val isSelected = selectedRealm == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) RetroGoldDark else RetroSurfaceVariant)
                            .clickable { selectedRealm = id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) RetroGold else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Stage List in Selected Realm
        items(realmStages) { stage ->
            val isUnlocked = stage.isUnlocked
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stage_card_${stage.stageNumber}"),
                backgroundColor = if (isUnlocked) RetroSurface else Color(0xFF130E24),
                borderColor = if (stage.isBoss) RetroRuby else if (isUnlocked) RetroCardBorder else Color(0xFF261D42)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stage number circle or lock icon
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (stage.isBoss) RetroRuby else if (isUnlocked) RetroGoldDark else Color(0xFF1E293B))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(
                                text = "${stage.stageNumber}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (stage.isBoss) Color.White else RetroGold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Stage",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Stage Title & description
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stage.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isUnlocked) Color(0xFFF1F5F9) else Color(0xFF64748B)
                            )
                            if (stage.isBoss) {
                                Spacer(modifier = Modifier.width(6.dp))
                                RetroBadge("BOSS", backgroundColor = RetroRuby, textColor = Color.White)
                            }
                        }
                        Text(
                            text = stage.description,
                            fontSize = 11.sp,
                            color = if (isUnlocked) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Rec Lv. ${stage.recommendedLevel}",
                                fontSize = 10.sp,
                                color = RetroManaBlue,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "+${stage.rewardGold} Gold",
                                fontSize = 10.sp,
                                color = RetroGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Launch Battle Button
                    if (isUnlocked) {
                        RetroButton(
                            text = if (stage.isCompleted) "REPLAY" else "BATTLE",
                            onClick = { onStartStage(stage) },
                            primaryColor = if (stage.isBoss) RetroRuby else RetroGold,
                            textColor = if (stage.isBoss) Color.White else RetroDarkBg,
                            testTag = "battle_stage_${stage.stageNumber}_btn"
                        )
                    }
                }
            }
        }
    }
}
