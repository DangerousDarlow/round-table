package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealerStrategy
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.springframework.stereotype.Component

@Component
class UnbiasedProbabilityCalculator : ProbabilityCalculator {
    override val strategy: DealerStrategy
        get() = DealerStrategy.Unbiased

    override fun calculate(
            game: Game,
            deck: Map<Character, Int>,
            player: Player): Map<Character, Double> = calculate(deck)

    companion object {
        fun calculate(deck: Map<Character, Int>): Map<Character, Double> {
            val totalLeftToPick = deck.values.sumBy { it }
            return deck.filter { it.value > 0 }.mapValues { it.value.toDouble() / totalLeftToPick }
        }
    }
}