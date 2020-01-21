package com.noicesoftware.roundtable.controllers

import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController(
        val redisTemplate: RedisTemplate<String, Game>
) {

    private fun buildGameKey(id: UUID) = "game-$id"

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody player: Player): Game {
        val game = Game(
                id = UUID.randomUUID(),
                created = OffsetDateTime.now(ZoneOffset.UTC),
                players = listOf(player)
        )

        redisTemplate.opsForValue().set(buildGameKey(game.id), game, Duration.ofMinutes(10))
        return game
    }

    @PostMapping("{id}/join")
    fun join(@PathVariable id: UUID, @RequestBody player: Player): Game {
        val oldGame = redisTemplate.opsForValue().get(buildGameKey(id)) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val newGame = oldGame.copy(players = oldGame.players.plus(player))
        redisTemplate.opsForValue().set(buildGameKey(newGame.id), newGame, Duration.ofMinutes(10))
        return newGame
    }

    @GetMapping("{id}")
    fun get(@PathVariable id: UUID): Game =
            redisTemplate.opsForValue().get(buildGameKey(id)) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
}