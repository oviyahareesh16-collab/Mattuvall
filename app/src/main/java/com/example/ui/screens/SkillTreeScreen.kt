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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
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
import com.example.data.model.Skill
import com.example.data.model.SkillBranch
import com.example.data.model.SkillDatabase
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
fun SkillTreeScreen(
    viewModel: GameViewModel,
    onOpenUpgrade: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val isPaid = profile?.isPaidVersion == true
    val activeHeroId = remember(profile?.activeHeroId) {
        HeroId.valueOf(profile?.activeHeroId ?: HeroId.KNIGHT.name)
    }

    var selectedHeroId by remember { mutableStateOf(activeHeroId) }
    val hero = viewModel.getConstructedHero(selectedHeroId)

    val allSkills = remember(selectedHeroId) {
        SkillDatabase.getSkillsForHero(selectedHeroId)
    }

    var selectedBranch by remember { mutableStateOf(SkillBranch.ASSAULT) }
    val branchSkills = remember(allSkills, selectedBranch) {
        allSkills.filter { it.branch == selectedBranch }.sortedBy { it.tier }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHARACTER SKILL TREES",
                        color = RetroGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Available Skill Points: ${hero.skillPoints}",
                        color = if (hero.skillPoints > 0) Color(0xFF4ADE80) else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (hero.allocatedSkillIds.isNotEmpty()) {
                    RetroButton(
                        text = "RESET",
                        onClick = { viewModel.resetSkillTree(hero.id) },
                        primaryColor = Color(0xFF475569),
                        textColor = Color.White,
                        testTag = "reset_skills_btn"
                    )
                }
            }
        }

        // Hero selector chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HeroId.entries) { hId ->
                    val isSelected = selectedHeroId == hId
                    val heroData = viewModel.getConstructedHero(hId)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) RetroGoldDark else RetroSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) RetroGold else RetroCardBorder,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedHeroId = hId }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = hId.displayName.substringBefore(" "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) RetroGold else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        // Branch Selection (Assault / Bastion / Arcana)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SkillBranch.entries.forEach { branch ->
                    val isSelected = selectedBranch == branch
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) Color(0xFF4A3875) else RetroSurface)
                            .border(
                                1.dp,
                                if (isSelected) RetroGold else RetroCardBorder,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { selectedBranch = branch }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = branch.title.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) RetroGold else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Expanded Skill Tree Notice Banner if free version
        item {
            if (!isPaid) {
                RetroCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF241433),
                    borderColor = Color(0xFF7C3AED)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Pro feature",
                            tint = RetroGold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EXPANDED SKILL TREES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = RetroGold
                            )
                            Text(
                                text = "Tier 4 Mastery & Tier 5 Mythic nodes unlock with Intrast Pro Edition.",
                                fontSize = 10.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                        RetroButton(
                            text = "UNLOCK",
                            onClick = onOpenUpgrade,
                            primaryColor = RetroRuby,
                            textColor = Color.White,
                            testTag = "skill_tree_unlock_pro_btn"
                        )
                    }
                }
            }
        }

        // Branch Skill Nodes (Tiers 1 to 5)
        items(branchSkills) { skill ->
            val isLearned = hero.allocatedSkillIds.contains(skill.id)
            val isExpandedLocked = skill.isExpandedPaidOnly && !isPaid
            val hasPoints = hero.skillPoints > 0
            val canLearn = !isLearned && !isExpandedLocked && hasPoints

            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("skill_node_${skill.id}"),
                backgroundColor = if (isLearned) Color(0xFF162529) else if (isExpandedLocked) Color(0xFF171026) else RetroSurface,
                borderColor = if (isLearned) Color(0xFF10B981) else if (skill.isExpandedPaidOnly) RetroRuby else RetroCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tier badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isLearned) Color(0xFF065F46)
                                else if (skill.isExpandedPaidOnly) Color(0xFF4C1D95)
                                else Color(0xFF1E293B)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "T${skill.tier}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLearned) Color(0xFF34D399) else RetroGold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = skill.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isLearned) Color(0xFF34D399) else if (isExpandedLocked) Color(0xFF94A3B8) else RetroGold
                            )
                            if (skill.isExpandedPaidOnly) {
                                Spacer(modifier = Modifier.width(6.dp))
                                RetroBadge("EXPANDED PRO", backgroundColor = RetroRuby, textColor = Color.White)
                            }
                        }
                        Text(
                            text = skill.description,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        if (skill.manaCost > 0) {
                            Text(
                                text = "Cost: ${skill.manaCost} MP",
                                fontSize = 10.sp,
                                color = RetroManaBlue,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isLearned) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF059669))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Learned",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else if (isExpandedLocked) {
                        RetroButton(
                            text = "PRO ONLY",
                            onClick = onOpenUpgrade,
                            primaryColor = RetroRuby,
                            textColor = Color.White,
                            testTag = "learn_pro_only_btn"
                        )
                    } else {
                        RetroButton(
                            text = "LEARN (-1 PT)",
                            onClick = { viewModel.allocateSkillPoint(hero.id, skill.id) },
                            enabled = canLearn,
                            primaryColor = RetroGold,
                            testTag = "learn_skill_${skill.id}_btn"
                        )
                    }
                }
            }
        }
    }
}
