package com.example.simongame

import kotlinx.coroutines.flow.Flow

// GameRepository is created to abstract the data source so the ViewModel doesn't interact directly with the DAO
class GameRepository(private val gameDao: GameDao) {
    // Exposes read-only data as an observable Flow
    fun getAllGames(): Flow<List<GameEntity>> = gameDao.getAllGames()

    // Inserts a new game into the database.
    // Marked as a suspend function to ensure database consistency and prevent blocking the main UI thread
    suspend fun insert(game: GameEntity) {
        gameDao.insert(game)
    }
}