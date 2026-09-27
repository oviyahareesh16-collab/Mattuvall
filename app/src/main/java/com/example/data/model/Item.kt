package com.example.data.model

enum class ItemType {
    POTION_HP,
    POTION_MP,
    ELIXIR_FULL,
    BOMB_FIRE,
    REVIVE_STONE
}

data class Item(
    val id: String,
    val type: ItemType,
    val name: String,
    val description: String,
    val goldPrice: Int,
    val value: Int, // e.g. amount restored or damage dealt
    val quantity: Int = 0
)

object ItemTemplates {
    val allShopItems: List<Item> = listOf(
        Item("item_hp_small", ItemType.POTION_HP, "Healing Salve", "Restores 60 HP to active hero", 40, 60),
        Item("item_hp_large", ItemType.POTION_HP, "Greater Health Potion", "Restores 150 HP to active hero", 90, 150),
        Item("item_mp_small", ItemType.POTION_MP, "Mana Vial", "Restores 40 MP for spells and skills", 45, 40),
        Item("item_mp_large", ItemType.POTION_MP, "Elixir of Arcana", "Restores 90 MP", 100, 90),
        Item("item_full_elixir", ItemType.ELIXIR_FULL, "Ambrosia Flask", "Fully restores both HP and MP", 250, 9999),
        Item("item_fire_bomb", ItemType.BOMB_FIRE, "Alchemical Fire Bomb", "Deals 120 fixed explosive damage to enemy", 80, 120),
        Item("item_revive", ItemType.REVIVE_STONE, "Phoenix Shard", "Revives a fallen hero with 50% HP", 300, 50)
    )
}
