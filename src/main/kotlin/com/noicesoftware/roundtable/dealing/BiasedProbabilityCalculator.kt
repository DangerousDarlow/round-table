package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealerStrategy
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.springframework.stereotype.Component

@Component
class BiasedProbabilityCalculator : ProbabilityCalculator {
    override val strategy: DealerStrategy
        get() = DealerStrategy.Biased

    override fun calculate(game: Game, deck: Map<Character, Int>, player: Player): Map<Character, Double> {
        val probabilities = UnbiasedProbabilityCalculator.calculate(deck)
        if (probabilities.count() < 2) return probabilities

        val lastCharacter = player.character ?: return probabilities
        val probSameAsLast = probabilities[lastCharacter] ?: 0.0
        val probBiasedSameAsLast = probSameAsLast / 2
        val probCountExceptLast = probabilities.count { it.key != lastCharacter }
        val probDiffExceptLast = (probSameAsLast - probBiasedSameAsLast) / probCountExceptLast

        return probabilities.map {
            if (it.key == lastCharacter)
                it.key to probBiasedSameAsLast
            else
                it.key to it.value + probDiffExceptLast
        }.toMap()
    }
}