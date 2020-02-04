package com.noicesoftware.roundtable.redis

import com.noicesoftware.roundtable.model.Game
import org.slf4j.Logger
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.util.UUID

@Component
class GameRepository(
        val redisTemplate: RedisTemplate<String, Game>,
        val logger: Logger,
        redisConfig: RedisConfig
) {
    private fun buildGameKey(id: UUID) = "game-$id"

    var defaultDuration: Duration = Duration.ofHours(redisConfig.defaultDuration)

    fun get(id: UUID): Game {
        try {
            val key = buildGameKey(id)
            return redisTemplate.opsForValue().get(key) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            logger.warn("Failed to get game '$id' from redis: $e")
            throw e
        }
    }

    fun set(game: Game) {
        try {
            redisTemplate.opsForValue().set(buildGameKey(game.id), game, defaultDuration)
        } catch (e: Exception) {
            logger.error("Failed to set game '${game.id}' to redis: $e")
            throw e
        }
    }
}