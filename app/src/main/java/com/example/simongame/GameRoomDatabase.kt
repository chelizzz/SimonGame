package com.example.simongame

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

// Reference: https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room

// Singleton database providing access to the GameEntity table
@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
abstract class GameRoomDatabase : RoomDatabase() {
    // Returns the GameDao so that the database knows about the DAO
    abstract fun gameDao() : GameDao

    // The companion object allows calling getDatabase() directly on the class name (e.g., GameRoomDatabase.getDatabase())
    companion object {
        // The INSTANCE variable keeps a reference to the database, when one has been created.
        // @Volatile forces all threads to skip the cache and read/write this variable straight from the main memory.
        @Volatile
        private var INSTANCE: GameRoomDatabase? = null

        fun getDatabase(context: Context): GameRoomDatabase {
            // Returns existing instance, or lock the synchronized(this) block to safely create a new one.
            // This approach avoids multiple instances of the same database at the same time (race condition)
            return INSTANCE ?: synchronized(this) { // the companion object is passed in this
                Room.databaseBuilder(
                    context.applicationContext,
                    GameRoomDatabase::class.java,
                    "simon_game_database"
                )
                    // Compile the database...
                    .build()
                    // ...assign it to INSTANCE, and return it
                    .also { INSTANCE = it }
            }
        }
    }
}