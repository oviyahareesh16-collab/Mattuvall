package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BattlePhase
import com.example.data.model.Item
import com.example.data.model.ItemTemplates
import com.example.data.model.Skill
import com.example.data.model.SkillDatabase
import com.example.ui.components.AnimatedFloatingText
import com.example.ui.components.PixelEnemySprite
import com.example.ui.components.PixelHeroSprite
import com.example.ui.components.RetroBadge
import com.example.ui.components.RetroButton
import com.example.ui.components.RetroCard
import com.example.ui.components.RetroGaugeBar
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
fun BattleScreen(
    viewModel: GameViewModel,
    onBattleExit: () -> Unit
) {
    val combatState by viewModel.combatState.collectAsState()
    val state = combatState ?: run {
        onBattleExit()
        return
    }

    val inventoryEntities by viewModel.inventory.collectAsState()
    val inventoryItems = remember(inventoryEntities) {
        ItemTemplates.allShopItems.mapNotNull { item ->
            val count = inventoryEntities.find { it.itemId == item.id }?.quantity ?: 0
            if (count > 0) item.copy(quantity = count) else null
        }
    }

    var selectedTab by remember { mutableStateOf<BattleActionTab>(BattleActionTab.ACTIONS) }

    BackHandler {
        if (state.phase == BattlePhase.VICTORY || state.phase == BattlePhase.DEFEAT) {
            viewModel.endBattle()
            onBattleExit()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F0A1E), Color(0xFF1B1433), Color(0xFF0D0B18))
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Stage Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "STAGE ${state.stage.stageNumber}: ${state.stage.name.uppercase()}",
                        color = RetroGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Turn ${state.currentTurn} • ${state.phase.name}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.endBattle()
                        onBattleExit()
                    },
                    modifier = Modifier.testTag("exit_battle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Flee Battle",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Combat Stage Arena (Canvas Sprites, HP Gauges, Floating Texts)
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = Color(0xFF130E26),
                borderColor = Color(0xFF3B2D61)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Enemy Info Gauge (Top)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = state.enemy.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (state.enemy.isBoss) RetroRuby else Color(0xFFF1F5F9)
                                    )
                                    if (state.enemy.isBoss) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        RetroBadge("BOSS", backgroundColor = RetroRuby, textColor = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                RetroGaugeBar(
                                    current = state.enemy.currentHp,
                                    max = state.enemy.maxHp,
                                    fillColor = RetroRuby,
                                    label = "ENEMY HP",
                                    height = 10
                                )
                            }
                        }

                        // Sprites Row (Hero on Left, Enemy on Right)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hero Sprite
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PixelHeroSprite(
                                    heroId = state.hero.id,
                                    skinId = state.hero.equippedSkinId,
                                    size = 110.dp,
                                    isLunging = state.heroLungeAnimation,
                                    isHit = state.heroFlashHit
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.hero.name.substringBefore(" "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            // VS / Center clash indicator
                            Text(
                                text = "⚔",
                                fontSize = 24.sp,
                                color = RetroGold.copy(alpha = 0.6f)
                            )

                            // Enemy Sprite
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PixelEnemySprite(
                                    type = state.enemy.type,
                                    size = if (state.enemy.isBoss) 130.dp else 105.dp,
                                    isBoss = state.enemy.isBoss,
                                    isLunging = state.enemyLungeAnimation,
                                    isHit = state.enemyFlashHit
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Lv. ${state.enemy.level}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Hero Info Gauges (Bottom of Arena)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RetroGaugeBar(
                                    current = state.hero.currentHp,
                                    max = state.hero.maxHp,
                                    fillColor = Color(0xFF22C55E),
                                    label = "HERO HP",
                                    height = 10,
                                    modifier = Modifier.weight(1f)
                                )
                                RetroGaugeBar(
                                    current = state.hero.currentMp,
                                    max = state.hero.maxMp,
                                    fillColor = RetroManaBlue,
                                    label = "HERO MP",
                                    height = 10,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Floating Combat Texts (Crits, Damage, Heals)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        state.floatingTexts.forEach { ft ->
                            AnimatedFloatingText(
                                floatingText = ft,
                                onFinished = { /* State cleans up naturally */ }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Selection Tabs (Actions / Skills / Items / Battle Log)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BattleActionTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) RetroGoldDark else RetroSurfaceVariant)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) RetroGold else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Panel Content Based on Selected Tab
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                when (selectedTab) {
                    BattleActionTab.ACTIONS -> {
                        val isHeroTurn = state.phase == BattlePhase.PLAYER_TURN
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RetroButton(
                                    text = "⚔ ATTACK",
                                    onClick = { viewModel.executeHeroAttack() },
                                    enabled = isHeroTurn,
                                    primaryColor = RetroGold,
                                    modifier = Modifier.weight(1f),
                                    testTag = "battle_attack_btn"
                                )
                                RetroButton(
                                    text = "🛡 DEFEND",
                                    onClick = { viewModel.executeHeroDefend() },
                                    enabled = isHeroTurn,
                                    primaryColor = RetroManaBlue,
                                    textColor = Color.White,
                                    modifier = Modifier.weight(1f),
                                    testTag = "battle_defend_btn"
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RetroButton(
                                    text = "✨ SKILLS",
                                    onClick = { selectedTab = BattleActionTab.SKILLS },
                                    enabled = isHeroTurn,
                                    primaryColor = Color(0xFFA855F7),
                                    textColor = Color.White,
                                    modifier = Modifier.weight(1f),
                                    testTag = "battle_open_skills_btn"
                                )
                                RetroButton(
                                    text = "🧪 ITEMS",
                                    onClick = { selectedTab = BattleActionTab.ITEMS },
                                    enabled = isHeroTurn,
                                    primaryColor = Color(0xFF10B981),
                                    textColor = Color.White,
                                    modifier = Modifier.weight(1f),
                                    testTag = "battle_open_items_btn"
                                )
                            }
                        }
                    }

                    BattleActionTab.SKILLS -> {
                        val heroSkills = remember(state.hero.id) {
                            SkillDatabase.getSkillsForHero(state.hero.id)
                                .filter { state.hero.allocatedSkillIds.contains(it.id) }
                        }

                        if (heroSkills.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No skills unlocked in Skill Tree yet!\nAllocate points in the Skill Tree tab.",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(heroSkills) { skill ->
                                    val canAfford = state.hero.currentMp >= skill.manaCost
                                    val isHeroTurn = state.phase == BattlePhase.PLAYER_TURN
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1E1638))
                                            .clickable(enabled = isHeroTurn && canAfford) {
                                                viewModel.executeHeroSkill(skill)
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = skill.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = if (canAfford) RetroGold else Color(0xFF64748B)
                                                )
                                                if (skill.isExpandedPaidOnly) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    RetroBadge("PRO", backgroundColor = RetroRuby, textColor = Color.White)
                                                }
                                            }
                                            Text(
                                                text = skill.description,
                                                fontSize = 10.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = "${skill.manaCost} MP",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (canAfford) RetroManaBlue else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    BattleActionTab.ITEMS -> {
                        if (inventoryItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No potions or items in pouch!\nVisit the merchant shop to restock.",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(inventoryItems) { item ->
                                    val isHeroTurn = state.phase == BattlePhase.PLAYER_TURN
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1E1638))
                                            .clickable(enabled = isHeroTurn) {
                                                viewModel.useBattleItem(item)
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF4ADE80)
                                            )
                                            Text(
                                                text = item.description,
                                                fontSize = 10.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = "x${item.quantity}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = RetroGold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    BattleActionTab.LOG -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.combatLog) { logEntry ->
                                Text(
                                    text = "• $logEntry",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (logEntry.contains("Hero")) RetroManaBlue else if (logEntry.contains("VICTORY")) RetroGold else Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Victory Modal Overlay
        AnimatedVisibility(
            visible = state.phase == BattlePhase.VICTORY,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 100 }),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                backgroundColor = Color(0xFF160F2E),
                borderColor = RetroGold
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "★ VICTORY! ★",
                        color = RetroGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The dungeon floor is cleared!",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val rewards = state.rewardsEarned
                    if (rewards != null) {
                        RetroCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color(0xFF221644),
                            borderColor = RetroCardBorder
                        ) {
                            Column {
                                Text(
                                    text = "REWARDS ACQUIRED:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = RetroGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "🪙 Gold: +${rewards.gold}", fontSize = 12.sp, color = RetroGold)
                                Text(text = "⚡ XP: +${rewards.xp}", fontSize = 12.sp, color = RetroManaBlue)
                                Text(text = "💎 Gems: +${rewards.gems}", fontSize = 12.sp, color = Color(0xFFA855F7))

                                if (rewards.newlyUnlockedHero != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "🔓 NEW HERO UNLOCKED: ${rewards.newlyUnlockedHero.displayName}!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4ADE80)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    RetroButton(
                        text = "CLAIM & CONTINUE",
                        onClick = {
                            viewModel.endBattle()
                            onBattleExit()
                        },
                        primaryColor = RetroGold,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "victory_claim_btn"
                    )
                }
            }
        }

        // Defeat Modal Overlay
        AnimatedVisibility(
            visible = state.phase == BattlePhase.DEFEAT,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 100 }),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp),
                backgroundColor = Color(0xFF240E1B),
                borderColor = RetroRuby
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "☠ DEFEAT ☠",
                        color = RetroRuby,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Your warrior collapsed under dark blows. Strengthen your skill tree and return.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RetroButton(
                        text = "RETURN TO SANCTUARY",
                        onClick = {
                            viewModel.endBattle()
                            onBattleExit()
                        },
                        primaryColor = RetroRuby,
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "defeat_return_btn"
                    )
                }
            }
        }
    }
}

enum class BattleActionTab(val label: String) {
    ACTIONS("Commands"),
    SKILLS("Skills"),
    ITEMS("Items"),
    LOG("Battle Log")
}
