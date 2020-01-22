package com.noicesoftware.roundtable.controllers

import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import com.noicesoftware.roundtable.redis.GameRepository
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController(
        val gameRepository: GameRepository,
        val logger: Logger
) {

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody player: Player): Game {
        val game = Game(
                id = UUID.randomUUID(),
                created = OffsetDateTime.now(ZoneOffset.UTC),
                players = listOf(player)
        )

        logger.info("Create game ${game.id}: player ${player.name} ${player.id}")

        gameRepository.set(game)
        return game
    }

    @PostMapping("{id}/join")
    fun join(@PathVariable id: UUID, @RequestBody player: Player): Game {
        val oldGame = gameRepository.get(id)
        logger.info("Join game ${oldGame.id}: player ${player.name} ${player.id}")

        if (oldGame.players.map { it.id }.contains(player.id))
            return oldGame

        val newGame = oldGame.copy(players = oldGame.players.plus(player))
        gameRepository.set(newGame)
        return newGame
    }

    @GetMapping("{id}")
    fun get(@PathVariable id: UUID): Game = gameRepository.get(id)
}