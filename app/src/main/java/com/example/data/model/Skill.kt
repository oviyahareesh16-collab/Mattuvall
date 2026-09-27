package com.example.data.model

enum class SkillBranch(val title: String) {
    ASSAULT("Assault"),
    BASTION("Bastion"),
    ARCANA("Arcana")
}

enum class SkillType {
    ACTIVE_ATTACK,
    ACTIVE_HEAL,
    ACTIVE_BUFF,
    PASSIVE
}

data class Skill(
    val id: String,
    val heroId: HeroId,
    val name: String,
    val description: String,
    val branch: SkillBranch,
    val tier: Int, // 1 to 5
    val type: SkillType,
    val manaCost: Int = 0,
    val cooldownTurns: Int = 0,
    val powerMultiplier: Float = 1.0f,
    val isExpandedPaidOnly: Boolean = false,
    val requiredSkillId: String? = null,
    val effectDuration: Int = 0
)

object SkillDatabase {
    fun getSkillsForHero(heroId: HeroId): List<Skill> {
        return when (heroId) {
            HeroId.KNIGHT -> listOf(
                // Assault Branch
                Skill("k_a1", heroId, "Power Strike", "A crushing blow dealing 140% physical damage", SkillBranch.ASSAULT, 1, SkillType.ACTIVE_ATTACK, manaCost = 10, powerMultiplier = 1.4f),
                Skill("k_a2", heroId, "Blade Fury", "Cleave enemies with 180% damage and bleed", SkillBranch.ASSAULT, 2, SkillType.ACTIVE_ATTACK, manaCost = 18, powerMultiplier = 1.8f, requiredSkillId = "k_a1"),
                Skill("k_a3", heroId, "Executioner", "Passive: +20% Critical Damage on foes below 40% HP", SkillBranch.ASSAULT, 3, SkillType.PASSIVE, isExpandedPaidOnly = false, requiredSkillId = "k_a2"),
                // Paid Expanded Assault Tiers
                Skill("k_a4", heroId, "Dragon Sunder [EXPANDED]", "Devastating slash dealing 260% armor-piercing damage", SkillBranch.ASSAULT, 4, SkillType.ACTIVE_ATTACK, manaCost = 30, powerMultiplier = 2.6f, isExpandedPaidOnly = true, requiredSkillId = "k_a3"),
                Skill("k_a5", heroId, "Titan Wrath [MYTHIC]", "Ultimate: Deals 350% damage and grants permanent ATK boost", SkillBranch.ASSAULT, 5, SkillType.ACTIVE_ATTACK, manaCost = 45, powerMultiplier = 3.5f, isExpandedPaidOnly = true, requiredSkillId = "k_a4"),

                // Bastion Branch
                Skill("k_b1", heroId, "Iron Shield", "Raise shield to gain 40% DEF and temporary shield", SkillBranch.BASTION, 1, SkillType.ACTIVE_BUFF, manaCost = 12, effectDuration = 3),
                Skill("k_b2", heroId, "Taunt & Brace", "Draw enemy focus and heal 25% max HP", SkillBranch.BASTION, 2, SkillType.ACTIVE_HEAL, manaCost = 20, powerMultiplier = 0.25f, requiredSkillId = "k_b1"),
                Skill("k_b3", heroId, "Unbreakable", "Passive: Taking lethal damage leaves you at 1 HP once per battle", SkillBranch.BASTION, 3, SkillType.PASSIVE, isExpandedPaidOnly = false, requiredSkillId = "k_b2"),
                // Paid Expanded Bastion Tiers
                Skill("k_b4", heroId, "Aegis Fortress [EXPANDED]", "Reflect 50% of incoming damage back to attackers for 2 turns", SkillBranch.BASTION, 4, SkillType.ACTIVE_BUFF, manaCost = 25, isExpandedPaidOnly = true, requiredSkillId = "k_b3"),
                Skill("k_b5", heroId, "Immortal Vanguard [MYTHIC]", "Passive: Regenerate 10% Max HP each turn + 30% resistance", SkillBranch.BASTION, 5, SkillType.PASSIVE, isExpandedPaidOnly = true, requiredSkillId = "k_b4"),

                // Arcana Branch
                Skill("k_c1", heroId, "Battle Cry", "Inspire courage, boosting ATK by 25% for 3 turns", SkillBranch.ARCANA, 1, SkillType.ACTIVE_BUFF, manaCost = 15, effectDuration = 3),
                Skill("k_c2", heroId, "Holy Shockwave", "Blasts all enemies with radiant light (130% damage)", SkillBranch.ARCANA, 2, SkillType.ACTIVE_ATTACK, manaCost = 22, powerMultiplier = 1.3f, requiredSkillId = "k_c1"),
                Skill("k_c3", heroId, "Righteous Aura", "Passive: Normal attacks generate +10 MP", SkillBranch.ARCANA, 3, SkillType.PASSIVE, requiredSkillId = "k_c2"),
                // Paid Expanded Arcana Tiers
                Skill("k_c4", heroId, "Solar Eclipse [EXPANDED]", "Blinds enemy boss, reducing their hit rate by 50%", SkillBranch.ARCANA, 4, SkillType.ACTIVE_BUFF, manaCost = 28, isExpandedPaidOnly = true, requiredSkillId = "k_c3"),
                Skill("k_c5", heroId, "Excalibur Descent [MYTHIC]", "Calls down celestial sword dealing 400% holy damage", SkillBranch.ARCANA, 5, SkillType.ACTIVE_ATTACK, manaCost = 50, powerMultiplier = 4.0f, isExpandedPaidOnly = true, requiredSkillId = "k_c4")
            )

            HeroId.MAGE -> listOf(
                // Assault (Flames)
                Skill("m_a1", heroId, "Fireball", "Hurl flaming sphere dealing 160% fire damage", SkillBranch.ASSAULT, 1, SkillType.ACTIVE_ATTACK, manaCost = 15, powerMultiplier = 1.6f),
                Skill("m_a2", heroId, "Pyroclasm", "Causes continuous burn dealing 30% damage for 3 turns", SkillBranch.ASSAULT, 2, SkillType.ACTIVE_ATTACK, manaCost = 25, powerMultiplier = 2.0f, requiredSkillId = "m_a1"),
                Skill("m_a3", heroId, "Flame Affinity", "Passive: +25% Fire Spell damage", SkillBranch.ASSAULT, 3, SkillType.PASSIVE, requiredSkillId = "m_a2"),
                Skill("m_a4", heroId, "Meteor Strike [EXPANDED]", "Summons apocalyptic meteor for 300% AoE fire damage", SkillBranch.ASSAULT, 4, SkillType.ACTIVE_ATTACK, manaCost = 45, powerMultiplier = 3.0f, isExpandedPaidOnly = true, requiredSkillId = "m_a3"),
                Skill("m_a5", heroId, "Supernova [MYTHIC]", "Ultimate: 450% damage and resets all spell cooldowns", SkillBranch.ASSAULT, 5, SkillType.ACTIVE_ATTACK, manaCost = 60, powerMultiplier = 4.5f, isExpandedPaidOnly = true, requiredSkillId = "m_a4"),

                // Bastion (Frost)
                Skill("m_b1", heroId, "Ice Barrier", "Conjures frost shield absorbing incoming damage", SkillBranch.BASTION, 1, SkillType.ACTIVE_BUFF, manaCost = 16, effectDuration = 3),
                Skill("m_b2", heroId, "Frost Nova", "Freezes enemy, skipping their next action turn", SkillBranch.BASTION, 2, SkillType.ACTIVE_ATTACK, manaCost = 28, powerMultiplier = 1.2f, requiredSkillId = "m_b1"),
                Skill("m_b3", heroId, "Glacial Shell", "Passive: Reduces damage taken by 20% while barrier active", SkillBranch.BASTION, 3, SkillType.PASSIVE, requiredSkillId = "m_b2"),
                Skill("m_b4", heroId, "Absolute Zero [EXPANDED]", "Encases enemy in ice, stunning for 2 turns and 200% shatter", SkillBranch.BASTION, 4, SkillType.ACTIVE_ATTACK, manaCost = 40, powerMultiplier = 2.0f, isExpandedPaidOnly = true, requiredSkillId = "m_b3"),
                Skill("m_b5", heroId, "Cryo Mastery [MYTHIC]", "Passive: Freezing triggers an instant free mana refund", SkillBranch.BASTION, 5, SkillType.PASSIVE, isExpandedPaidOnly = true, requiredSkillId = "m_b4"),

                // Arcana (Lightning & Void)
                Skill("m_c1", heroId, "Arcane Bolt", "Swift bolt with 100% accuracy and +5 MP gain", SkillBranch.ARCANA, 1, SkillType.ACTIVE_ATTACK, manaCost = 8, powerMultiplier = 1.2f),
                Skill("m_c2", heroId, "Chain Lightning", "Electrocutes target with 210% critical electric shock", SkillBranch.ARCANA, 2, SkillType.ACTIVE_ATTACK, manaCost = 26, powerMultiplier = 2.1f, requiredSkillId = "m_c1"),
                Skill("m_c3", heroId, "Mana Well", "Passive: Max MP +40 and MP regen +15 each turn", SkillBranch.ARCANA, 3, SkillType.PASSIVE, requiredSkillId = "m_c2"),
                Skill("m_c4", heroId, "Time Warp [EXPANDED]", "Take 2 consecutive turns immediately in combat", SkillBranch.ARCANA, 4, SkillType.ACTIVE_BUFF, manaCost = 50, isExpandedPaidOnly = true, requiredSkillId = "m_c3"),
                Skill("m_c5", heroId, "Singularity [MYTHIC]", "Opens black hole draining 15% enemy max HP per turn", SkillBranch.ARCANA, 5, SkillType.ACTIVE_ATTACK, manaCost = 65, powerMultiplier = 3.8f, isExpandedPaidOnly = true, requiredSkillId = "m_c4")
            )

            HeroId.ROGUE -> listOf(
                Skill("r_a1", heroId, "Quick Slash", "Rapid double-strike dealing 135% damage", SkillBranch.ASSAULT, 1, SkillType.ACTIVE_ATTACK, manaCost = 10, powerMultiplier = 1.35f),
                Skill("r_a2", heroId, "Shadow Strike", "High crit attack dealing 220% damage from shadows", SkillBranch.ASSAULT, 2, SkillType.ACTIVE_ATTACK, manaCost = 20, powerMultiplier = 2.2f, requiredSkillId = "r_a1"),
                Skill("r_a3", heroId, "Lethal Precision", "Passive: Base critical strike chance increased to 35%", SkillBranch.ASSAULT, 3, SkillType.PASSIVE, requiredSkillId = "r_a2"),
                Skill("r_a4", heroId, "Assassinate [EXPANDED]", "Executes low-health foes with 320% burst damage", SkillBranch.ASSAULT, 4, SkillType.ACTIVE_ATTACK, manaCost = 35, powerMultiplier = 3.2f, isExpandedPaidOnly = true, requiredSkillId = "r_a3"),
                Skill("r_a5", heroId, "Omnislash [MYTHIC]", "Hits 5 consecutive times for massive 420% total damage", SkillBranch.ASSAULT, 5, SkillType.ACTIVE_ATTACK, manaCost = 50, powerMultiplier = 4.2f, isExpandedPaidOnly = true, requiredSkillId = "r_a4"),

                Skill("r_b1", heroId, "Smoke Bomb", "Escape targeting and gain 50% dodge chance", SkillBranch.BASTION, 1, SkillType.ACTIVE_BUFF, manaCost = 14, effectDuration = 2),
                Skill("r_b2", heroId, "Evasion Step", "Dodging an attack counters with 100% damage strike", SkillBranch.BASTION, 2, SkillType.PASSIVE, requiredSkillId = "r_b1"),
                Skill("r_b3", heroId, "Adrenaline Rush", "Passive: Speed +10 and heals 15 HP on dodge", SkillBranch.BASTION, 3, SkillType.PASSIVE, requiredSkillId = "r_b2"),
                Skill("r_b4", heroId, "Shadow Cloak [EXPANDED]", "Complete invulnerability to all attacks for 1 full turn", SkillBranch.BASTION, 4, SkillType.ACTIVE_BUFF, manaCost = 30, isExpandedPaidOnly = true, requiredSkillId = "r_b3"),
                Skill("r_b5", heroId, "Ghost Walk [MYTHIC]", "Passive: 25% passive chance to avoid ANY damage entirely", SkillBranch.BASTION, 5, SkillType.PASSIVE, isExpandedPaidOnly = true, requiredSkillId = "r_b4"),

                Skill("r_c1", heroId, "Poison Vial", "Infect enemy weapon with neurotoxin dealing poison", SkillBranch.ARCANA, 1, SkillType.ACTIVE_ATTACK, manaCost = 12, powerMultiplier = 1.1f),
                Skill("r_c2", heroId, "Mug & Steal", "Steal extra Gold & Potions during battle while attacking", SkillBranch.ARCANA, 2, SkillType.ACTIVE_ATTACK, manaCost = 15, powerMultiplier = 1.3f, requiredSkillId = "r_c1"),
                Skill("r_c3", heroId, "Venom Mastery", "Passive: Poison ticks 50% stronger and reduces enemy ATK", SkillBranch.ARCANA, 3, SkillType.PASSIVE, requiredSkillId = "r_c2"),
                Skill("r_c4", heroId, "Fatal Lotus [EXPANDED]", "Detonates all active poison stacks for 280% burst", SkillBranch.ARCANA, 4, SkillType.ACTIVE_ATTACK, manaCost = 32, powerMultiplier = 2.8f, isExpandedPaidOnly = true, requiredSkillId = "r_c3"),
                Skill("r_c5", heroId, "Reaper Harvest [MYTHIC]", "Defeating an enemy grants 300 bonus gold & instant MP", SkillBranch.ARCANA, 5, SkillType.PASSIVE, isExpandedPaidOnly = true, requiredSkillId = "r_c4")
            )

            HeroId.PALADIN, HeroId.NECROMANCER, HeroId.VALKYRIE -> listOf(
                Skill("${heroId.name.lowercase()}_a1", heroId, "Holy Sunder", "Smite with 150% radiant physical strike", SkillBranch.ASSAULT, 1, SkillType.ACTIVE_ATTACK, manaCost = 12, powerMultiplier = 1.5f),
                Skill("${heroId.name.lowercase()}_a2", heroId, "Divine Brand", "Brands target, dealing 210% damage", SkillBranch.ASSAULT, 2, SkillType.ACTIVE_ATTACK, manaCost = 22, powerMultiplier = 2.1f, requiredSkillId = "${heroId.name.lowercase()}_a1"),
                Skill("${heroId.name.lowercase()}_a3", heroId, "Zealot Heart", "Passive: +20% damage when below 50% HP", SkillBranch.ASSAULT, 3, SkillType.PASSIVE, requiredSkillId = "${heroId.name.lowercase()}_a2"),
                Skill("${heroId.name.lowercase()}_a4", heroId, "Wrath of Heavens [EXPANDED]", "Crushing blow dealing 290% divine damage", SkillBranch.ASSAULT, 4, SkillType.ACTIVE_ATTACK, manaCost = 38, powerMultiplier = 2.9f, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_a3"),
                Skill("${heroId.name.lowercase()}_a5", heroId, "Judgment Dawn [MYTHIC]", "Ultimate: 380% holy damage and heals party to full", SkillBranch.ASSAULT, 5, SkillType.ACTIVE_ATTACK, manaCost = 55, powerMultiplier = 3.8f, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_a4"),

                Skill("${heroId.name.lowercase()}_b1", heroId, "Radiant Grace", "Heals 35% HP and removes debuffs", SkillBranch.BASTION, 1, SkillType.ACTIVE_HEAL, manaCost = 18, powerMultiplier = 0.35f),
                Skill("${heroId.name.lowercase()}_b2", heroId, "Sanctuary", "Create holy dome reducing all damage by 40%", SkillBranch.BASTION, 2, SkillType.ACTIVE_BUFF, manaCost = 25, effectDuration = 3, requiredSkillId = "${heroId.name.lowercase()}_b1"),
                Skill("${heroId.name.lowercase()}_b3", heroId, "Blessed Armor", "Passive: +15 Defense and status resistance", SkillBranch.BASTION, 3, SkillType.PASSIVE, requiredSkillId = "${heroId.name.lowercase()}_b2"),
                Skill("${heroId.name.lowercase()}_b4", heroId, "Guardian Angel [EXPANDED]", "Immunity to all damage for 1 turn and +50% ATK next turn", SkillBranch.BASTION, 4, SkillType.ACTIVE_BUFF, manaCost = 35, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_b3"),
                Skill("${heroId.name.lowercase()}_b5", heroId, "Eternal Grace [MYTHIC]", "Passive: Auto-resurrection with 100% HP once per run", SkillBranch.BASTION, 5, SkillType.PASSIVE, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_b4"),

                Skill("${heroId.name.lowercase()}_c1", heroId, "Smite Miasma", "Purge dark forces with 130% beam", SkillBranch.ARCANA, 1, SkillType.ACTIVE_ATTACK, manaCost = 14, powerMultiplier = 1.3f),
                Skill("${heroId.name.lowercase()}_c2", heroId, "Solar Blessing", "Buffs Attack and Critical by 30%", SkillBranch.ARCANA, 2, SkillType.ACTIVE_BUFF, manaCost = 20, effectDuration = 3, requiredSkillId = "${heroId.name.lowercase()}_c1"),
                Skill("${heroId.name.lowercase()}_c3", heroId, "Consecrated Ground", "Passive: Enemies deal 15% less damage", SkillBranch.ARCANA, 3, SkillType.PASSIVE, requiredSkillId = "${heroId.name.lowercase()}_c2"),
                Skill("${heroId.name.lowercase()}_c4", heroId, "Solar Flare [EXPANDED]", "Blinds and burns for 250% total damage", SkillBranch.ARCANA, 4, SkillType.ACTIVE_ATTACK, manaCost = 35, powerMultiplier = 2.5f, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_c3"),
                Skill("${heroId.name.lowercase()}_c5", heroId, "Archon Ascension [MYTHIC]", "Transform into Divine Celestial: +50% all stats", SkillBranch.ARCANA, 5, SkillType.ACTIVE_BUFF, manaCost = 60, isExpandedPaidOnly = true, requiredSkillId = "${heroId.name.lowercase()}_c4")
            )
        }
    }
}
