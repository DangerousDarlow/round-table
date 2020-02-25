package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DeckBuilder
import com.noicesoftware.roundtable.model.Game
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class Dealer(
        val deckBuilder: DeckBuilder,
        val probabilityCalculator: ProbabilityCalculator,
        val logger: Logger
) {

    fun deal(game: Game): Game {
        val deck = deckBuilder.build(game.players.count()).toMutableMap()

        game.players.forEach { (_, player) ->
            val totalLeftToPick = deck.values.sumBy { it }

            if (totalLeftToPick == 1) {
                player.character = deck.filterValues { it != 0 }.keys.first()
            } else {
                val probabilities = probabilityCalculator.calculate(game, deck, player)
                player.character = pickCharacter(probabilities)

                // decrement character type count by one
                deck.merge(player.character!!, 1) { old, value -> old - value }
            }
        }

        return game
    }

    fun pickCharacter(probabilities: Map<Character, Double>): Character {
        val random = Math.random()
        var cumulative = 0.0

        for (probability in probabilities) {
            cumulative += probability.value
            if (random < cumulative)
                return probability.key
        }

        logger.warn("Returned default character class for random $random and probabilities $probabilities")
        return Character.Servant
    }
}