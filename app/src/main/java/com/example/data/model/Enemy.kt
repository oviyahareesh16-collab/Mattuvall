package com.example.data.model

enum class EnemyType {
    SLIME,
    GOBLIN,
    SKELETON,
    ORC,
    SHADOW_SPECTER,
    FLAME_DRAKE,
    VOID_DEMON,
    LICH_KING
}

data class EnemySkill(
    val name: String,
    val powerMultiplier: Float = 1.2f,
    val description: String,
    val isDebuff: Boolean = false
)

data class Enemy(
    val id: String,
    val name: String,
    val type: EnemyType,
    val level: Int,
    val maxHp: Int,
    val currentHp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val xpReward: Int,
    val goldReward: Int,
    val isBoss: Boolean = false,
    val skills: List<EnemySkill> = emptyList()
) {
    val isAlive: Boolean get() = currentHp > 0
}

object EnemyTemplates {
    fun createEnemy(type: EnemyType, level: Int, isBoss: Boolean = false): Enemy {
        val baseMultiplier = 1.0f + (level - 1) * 0.22f
        val bossMult = if (isBoss) 2.4f else 1.0f

        val (name, hpBase, atkBase, defBase, spdBase) = when (type) {
            EnemyType.SLIME -> listOf("Acid Slime", 45, 10, 4, 8)
            EnemyType.GOBLIN -> listOf("Goblin Scout", 60, 14, 6, 12)
            EnemyType.SKELETON -> listOf("Skeleton Knight", 85, 18, 10, 10)
            EnemyType.ORC -> listOf("Orc Berserker", 120, 24, 12, 9)
            EnemyType.SHADOW_SPECTER -> listOf("Shadow Specter", 90, 28, 8, 16)
            EnemyType.FLAME_DRAKE -> listOf(if (isBoss) "Infernal Drake Boss" else "Flame Drake", 200, 32, 16, 14)
            EnemyType.VOID_DEMON -> listOf(if (isBoss) "Abyssal Behemoth" else "Void Fiend", 240, 38, 20, 13)
            EnemyType.LICH_KING -> listOf("Ancient Lich Sovereign", 300, 44, 18, 15)
        }

        val maxHp = ((hpBase as Int) * baseMultiplier * bossMult).toInt()
        val atk = ((atkBase as Int) * baseMultiplier * (if (isBoss) 1.3f else 1.0f)).toInt()
        val def = ((defBase as Int) * baseMultiplier).toInt()
        val spd = (spdBase as Int)

        val skills = when (type) {
            EnemyType.SLIME -> listOf(EnemySkill("Acid Spit", 1.2f, "Spits corrosive acid"))
            EnemyType.GOBLIN -> listOf(EnemySkill("Dirty Slash", 1.3f, "Sneak strike"))
            EnemyType.SKELETON -> listOf(EnemySkill("Bone Cleave", 1.4f, "Heavy bone slash"))
            EnemyType.ORC -> listOf(EnemySkill("Berserk Slam", 1.6f, "Smashes down with brute force"))
            EnemyType.SHADOW_SPECTER -> listOf(EnemySkill("Soul Drain", 1.4f, "Drains life essence"))
            EnemyType.FLAME_DRAKE -> listOf(EnemySkill("Dragon Breath", 1.8f, "Breaths blazing hellfire"))
            EnemyType.VOID_DEMON -> listOf(EnemySkill("Void Rupture", 1.9f, "Tears reality open"))
            EnemyType.LICH_KING -> listOf(
                EnemySkill("Death Nova", 2.0f, "Apocalyptic wave of necrosis"),
                EnemySkill("Curse of Agony", 1.5f, "Applies dread curse")
            )
        }

        val xp = (30 * level * (if (isBoss) 3.5f else 1.0f)).toInt()
        val gold = (25 * level * (if (isBoss) 4.0f else 1.0f)).toInt()

        return Enemy(
            id = "${type.name.lowercase()}_$level",
            name = (name as String) + if (isBoss) " [BOSS]" else "",
            type = type,
            level = level,
            maxHp = maxHp,
            currentHp = maxHp,
            attack = atk,
            defense = def,
            speed = spd,
            xpReward = xp,
            goldReward = gold,
            isBoss = isBoss,
            skills = skills
        )
    }
}
