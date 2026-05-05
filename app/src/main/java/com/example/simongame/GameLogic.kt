package com.example.simongame

// Represents a "session" of a Simon Game
object Game {
    private val chars = GameConst.buColors.keys
    private var sequence = ""
    private var round = 1


    fun playComputer() : String {
        // Reference: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/random.html
        if(sequence.isEmpty())
            sequence += chars.random()
        else
            sequence = "$sequence, ${chars.random()}"

        round++

        return sequence
    }


    fun getRound() : Int { return round }
}