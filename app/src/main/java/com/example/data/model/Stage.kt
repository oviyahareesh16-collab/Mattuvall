package com.example.data.model

data class Stage(
    val stageNumber: Int,
    val realmId: Int,
    val realmName: String,
    val name: String,
    val description: String,
    val recommendedLevel: Int,
    val enemyType: EnemyType,
    val isBoss: Boolean = false,
    val rewardGold: Int,
    val rewardGems: Int,
    val stars: Int = 0,
    val isCompleted: Boolean = false,
    val isUnlocked: Boolean = false
)

object StageDatabase {
    val allStages: List<Stage> = listOf(
        // Realm 1: Whispering Woods
        Stage(1, 1, "Whispering Woods", "Mossy Pathway", "Slimes and wild beasts lurk in the dense foliage.", 1, EnemyType.SLIME, false, 80, 5, isUnlocked = true),
        Stage(2, 1, "Whispering Woods", "Goblin Thicket", "Goblin sentries patrol the forest crossroads.", 2, EnemyType.GOBLIN, false, 120, 8),
        Stage(3, 1, "Whispering Woods", "Deep Mire", "Unlocks Lyra the Pyromancer upon victory!", 3, EnemyType.SLIME, false, 160, 10),
        Stage(4, 1, "Whispering Woods", "Bandit Outpost", "Armored goblins guarding the ancient gate.", 4, EnemyType.GOBLIN, false, 200, 12),
        Stage(5, 1, "Whispering Woods", "Chieftain's Hollow", "BOSS: Goblin War Chief. Defeat to unlock Kael Shadowstep!", 5, EnemyType.ORC, true, 400, 30),

        // Realm 2: Cursed Crypt
        Stage(6, 2, "Cursed Crypt", "Bone Vaults", "Ancient catacombs reeking of death and rust.", 6, EnemyType.SKELETON, false, 280, 15),
        Stage(7, 2, "Cursed Crypt", "Hall of Torment", "Restless souls and phantom specters wander here.", 7, EnemyType.SHADOW_SPECTER, false, 340, 18),
        Stage(8, 2, "Cursed Crypt", "Tomb of the Sun", "Clear stage to progress toward unlocking Paladin Seraphina!", 8, EnemyType.SKELETON, false, 420, 20),
        Stage(9, 2, "Cursed Crypt", "Crypt Depths", "Necrotic energies surge beneath the flagstones.", 9, EnemyType.SHADOW_SPECTER, false, 500, 25),
        Stage(10, 2, "Cursed Crypt", "Skeleton King's Throne", "BOSS: The Fallen Skeleton Monarch awaits your doom.", 10, EnemyType.ORC, true, 800, 50),

        // Realm 3: Molten Caldera
        Stage(11, 3, "Molten Caldera", "Obsidian Path", "Rivers of glowing lava and sulfurous steam.", 11, EnemyType.ORC, false, 600, 30),
        Stage(12, 3, "Molten Caldera", "Basalt Bridges", "Terrifying fiery beasts guard the magma forge.", 12, EnemyType.FLAME_DRAKE, false, 720, 35),
        Stage(13, 3, "Molten Caldera", "Ash Plateau", "Smoldering ashes and fierce infernal sentinels.", 13, EnemyType.ORC, false, 850, 40),
        Stage(14, 3, "Molten Caldera", "Dragon's Perch", "The caldera roars with ancient draconic fury.", 14, EnemyType.FLAME_DRAKE, false, 1000, 45),
        Stage(15, 3, "Molten Caldera", "Infernal Drake Lair", "BOSS: Infernal Drake. Defeating it unlocks Astrid the Valkyrie!", 15, EnemyType.FLAME_DRAKE, true, 1600, 100),

        // Realm 4: Void Citadel
        Stage(16, 4, "Void Citadel", "Dimensional Rift", "Reality warps as cosmic fiends slip into the world.", 16, EnemyType.VOID_DEMON, false, 1200, 60),
        Stage(17, 4, "Void Citadel", "Singularity Hall", "Gravity bends and dark matter pulses violently.", 17, EnemyType.SHADOW_SPECTER, false, 1400, 70),
        Stage(18, 4, "Void Citadel", "Astral Spire", "Cosmic titans safeguard the forbidden altar.", 18, EnemyType.VOID_DEMON, false, 1700, 80),
        Stage(19, 4, "Void Citadel", "Abyssal Gate", "Final line of defense before the Sovereign's keep.", 19, EnemyType.VOID_DEMON, false, 2000, 90),
        Stage(20, 4, "Void Citadel", "Throne of the Lich King", "FINAL BOSS: Ancient Lich Sovereign. Ascend to Mythic Legend!", 20, EnemyType.LICH_KING, true, 3500, 250)
    )
}
