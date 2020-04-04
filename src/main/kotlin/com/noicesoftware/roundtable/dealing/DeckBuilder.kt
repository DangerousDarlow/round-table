package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import org.springframework.stereotype.Component

@Component
class DeckBuilder {
    fun build(players: Int): Map<Character, Int> {
        val evil = when (players) {
            5 -> 2
            6 -> 2
            7 -> 3
            8 -> 3
            9 -> 3
            10 -> 4
            else -> throw Exception("Player count ($players) must be between 5 and 10 inclusive")
        }

        val good = players - evil
        return mapOf(
                Character.Servant to good - 1,
                Character.Minion to evil,
                Character.Merlin to 1
        )
    }
}