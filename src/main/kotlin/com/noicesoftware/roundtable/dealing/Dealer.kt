package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealProbabilities
import com.noicesoftware.roundtable.model.Game
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class Dealer(
        val deckBuilder: DeckBuilder,
        val probabilityCalculatorStrategy: ProbabilityCalculatorStrategy,
        val logger: Logger
) {

    fun deal(game: Game): Game {
        val deck = deckBuilder.build(game.players.count()).toMutableMap()

        val updatedGame = game.copy()
        val probabilityCalculator = probabilityCalculatorStrategy.getCalculator(updatedGame)

        updatedGame.players.forEach { (_, player) ->
            val totalLeftToPick = deck.values.sumBy { it }

            if (totalLeftToPick == 1) {
                player.character = deck.filterValues { it != 0 }.keys.single()
            } else {
                val probabilities = probabilityCalculator.calculate(updatedGame, deck, player)
                player.character = pickCharacter(probabilities)

                // decrement character type count by one
                deck.merge(player.character!!, 1, Int::minus)
            }
        }

        return updatedGame
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

    fun probabilities(game: Game): DealProbabilities {
        val characterCounts = Character.values().map { it to 0 }.toMap().toMutableMap()
        var timesSameCharacter = 0
        var lastCharacter: Character? = null

        val iterations = 10000

        for (iteration in 1..iterations) {
            val dummyGame = deal(game)

            val player = dummyGame.players.values.first()
            characterCounts.merge(player.character!!, 1, Int::plus)

            if (player.character == lastCharacter)
                timesSameCharacter++
            else
                lastCharacter = player.character
        }

        return DealProbabilities(
                characterCounts.map { it.key to it.value.toDouble() / iterations }.toMap(),
                timesSameCharacter.toDouble() / iterations
        )
    }
}