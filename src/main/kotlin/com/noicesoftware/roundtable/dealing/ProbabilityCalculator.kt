package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealerStrategy
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player

interface ProbabilityCalculator {
    // dealer strategy the probability calculator implements
    val strategy: DealerStrategy

    fun calculate(game: Game, deck: Map<Character, Int>, player: Player): Map<Character, Double>
}