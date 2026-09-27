package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val gold: Int = 250,
    val gems: Int = 30,
    val totalBattles: Int = 0,
    val totalWins: Int = 0,
    val activeHeroId: String = "KNIGHT",
    val isPaidVersion: Boolean = false,
    val upiTransactionRef: String? = null,
    val hapticsEnabled: Boolean = true,
    val hapticIntensity: String = "HEAVY", // LIGHT, MEDIUM, HEAVY
    val soundEnabled: Boolean = true,
    val crtScanlines: Boolean = true,
    val lastCloudSyncTime: Long = 0L,
    val cloudSyncId: String = "INTRAST-CLOUD-DEFAULT"
)

@Entity(tableName = "hero_progress")
data class HeroProgressEntity(
    @PrimaryKey val heroId: String,
    val isUnlocked: Boolean,
    val level: Int,
    val xp: Int,
    val equippedSkinId: String,
    val allocatedSkillIds: String, // comma-separated IDs
    val skillPoints: Int
)

@Entity(tableName = "stage_progress")
data class StageProgressEntity(
    @PrimaryKey val stageNumber: Int,
    val stars: Int,
    val isCompleted: Boolean,
    val isUnlocked: Boolean
)

@Entity(tableName = "inventory_items")
data class InventoryEntity(
    @PrimaryKey val itemId: String,
    val quantity: Int
)

@Entity(tableName = "cloud_saves")
data class CloudSaveEntity(
    @PrimaryKey val slotId: Int = 1,
    val timestamp: Long,
    val playerLevelSummary: String,
    val stageReached: Int,
    val isPaidPro: Boolean,
    val fullBackupJson: String
)
