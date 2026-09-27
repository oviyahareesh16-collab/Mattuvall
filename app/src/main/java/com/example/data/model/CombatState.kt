package com.example.data.model

enum class BattlePhase {
    INTRO,
    PLAYER_TURN,
    PLAYER_ANIMATING,
    ENEMY_TURN,
    ENEMY_ANIMATING,
    VICTORY,
    DEFEAT
}

enum class FloatingEffectType {
    PHYSICAL_DAMAGE,
    CRITICAL_DAMAGE,
    MAGIC_DAMAGE,
    HEAL,
    STATUS_EFFECT,
    MISS
}

data class FloatingCombatText(
    val text: String,
    val type: FloatingEffectType,
    val isTargetHero: Boolean = false,
    val id: Long = System.nanoTime()
)

enum class HapticPulseType {
    BUTTON_CLICK,
    HERO_ATTACK,
    CRITICAL_HIT,
    DAMAGE_TAKEN,
    SPELL_CAST,
    VICTORY_FANFARE,
    DEFEAT_RUMBLE
}

data class CombatState(
    val stage: Stage,
    val hero: Hero,
    val enemy: Enemy,
    val phase: BattlePhase = BattlePhase.INTRO,
    val currentTurn: Int = 1,
    val isHeroDefending: Boolean = false,
    val isEnemyStunned: Boolean = false,
    val heroAtkBuffTurns: Int = 0,
    val heroDefBuffTurns: Int = 0,
    val enemyBurnTurns: Int = 0,
    val enemyPoisonTurns: Int = 0,
    val combatLog: List<String> = emptyList(),
    val floatingTexts: List<FloatingCombatText> = emptyList(),
    val heroActionPoints: Int = 3,
    val maxActionPoints: Int = 3,
    val lastHapticEvent: HapticPulseType? = null,
    val heroLungeAnimation: Boolean = false,
    val enemyLungeAnimation: Boolean = false,
    val heroFlashHit: Boolean = false,
    val enemyFlashHit: Boolean = false,
    val rewardsEarned: BattleRewards? = null
)

data class BattleRewards(
    val gold: Int,
    val xp: Int,
    val gems: Int,
    val newLevel: Int? = null,
    val newlyUnlockedHero: HeroId? = null
)
