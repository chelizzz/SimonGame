package com.example.simongame

import androidx.room.Entity
import androidx.room.PrimaryKey

// Reference: https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room

// Each instance of GameEntity represents a row in games_history table in the app's database
@Entity(tableName = "games_history")
data class GameEntity(
    // Room will automatically generate a unique value for the primary key column
    // when a new entity instance is inserted into the database
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val sequence: String,
    val clicks: Int
)