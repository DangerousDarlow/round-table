package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Game
import org.springframework.stereotype.Component

@Component
class ProbabilityCalculatorStrategy(probabilityCalculators: List<ProbabilityCalculator>) {
    private val calculators = probabilityCalculators.map { it.strategy to it }.toMap()

    fun getCalculator(game: Game): ProbabilityCalculator = calculators[game.strategy]
            ?: throw Exception("Unable to find probability calculator for strategy '${game.strategy}'")
}