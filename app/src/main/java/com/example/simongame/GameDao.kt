package com.example.simongame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Reference: https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room

/**
 * Data access object (DAO) is a pattern used to separate the persistence layer
 * from the rest of the application by providing an abstract interface.
 *
 * Instead of running queries on the database directly, GameDao provides the methods
 * that the rest of the app uses to interact with data in the games_history table:
 * - Getting all the games in descending order
 * - Inserting a game
 */
@Dao
interface GameDao {
    @Query("SELECT * from games_history ORDER BY id DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: GameEntity)
}