package com.noicesoftware.roundtable.model

import org.springframework.stereotype.Component

@Component
class DeckBuilder {
    fun build(players: Int): Map<Character, Int> {
        if (players < 5 || players > 10) throw Exception("Player count ($players) must be between 5 and 10 inclusive")

        val evil = (players - 1) / 2
        val good = players - evil
        return mapOf(
                Character.Servant to good - 1,
                Character.Minion to evil,
                Character.Merlin to 1
        )
    }
}