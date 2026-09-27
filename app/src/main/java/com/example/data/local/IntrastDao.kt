package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IntrastDao {

    // Player Profile
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    // Hero Progress
    @Query("SELECT * FROM hero_progress")
    fun getAllHeroesProgress(): Flow<List<HeroProgressEntity>>

    @Query("SELECT * FROM hero_progress WHERE heroId = :heroId LIMIT 1")
    suspend fun getHeroProgressById(heroId: String): HeroProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHero(hero: HeroProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeroes(heroes: List<HeroProgressEntity>)

    // Stages Progress
    @Query("SELECT * FROM stage_progress ORDER BY stageNumber ASC")
    fun getAllStagesProgress(): Flow<List<StageProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStage(stage: StageProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStages(stages: List<StageProgressEntity>)

    // Inventory
    @Query("SELECT * FROM inventory_items")
    fun getInventoryItems(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInventoryItem(item: InventoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInventory(items: List<InventoryEntity>)

    // Cloud Saves
    @Query("SELECT * FROM cloud_saves ORDER BY slotId ASC")
    fun getCloudSaves(): Flow<List<CloudSaveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloudSave(cloudSave: CloudSaveEntity)
}
