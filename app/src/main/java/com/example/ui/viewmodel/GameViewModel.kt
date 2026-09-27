package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.IntrastDatabase
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.BattlePhase
import com.example.data.model.BattleRewards
import com.example.data.model.CombatState
import com.example.data.model.Enemy
import com.example.data.model.EnemyTemplates
import com.example.data.model.FloatingCombatText
import com.example.data.model.FloatingEffectType
import com.example.data.model.HapticPulseType
import com.example.data.model.Hero
import com.example.data.model.HeroId
import com.example.data.model.HeroTemplates
import com.example.data.model.Item
import com.example.data.model.ItemTemplates
import com.example.data.model.ItemType
import com.example.data.model.Skill
import com.example.data.model.SkillType
import com.example.data.model.Stage
import com.example.data.model.StageDatabase
import com.example.data.repository.GameRepository
import com.example.util.HapticsManager
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = IntrastDatabase.getDatabase(application)
    private val repository = GameRepository(db.dao())
    val haptics = HapticsManager(application)
    val sounds = SoundManager()

    val profile = repository.playerProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val heroesProgress = repository.heroesProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stagesProgress = repository.stagesProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val inventory = repository.inventory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cloudSaves = repository.cloudSaves.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Active Combat
    private val _combatState = MutableStateFlow<CombatState?>(null)
    val combatState: StateFlow<CombatState?> = _combatState.asStateFlow()

    // Navigation and UI state
    val isUpgradeDialogOpen = MutableStateFlow(false)
    val isCloudSaveDialogOpen = MutableStateFlow(false)
    val statusMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }

        viewModelScope.launch {
            profile.collect { p ->
                if (p != null) {
                    haptics.isEnabled = p.hapticsEnabled
                    haptics.intensity = p.hapticIntensity
                    sounds.isEnabled = p.soundEnabled
                }
            }
        }
    }

    fun startBattle(stage: Stage) {
        val heroId = HeroId.valueOf(profile.value?.activeHeroId ?: HeroId.KNIGHT.name)
        val heroData = getConstructedHero(heroId)
        val enemy = EnemyTemplates.createEnemy(stage.enemyType, stage.recommendedLevel, stage.isBoss)

        _combatState.value = CombatState(
            stage = stage,
            hero = heroData,
            enemy = enemy,
            phase = BattlePhase.PLAYER_TURN,
            combatLog = listOf("Encountered ${enemy.name}! Ready your blade.")
        )
        sounds.play(SoundManager.SoundEffect.BUTTON)
        haptics.trigger(HapticPulseType.BUTTON_CLICK)
    }

    fun executeHeroAttack() {
        val state = _combatState.value ?: return
        if (state.phase != BattlePhase.PLAYER_TURN) return

        viewModelScope.launch {
            // Animate Hero Lunge
            _combatState.value = state.copy(
                phase = BattlePhase.PLAYER_ANIMATING,
                heroLungeAnimation = true
            )
            delay(150)

            val isCrit = Random.nextFloat() < state.hero.critRate
            val critMultiplier = if (isCrit) 1.8f else 1.0f
            val baseAtk = state.hero.attack * (if (state.heroAtkBuffTurns > 0) 1.25f else 1.0f)
            val defenseMitigation = (state.enemy.defense * 0.4f)
            val rawDamage = ((baseAtk - defenseMitigation) * critMultiplier).toInt().coerceAtLeast(8)
            val damage = rawDamage + Random.nextInt(-2, 3)

            val newEnemyHp = (state.enemy.currentHp - damage).coerceAtLeast(0)
            val updatedEnemy = state.enemy.copy(currentHp = newEnemyHp)

            // Trigger sound & haptic
            if (isCrit) {
                sounds.play(SoundManager.SoundEffect.CRITICAL)
                haptics.trigger(HapticPulseType.CRITICAL_HIT)
            } else {
                sounds.play(SoundManager.SoundEffect.ATTACK)
                haptics.trigger(HapticPulseType.HERO_ATTACK)
            }

            val floating = FloatingCombatText(
                text = if (isCrit) "CRIT! -$damage" else "-$damage",
                type = if (isCrit) FloatingEffectType.CRITICAL_DAMAGE else FloatingEffectType.PHYSICAL_DAMAGE,
                isTargetHero = false
            )

            val log = "Hero strikes for $damage damage! ${if (isCrit) "[CRITICAL STRIKE!]" else ""}"

            _combatState.value = _combatState.value?.copy(
                enemy = updatedEnemy,
                heroLungeAnimation = false,
                enemyFlashHit = true,
                floatingTexts = listOf(floating),
                combatLog = (listOf(log) + state.combatLog).take(6)
            )

            delay(350)
            _combatState.value = _combatState.value?.copy(enemyFlashHit = false)

            if (newEnemyHp <= 0) {
                handleBattleVictory()
            } else {
                executeEnemyTurn()
            }
        }
    }

    fun executeHeroSkill(skill: Skill) {
        val state = _combatState.value ?: return
        if (state.phase != BattlePhase.PLAYER_TURN) return
        if (state.hero.currentMp < skill.manaCost) {
            statusMessage.value = "Not enough MP!"
            return
        }

        viewModelScope.launch {
            val newMp = state.hero.currentMp - skill.manaCost
            val updatedHeroWithMp = state.hero.copy(currentMp = newMp)

            _combatState.value = state.copy(
                hero = updatedHeroWithMp,
                phase = BattlePhase.PLAYER_ANIMATING,
                heroLungeAnimation = true
            )

            sounds.play(SoundManager.SoundEffect.SPELL)
            haptics.trigger(HapticPulseType.SPELL_CAST)
            delay(200)

            when (skill.type) {
                SkillType.ACTIVE_ATTACK -> {
                    val baseAtk = state.hero.attack * skill.powerMultiplier
                    val damage = (baseAtk - (state.enemy.defense * 0.3f)).toInt().coerceAtLeast(12)
                    val newEnemyHp = (state.enemy.currentHp - damage).coerceAtLeast(0)
                    val updatedEnemy = state.enemy.copy(currentHp = newEnemyHp)

                    val isBurnOrPoison = skill.id.contains("pyro") || skill.id.contains("poison")
                    val floating = FloatingCombatText(
                        text = "${skill.name}! -$damage",
                        type = FloatingEffectType.MAGIC_DAMAGE,
                        isTargetHero = false
                    )

                    _combatState.value = _combatState.value?.copy(
                        enemy = updatedEnemy,
                        heroLungeAnimation = false,
                        enemyFlashHit = true,
                        enemyBurnTurns = if (skill.id.contains("pyro")) 3 else state.enemyBurnTurns,
                        enemyPoisonTurns = if (skill.id.contains("poison")) 3 else state.enemyPoisonTurns,
                        floatingTexts = listOf(floating),
                        combatLog = (listOf("Hero cast ${skill.name} dealing $damage arcane damage!") + state.combatLog).take(6)
                    )
                    delay(350)
                    _combatState.value = _combatState.value?.copy(enemyFlashHit = false)

                    if (newEnemyHp <= 0) {
                        handleBattleVictory()
                    } else {
                        executeEnemyTurn()
                    }
                }

                SkillType.ACTIVE_HEAL -> {
                    val healAmount = (state.hero.maxHp * skill.powerMultiplier).toInt().coerceAtLeast(30)
                    val newHp = (state.hero.currentHp + healAmount).coerceAtMost(state.hero.maxHp)
                    val updatedHero = updatedHeroWithMp.copy(currentHp = newHp)

                    sounds.play(SoundManager.SoundEffect.HEAL)
                    val floating = FloatingCombatText("+$healAmount HP", FloatingEffectType.HEAL, isTargetHero = true)

                    _combatState.value = _combatState.value?.copy(
                        hero = updatedHero,
                        heroLungeAnimation = false,
                        floatingTexts = listOf(floating),
                        combatLog = (listOf("Hero healed for $healAmount HP with ${skill.name}!") + state.combatLog).take(6)
                    )
                    delay(400)
                    executeEnemyTurn()
                }

                SkillType.ACTIVE_BUFF -> {
                    val floating = FloatingCombatText("DEF UP!", FloatingEffectType.STATUS_EFFECT, isTargetHero = true)
                    _combatState.value = _combatState.value?.copy(
                        heroLungeAnimation = false,
                        heroDefBuffTurns = 3,
                        floatingTexts = listOf(floating),
                        combatLog = (listOf("Hero activated ${skill.name} defense ward!") + state.combatLog).take(6)
                    )
                    delay(400)
                    executeEnemyTurn()
                }

                SkillType.PASSIVE -> {
                    // Passives are applied statically
                    _combatState.value = _combatState.value?.copy(heroLungeAnimation = false)
                    executeEnemyTurn()
                }
            }
        }
    }

    fun executeHeroDefend() {
        val state = _combatState.value ?: return
        if (state.phase != BattlePhase.PLAYER_TURN) return

        viewModelScope.launch {
            sounds.play(SoundManager.SoundEffect.DEFEND)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)

            // Defending restores 10 MP and gives 50% damage reduction for next enemy hit
            val recoveredMp = (state.hero.currentMp + 12).coerceAtMost(state.hero.maxMp)
            val updatedHero = state.hero.copy(currentMp = recoveredMp)

            _combatState.value = state.copy(
                hero = updatedHero,
                isHeroDefending = true,
                floatingTexts = listOf(FloatingCombatText("+12 MP Guard", FloatingEffectType.STATUS_EFFECT, isTargetHero = true)),
                combatLog = (listOf("Hero takes defensive stance (+12 MP, 50% DMG Resist)") + state.combatLog).take(6)
            )
            delay(500)
            executeEnemyTurn()
        }
    }

    fun useBattleItem(item: Item) {
        val state = _combatState.value ?: return
        if (state.phase != BattlePhase.PLAYER_TURN) return

        viewModelScope.launch {
            val consumed = repository.consumeItem(item.id)
            if (!consumed) {
                statusMessage.value = "No ${item.name} remaining!"
                return@launch
            }

            when (item.type) {
                ItemType.POTION_HP -> {
                    val heal = item.value
                    val newHp = (state.hero.currentHp + heal).coerceAtMost(state.hero.maxHp)
                    sounds.play(SoundManager.SoundEffect.HEAL)
                    haptics.trigger(HapticPulseType.BUTTON_CLICK)
                    _combatState.value = state.copy(
                        hero = state.hero.copy(currentHp = newHp),
                        floatingTexts = listOf(FloatingCombatText("+$heal HP", FloatingEffectType.HEAL, true)),
                        combatLog = (listOf("Used ${item.name} (+${heal} HP)!") + state.combatLog).take(6)
                    )
                }
                ItemType.POTION_MP -> {
                    val mp = item.value
                    val newMp = (state.hero.currentMp + mp).coerceAtMost(state.hero.maxMp)
                    sounds.play(SoundManager.SoundEffect.HEAL)
                    _combatState.value = state.copy(
                        hero = state.hero.copy(currentMp = newMp),
                        floatingTexts = listOf(FloatingCombatText("+$mp MP", FloatingEffectType.MAGIC_DAMAGE, true)),
                        combatLog = (listOf("Used ${item.name} (+${mp} MP)!") + state.combatLog).take(6)
                    )
                }
                ItemType.ELIXIR_FULL -> {
                    sounds.play(SoundManager.SoundEffect.HEAL)
                    _combatState.value = state.copy(
                        hero = state.hero.copy(currentHp = state.hero.maxHp, currentMp = state.hero.maxMp),
                        floatingTexts = listOf(FloatingCombatText("FULL RESTORE", FloatingEffectType.HEAL, true)),
                        combatLog = (listOf("Used Ambrosia: Full HP & MP restored!") + state.combatLog).take(6)
                    )
                }
                ItemType.BOMB_FIRE -> {
                    val dmg = item.value
                    val newEnemyHp = (state.enemy.currentHp - dmg).coerceAtLeast(0)
                    sounds.play(SoundManager.SoundEffect.CRITICAL)
                    haptics.trigger(HapticPulseType.CRITICAL_HIT)
                    _combatState.value = state.copy(
                        enemy = state.enemy.copy(currentHp = newEnemyHp),
                        enemyFlashHit = true,
                        floatingTexts = listOf(FloatingCombatText("BOMB! -$dmg", FloatingEffectType.CRITICAL_DAMAGE, false)),
                        combatLog = (listOf("Hurled Fire Bomb dealing $dmg explosive damage!") + state.combatLog).take(6)
                    )
                    delay(300)
                    _combatState.value = _combatState.value?.copy(enemyFlashHit = false)
                    if (newEnemyHp <= 0) {
                        handleBattleVictory()
                        return@launch
                    }
                }
                ItemType.REVIVE_STONE -> {}
            }
            delay(500)
            executeEnemyTurn()
        }
    }

    private fun executeEnemyTurn() {
        val state = _combatState.value ?: return

        viewModelScope.launch {
            _combatState.value = state.copy(phase = BattlePhase.ENEMY_TURN)
            delay(400)

            // Check if enemy stunned
            if (state.isEnemyStunned) {
                _combatState.value = state.copy(
                    isEnemyStunned = false,
                    phase = BattlePhase.PLAYER_TURN,
                    combatLog = (listOf("${state.enemy.name} is stunned and cannot act!") + state.combatLog).take(6)
                )
                return@launch
            }

            // Animate Enemy Lunge
            _combatState.value = _combatState.value?.copy(
                phase = BattlePhase.ENEMY_ANIMATING,
                enemyLungeAnimation = true
            )
            delay(150)

            // Enemy Attack Logic
            val useSkill = state.enemy.skills.isNotEmpty() && Random.nextFloat() < 0.4f
            val skill = if (useSkill) state.enemy.skills.random() else null
            val mult = skill?.powerMultiplier ?: 1.0f

            val rawEnemyAtk = state.enemy.attack * mult
            val heroDef = state.hero.defense + (if (state.heroDefBuffTurns > 0) 10 else 0)
            val mitigated = rawEnemyAtk - (heroDef * 0.4f)
            val defendFactor = if (state.isHeroDefending) 0.5f else 1.0f
            val finalDmg = ((mitigated * defendFactor).toInt() + Random.nextInt(-1, 2)).coerceAtLeast(6)

            val newHeroHp = (state.hero.currentHp - finalDmg).coerceAtLeast(0)
            val updatedHero = state.hero.copy(currentHp = newHeroHp)

            sounds.play(SoundManager.SoundEffect.ATTACK)
            haptics.trigger(HapticPulseType.DAMAGE_TAKEN)

            val actionName = skill?.name ?: "Attack"
            val log = "${state.enemy.name} uses $actionName dealing $finalDmg damage!"
            val floating = FloatingCombatText("-$finalDmg", FloatingEffectType.PHYSICAL_DAMAGE, isTargetHero = true)

            _combatState.value = _combatState.value?.copy(
                hero = updatedHero,
                enemyLungeAnimation = false,
                heroFlashHit = true,
                floatingTexts = listOf(floating),
                combatLog = (listOf(log) + state.combatLog).take(6)
            )

            delay(350)
            _combatState.value = _combatState.value?.copy(heroFlashHit = false)

            if (newHeroHp <= 0) {
                handleBattleDefeat()
            } else {
                // Next turn setup
                _combatState.value = _combatState.value?.copy(
                    phase = BattlePhase.PLAYER_TURN,
                    currentTurn = state.currentTurn + 1,
                    isHeroDefending = false,
                    heroAtkBuffTurns = (state.heroAtkBuffTurns - 1).coerceAtLeast(0),
                    heroDefBuffTurns = (state.heroDefBuffTurns - 1).coerceAtLeast(0)
                )
            }
        }
    }

    private fun handleBattleVictory() {
        val state = _combatState.value ?: return

        viewModelScope.launch {
            sounds.play(SoundManager.SoundEffect.VICTORY)
            haptics.trigger(HapticPulseType.VICTORY_FANFARE)

            val gold = state.stage.rewardGold
            val xp = state.enemy.xpReward
            val gems = state.stage.rewardGems

            val newlyUnlocked = repository.recordBattleVictory(
                stageNumber = state.stage.stageNumber,
                goldEarned = gold,
                xpEarned = xp,
                gemsEarned = gems,
                activeHeroId = state.hero.id
            )

            val unlockNotice = newlyUnlocked.firstOrNull()

            _combatState.value = state.copy(
                phase = BattlePhase.VICTORY,
                rewardsEarned = BattleRewards(
                    gold = if (profile.value?.isPaidVersion == true) gold * 2 else gold,
                    xp = xp,
                    gems = gems,
                    newlyUnlockedHero = unlockNotice
                ),
                combatLog = (listOf("VICTORY! Enemy vanquished. Rewards gathered.") + state.combatLog).take(6)
            )
        }
    }

    private fun handleBattleDefeat() {
        val state = _combatState.value ?: return

        viewModelScope.launch {
            sounds.play(SoundManager.SoundEffect.DEFEAT)
            haptics.trigger(HapticPulseType.DEFEAT_RUMBLE)
            repository.recordBattleDefeat()

            _combatState.value = state.copy(
                phase = BattlePhase.DEFEAT,
                combatLog = (listOf("DEFEAT! Your hero has fallen in battle.") + state.combatLog).take(6)
            )
        }
    }

    fun endBattle() {
        _combatState.value = null
    }

    fun getConstructedHero(heroId: HeroId): Hero {
        val progress = heroesProgress.value.find { it.heroId == heroId.name }
        val level = progress?.level ?: 1
        val isUnlocked = progress?.isUnlocked ?: (heroId == HeroId.KNIGHT)
        val template = HeroTemplates.createHero(heroId, isUnlocked = isUnlocked, level = level)
        val allocatedSkills = progress?.allocatedSkillIds?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        val equippedSkin = progress?.equippedSkinId ?: template.equippedSkinId

        return template.copy(
            isUnlocked = isUnlocked,
            level = level,
            xp = progress?.xp ?: 0,
            equippedSkinId = equippedSkin,
            skillPoints = progress?.skillPoints ?: 0,
            allocatedSkillIds = allocatedSkills
        )
    }

    fun allocateSkillPoint(heroId: HeroId, skillId: String) {
        viewModelScope.launch {
            repository.allocateSkill(heroId, skillId)
            sounds.play(SoundManager.SoundEffect.BUTTON)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
        }
    }

    fun resetSkillTree(heroId: HeroId) {
        viewModelScope.launch {
            repository.resetSkills(heroId)
            sounds.play(SoundManager.SoundEffect.BUTTON)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
        }
    }

    fun switchActiveHero(heroId: HeroId) {
        viewModelScope.launch {
            repository.setActiveHero(heroId)
            sounds.play(SoundManager.SoundEffect.BUTTON)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
        }
    }

    fun equipSkin(heroId: HeroId, skinId: String) {
        viewModelScope.launch {
            repository.equipSkin(heroId, skinId)
            sounds.play(SoundManager.SoundEffect.BUTTON)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
        }
    }

    fun buyShopItem(item: Item, quantity: Int = 1) {
        viewModelScope.launch {
            val success = repository.buyItem(item, quantity)
            if (success) {
                sounds.play(SoundManager.SoundEffect.BUTTON)
                haptics.trigger(HapticPulseType.BUTTON_CLICK)
                statusMessage.value = "Purchased ${item.name}!"
            } else {
                statusMessage.value = "Not enough gold!"
            }
        }
    }

    // Paid / Pro Edition Activation (UPI to oviyahareesh16-1@okicici)
    fun activatePaidVersion(upiRef: String?) {
        viewModelScope.launch {
            repository.setPaidVersion(true, upiRef)
            sounds.play(SoundManager.SoundEffect.VICTORY)
            haptics.trigger(HapticPulseType.VICTORY_FANFARE)
            statusMessage.value = "Intrast Pro Edition Activated! Cloud Saving & Expanded Trees Unlocked!"
            isUpgradeDialogOpen.value = false
        }
    }

    fun triggerCloudSave() {
        viewModelScope.launch {
            repository.createCloudSave(slotId = 1)
            sounds.play(SoundManager.SoundEffect.BUTTON)
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
            statusMessage.value = "Progress successfully synced to Cloud!"
        }
    }

    fun toggleHaptics() {
        val p = profile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(p.copy(hapticsEnabled = !p.hapticsEnabled))
        }
    }

    fun cycleHapticIntensity() {
        val p = profile.value ?: return
        val next = when (p.hapticIntensity) {
            "LIGHT" -> "MEDIUM"
            "MEDIUM" -> "HEAVY"
            else -> "LIGHT"
        }
        viewModelScope.launch {
            repository.updateProfile(p.copy(hapticIntensity = next))
            haptics.intensity = next
            haptics.trigger(HapticPulseType.BUTTON_CLICK)
        }
    }

    fun toggleSound() {
        val p = profile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(p.copy(soundEnabled = !p.soundEnabled))
        }
    }

    fun toggleScanlines() {
        val p = profile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(p.copy(crtScanlines = !p.crtScanlines))
        }
    }
}
