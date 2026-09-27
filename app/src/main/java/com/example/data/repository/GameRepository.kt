package com.example.data.repository

import com.example.data.local.CloudSaveEntity
import com.example.data.local.HeroProgressEntity
import com.example.data.local.IntrastDao
import com.example.data.local.InventoryEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.StageProgressEntity
import com.example.data.model.Hero
import com.example.data.model.HeroId
import com.example.data.model.HeroTemplates
import com.example.data.model.Item
import com.example.data.model.ItemTemplates
import com.example.data.model.Stage
import com.example.data.model.StageDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GameRepository(private val dao: IntrastDao) {

    val playerProfile: Flow<PlayerProfileEntity?> = dao.getPlayerProfile()
    val stagesProgress: Flow<List<StageProgressEntity>> = dao.getAllStagesProgress()
    val heroesProgress: Flow<List<HeroProgressEntity>> = dao.getAllHeroesProgress()
    val inventory: Flow<List<InventoryEntity>> = dao.getInventoryItems()
    val cloudSaves: Flow<List<CloudSaveEntity>> = dao.getCloudSaves()

    suspend fun initializeIfNeeded() {
        val currentProfile = dao.getPlayerProfile().firstOrNull()
        if (currentProfile == null) {
            // Seed profile
            dao.insertOrUpdateProfile(
                PlayerProfileEntity(
                    id = 1,
                    gold = 350,
                    gems = 50,
                    totalBattles = 0,
                    totalWins = 0,
                    activeHeroId = HeroId.KNIGHT.name,
                    isPaidVersion = false,
                    hapticsEnabled = true,
                    hapticIntensity = "HEAVY",
                    soundEnabled = true,
                    crtScanlines = true
                )
            )

            // Seed heroes
            val heroEntities = HeroId.entries.map { heroId ->
                val template = HeroTemplates.createHero(heroId, isUnlocked = heroId == HeroId.KNIGHT)
                HeroProgressEntity(
                    heroId = heroId.name,
                    isUnlocked = heroId == HeroId.KNIGHT,
                    level = 1,
                    xp = 0,
                    equippedSkinId = template.equippedSkinId,
                    allocatedSkillIds = if (heroId == HeroId.KNIGHT) "k_a1" else "",
                    skillPoints = 1
                )
            }
            dao.insertAllHeroes(heroEntities)

            // Seed stages
            val stageEntities = StageDatabase.allStages.map { stage ->
                StageProgressEntity(
                    stageNumber = stage.stageNumber,
                    stars = 0,
                    isCompleted = false,
                    isUnlocked = stage.stageNumber == 1
                )
            }
            dao.insertAllStages(stageEntities)

            // Seed starter inventory
            val starterInventory = listOf(
                InventoryEntity("item_hp_small", 4),
                InventoryEntity("item_mp_small", 3),
                InventoryEntity("item_fire_bomb", 1)
            )
            dao.insertAllInventory(starterInventory)
        }
    }

    suspend fun updateProfile(profile: PlayerProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun setPaidVersion(isPaid: Boolean, upiRef: String?) {
        val current = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(
            current.copy(
                isPaidVersion = isPaid,
                upiTransactionRef = upiRef,
                gold = if (isPaid) current.gold + 1000 else current.gold,
                gems = if (isPaid) current.gems + 150 else current.gems
            )
        )
    }

    suspend fun setActiveHero(heroId: HeroId) {
        val current = dao.getPlayerProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(current.copy(activeHeroId = heroId.name))
    }

    suspend fun updateHeroProgress(heroProgress: HeroProgressEntity) {
        dao.insertOrUpdateHero(heroProgress)
    }

    suspend fun allocateSkill(heroId: HeroId, skillId: String) {
        val hero = dao.getHeroProgressById(heroId.name) ?: return
        val currentAllocated = hero.allocatedSkillIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (!currentAllocated.contains(skillId) && hero.skillPoints > 0) {
            currentAllocated.add(skillId)
            dao.insertOrUpdateHero(
                hero.copy(
                    allocatedSkillIds = currentAllocated.joinToString(","),
                    skillPoints = hero.skillPoints - 1
                )
            )
        }
    }

    suspend fun resetSkills(heroId: HeroId) {
        val hero = dao.getHeroProgressById(heroId.name) ?: return
        val count = hero.allocatedSkillIds.split(",").filter { it.isNotBlank() }.size
        dao.insertOrUpdateHero(
            hero.copy(
                allocatedSkillIds = "",
                skillPoints = hero.skillPoints + count
            )
        )
    }

    suspend fun equipSkin(heroId: HeroId, skinId: String) {
        val hero = dao.getHeroProgressById(heroId.name) ?: return
        dao.insertOrUpdateHero(hero.copy(equippedSkinId = skinId))
    }

    suspend fun recordBattleVictory(
        stageNumber: Int,
        goldEarned: Int,
        xpEarned: Int,
        gemsEarned: Int,
        activeHeroId: HeroId
    ): List<HeroId> {
        val profile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        val newWins = profile.totalWins + 1
        val newBattles = profile.totalBattles + 1

        // Pro version grants 2x gold bonus
        val finalGold = if (profile.isPaidVersion) (goldEarned * 2) else goldEarned
        dao.insertOrUpdateProfile(
            profile.copy(
                gold = profile.gold + finalGold,
                gems = profile.gems + gemsEarned,
                totalWins = newWins,
                totalBattles = newBattles
            )
        )

        // Update Stage Progress
        dao.insertOrUpdateStage(
            StageProgressEntity(
                stageNumber = stageNumber,
                stars = 3,
                isCompleted = true,
                isUnlocked = true
            )
        )
        // Unlock next stage
        val nextStageNum = stageNumber + 1
        if (nextStageNum <= StageDatabase.allStages.size) {
            dao.insertOrUpdateStage(
                StageProgressEntity(
                    stageNumber = nextStageNum,
                    stars = 0,
                    isCompleted = false,
                    isUnlocked = true
                )
            )
        }

        // Hero XP and Level Up
        val heroProgress = dao.getHeroProgressById(activeHeroId.name)
        if (heroProgress != null) {
            val totalXp = heroProgress.xp + xpEarned
            val currentLevel = heroProgress.level
            val xpNeeded = currentLevel * 100
            val (newLevel, newXp, earnedPoints) = if (totalXp >= xpNeeded) {
                Triple(currentLevel + 1, totalXp - xpNeeded, heroProgress.skillPoints + 1)
            } else {
                Triple(currentLevel, totalXp, heroProgress.skillPoints)
            }
            dao.insertOrUpdateHero(
                heroProgress.copy(
                    level = newLevel,
                    xp = newXp,
                    skillPoints = earnedPoints
                )
            )
        }

        // Check hero unlocks based on progression criteria
        val unlockedHeroes = mutableListOf<HeroId>()
        HeroId.entries.forEach { id ->
            if (id != HeroId.KNIGHT) {
                val p = dao.getHeroProgressById(id.name)
                if (p != null && !p.isUnlocked) {
                    val canUnlock = (id.unlockStageRequired > 0 && stageNumber >= id.unlockStageRequired) ||
                            (id.unlockWinsRequired > 0 && newWins >= id.unlockWinsRequired)
                    if (canUnlock) {
                        dao.insertOrUpdateHero(p.copy(isUnlocked = true))
                        unlockedHeroes.add(id)
                    }
                }
            }
        }
        return unlockedHeroes
    }

    suspend fun recordBattleDefeat() {
        val profile = dao.getPlayerProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(profile.copy(totalBattles = profile.totalBattles + 1))
    }

    suspend fun buyItem(item: Item, quantity: Int = 1): Boolean {
        val profile = dao.getPlayerProfile().firstOrNull() ?: return false
        val totalCost = item.goldPrice * quantity
        if (profile.gold < totalCost) return false

        dao.insertOrUpdateProfile(profile.copy(gold = profile.gold - totalCost))
        val currentItems = dao.getInventoryItems().firstOrNull() ?: emptyList()
        val existing = currentItems.find { it.itemId == item.id }
        val newQty = (existing?.quantity ?: 0) + quantity
        dao.updateInventoryItem(InventoryEntity(item.id, newQty))
        return true
    }

    suspend fun consumeItem(itemId: String): Boolean {
        val currentItems = dao.getInventoryItems().firstOrNull() ?: emptyList()
        val existing = currentItems.find { it.itemId == itemId } ?: return false
        if (existing.quantity <= 0) return false
        dao.updateInventoryItem(existing.copy(quantity = existing.quantity - 1))
        return true
    }

    suspend fun createCloudSave(slotId: Int = 1): CloudSaveEntity {
        val profile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        val heroes = dao.getAllHeroesProgress().firstOrNull() ?: emptyList()
        val stages = dao.getAllStagesProgress().firstOrNull() ?: emptyList()
        val inventory = dao.getInventoryItems().firstOrNull() ?: emptyList()

        val highestStage = stages.filter { it.isCompleted }.maxOfOrNull { it.stageNumber } ?: 1
        val summary = "Hero: ${profile.activeHeroId} | Gold: ${profile.gold} | Stage: $highestStage"
        val payload = "SAVE-V1:${profile.gold}:${profile.gems}:${profile.activeHeroId}:${profile.isPaidVersion}:$highestStage"

        val cloudSave = CloudSaveEntity(
            slotId = slotId,
            timestamp = System.currentTimeMillis(),
            playerLevelSummary = summary,
            stageReached = highestStage,
            isPaidPro = profile.isPaidVersion,
            fullBackupJson = payload
        )
        dao.insertCloudSave(cloudSave)
        dao.insertOrUpdateProfile(profile.copy(lastCloudSyncTime = System.currentTimeMillis()))
        return cloudSave
    }

    suspend fun restoreCloudSave(save: CloudSaveEntity) {
        val profile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(
            profile.copy(
                isPaidVersion = save.isPaidPro || profile.isPaidVersion,
                lastCloudSyncTime = save.timestamp
            )
        )
    }
}
