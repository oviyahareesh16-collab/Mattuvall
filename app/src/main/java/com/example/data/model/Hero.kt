package com.example.data.model

data class HeroSkin(
    val id: String,
    val name: String,
    val description: String,
    val isPaidExclusive: Boolean = false,
    val primaryColorHex: Long = 0xFF4A90E2,
    val accentColorHex: Long = 0xFFFFD700
)

enum class HeroId(
    val displayName: String,
    val roleTitle: String,
    val lore: String,
    val defaultSkin: String,
    val unlockRequirementText: String,
    val unlockStageRequired: Int = 0,
    val unlockWinsRequired: Int = 0
) {
    KNIGHT(
        displayName = "Alric the Valiant",
        roleTitle = "Iron Vanguard",
        lore = "A stalwart defender sworn to protect the realm from subterranean horrors.",
        defaultSkin = "skin_knight_iron",
        unlockRequirementText = "Unlocked by default"
    ),
    MAGE(
        displayName = "Lyra Emberheart",
        roleTitle = "Pyromancer",
        lore = "A master of blazing elemental incantations from the Arcane Spire.",
        defaultSkin = "skin_mage_crimson",
        unlockRequirementText = "Clear Realm 1 - Stage 3",
        unlockStageRequired = 3
    ),
    ROGUE(
        displayName = "Kael Shadowstep",
        roleTitle = "Silent Blade",
        lore = "A swift assassin from the underworld who strikes unseen with venom-laced daggers.",
        defaultSkin = "skin_rogue_twilight",
        unlockRequirementText = "Clear Realm 1 - Stage 5 (Boss)",
        unlockStageRequired = 5
    ),
    PALADIN(
        displayName = "Seraphina Dawn",
        roleTitle = "Sun Arbiter",
        lore = "Blessed by the solar pantheon to smite the wicked and cleanse tainted ruins.",
        defaultSkin = "skin_paladin_radiant",
        unlockRequirementText = "Win 10 Battles & Clear Stage 8",
        unlockStageRequired = 8,
        unlockWinsRequired = 10
    ),
    NECROMANCER(
        displayName = "Moros Cryptborn",
        roleTitle = "Soul Reaver",
        lore = "Channels spectral miasma to siphon enemy vitality and summon dark legions.",
        defaultSkin = "skin_necro_abyssal",
        unlockRequirementText = "Win 15 Total Battles",
        unlockWinsRequired = 15
    ),
    VALKYRIE(
        displayName = "Astrid Skybreaker",
        roleTitle = "Valkyrie Ascendant",
        lore = "A mythical warrior whose celestial spear commands thunderstorms and divine judgment.",
        defaultSkin = "skin_valkyrie_storm",
        unlockRequirementText = "Defeat Molten Drake (Stage 15)",
        unlockStageRequired = 15
    )
}

data class Hero(
    val id: HeroId,
    val name: String,
    val title: String,
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val baseHp: Int,
    val currentHp: Int,
    val maxHp: Int,
    val baseMp: Int,
    val currentMp: Int,
    val maxMp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val critRate: Float = 0.15f,
    val isUnlocked: Boolean = false,
    val equippedSkinId: String,
    val availableSkins: List<HeroSkin>,
    val skillPoints: Int = 0,
    val allocatedSkillIds: Set<String> = emptySet()
) {
    val isAlive: Boolean get() = currentHp > 0
}

object HeroTemplates {
    fun createHero(id: HeroId, isUnlocked: Boolean = false, level: Int = 1): Hero {
        val (hp, mp, atk, def, spd) = when (id) {
            HeroId.KNIGHT -> listOf(140, 40, 24, 18, 12)
            HeroId.MAGE -> listOf(85, 90, 36, 8, 14)
            HeroId.ROGUE -> listOf(100, 50, 30, 11, 20)
            HeroId.PALADIN -> listOf(160, 60, 22, 22, 10)
            HeroId.NECROMANCER -> listOf(95, 80, 32, 12, 15)
            HeroId.VALKYRIE -> listOf(130, 70, 34, 16, 17)
        }

        val skins = when (id) {
            HeroId.KNIGHT -> listOf(
                HeroSkin("skin_knight_iron", "Steel Vanguard", "Classic polished forged plate armor", false, 0xFF64748B, 0xFF38BDF8),
                HeroSkin("skin_knight_cyber", "Cyber Ronin [PRO]", "Exclusive futuristic neon power-armor with plasma blade", true, 0xFF06B6D4, 0xFFF43F5E),
                HeroSkin("skin_knight_gold", "Sun King [PRO]", "Gilded royal armor of the high crusades", true, 0xFFF59E0B, 0xFFFEF08A)
            )
            HeroId.MAGE -> listOf(
                HeroSkin("skin_mage_crimson", "Ruby Sorceress", "Traditional crimson flame wizard robes", false, 0xFFDC2626, 0xFFFCD34D),
                HeroSkin("skin_mage_starlight", "Starlight Weaver [PRO]", "Infused with constellations and stellar nebulae", true, 0xFF8B5CF6, 0xFF38BDF8)
            )
            HeroId.ROGUE -> listOf(
                HeroSkin("skin_rogue_twilight", "Shadow Strider", "Stealthy hooded leather cuirass", false, 0xFF334155, 0xFF10B981),
                HeroSkin("skin_rogue_phantom", "Phantom Reaper [PRO]", "Wraith form shrouded in ghostly blue flames", true, 0xFF1E293B, 0xFF06B6D4)
            )
            HeroId.PALADIN -> listOf(
                HeroSkin("skin_paladin_radiant", "Radiant Justiciar", "Blessed silver armor and gilded shield", false, 0xFFF59E0B, 0xFFFFFFFF),
                HeroSkin("skin_paladin_solar", "Solar Seraph [PRO]", "Wings of holy daylight and diamond plate", true, 0xFFFBBF24, 0xFFF43F5E)
            )
            HeroId.NECROMANCER -> listOf(
                HeroSkin("skin_necro_abyssal", "Blight Warden", "Bone-inlaid robes carrying the death lantern", false, 0xFF475569, 0xFF84CC16),
                HeroSkin("skin_necro_void", "Nether Sovereign [PRO]", "Cosmic entropy aura with floating skull runes", true, 0xFF6B21A8, 0xFF22C55E)
            )
            HeroId.VALKYRIE -> listOf(
                HeroSkin("skin_valkyrie_storm", "Storm Maiden", "Winged helmet and runic storm spear", false, 0xFF3B82F6, 0xFFE0E7FF),
                HeroSkin("skin_valkyrie_divine", "Celestial Archon [PRO]", "Golden battle wings radiating aurora borealis", true, 0xFFEAB308, 0xFFA855F7)
            )
        }

        val maxHealth = hp + (level - 1) * 20
        val maxMana = mp + (level - 1) * 10
        val totalAtk = atk + (level - 1) * 4
        val totalDef = def + (level - 1) * 3

        return Hero(
            id = id,
            name = id.displayName,
            title = id.roleTitle,
            level = level,
            xp = 0,
            xpToNextLevel = level * 100,
            baseHp = hp,
            currentHp = maxHealth,
            maxHp = maxHealth,
            baseMp = mp,
            currentMp = maxMana,
            maxMp = maxMana,
            attack = totalAtk,
            defense = totalDef,
            speed = spd,
            isUnlocked = isUnlocked,
            equippedSkinId = skins.first().id,
            availableSkins = skins,
            skillPoints = level - 1
        )
    }
}
