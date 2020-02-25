package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.springframework.stereotype.Component

@Component
class ProbabilityCalculator {
    fun calculate(
            game: Game,
            deck: Map<Character, Int>,
            player: Player
    ): Map<Character, Double> {
        val totalLeftToPick = deck.values.sumBy { it }
        return deck.mapValues { it.value.toDouble() / totalLeftToPick }
    }
}