package com.noicesoftware.roundtable.controllers

import com.noicesoftware.roundtable.dealing.Dealer
import com.noicesoftware.roundtable.model.Allegiance
import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealProbabilities
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import com.noicesoftware.roundtable.model.PlayersResponse
import com.noicesoftware.roundtable.model.RedactedPlayer
import com.noicesoftware.roundtable.model.allegiance
import com.noicesoftware.roundtable.redis.GameRepository
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController(
        val gameRepository: GameRepository,
        val dealer: Dealer,
        val logger: Logger
) {

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestHeader(PLAYER_HEADER) playerId: UUID, @RequestBody playerName: String): UUID {
        val player = Player(playerId, sanitisePlayerName(playerName))
        val game = Game(
                id = UUID.randomUUID(),
                created = OffsetDateTime.now(ZoneOffset.UTC),
                players = mapOf(player.id to player)
        )

        gameRepository.set(game)

        logger.info("Create game ${game.toLogStr()}: player ${player.toLogStr()}")
        return game.id
    }

    @PostMapping("{id}/join")
    fun join(@RequestHeader(PLAYER_HEADER) playerId: UUID, @PathVariable id: UUID, @RequestBody playerName: String) {
        val game = gameRepository.get(id)
        val player = Player(playerId, sanitisePlayerName(playerName))

        if (game.players.containsKey(player.id))
            return

        val updatedGame = game.copy(players = game.players.plus(player.id to player))

        logger.info("Join game ${game.toLogStr()}: player ${player.toLogStr()}")
        gameRepository.set(updatedGame)
    }

    @GetMapping("{id}/players")
    fun players(@RequestHeader(PLAYER_HEADER) playerId: UUID, @PathVariable id: UUID): PlayersResponse {
        val game = gameRepository.get(id)

        checkPlayerInGame(game, playerId)

        val you = game.players[playerId] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return PlayersResponse(
                you = you,
                others = redactPlayerInfo(game.players.values, you)
        )
    }

    @PostMapping("{id}/deal")
    fun deal(@RequestHeader(PLAYER_HEADER) playerId: UUID, @PathVariable id: UUID) {
        val game = gameRepository.get(id)

        checkPlayerInGame(game, playerId)
        checkPlayerCount(game)

        val updatedGame = dealer.deal(game)

        logger.info("Deal game ${game.toLogStr()}: player ${game.players[playerId]?.toLogStr()}")
        gameRepository.set(updatedGame)
    }

    @PostMapping("{id}/deal/probabilities")
    fun probabilities(@RequestHeader(PLAYER_HEADER) playerId: UUID, @PathVariable id: UUID): DealProbabilities {
        val game = gameRepository.get(id)

        checkPlayerInGame(game, playerId)
        checkPlayerCount(game)

        return dealer.probabilities(game)
    }

    private val matchInvalidNameCharacters = Regex("[!@#\$%^&*()_+{};':=,.<>?\\-\\[\\]\"|/]")

    private fun sanitisePlayerName(name: String): String = matchInvalidNameCharacters.replace(name, "").trim()

    private fun checkPlayerInGame(game: Game, playerId: UUID) {
        if (!game.players.containsKey(playerId))
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    private fun checkPlayerCount(game: Game) {
        val playerCount = game.players.count()
        if (playerCount < 5 || playerCount > 10) throw ResponseStatusException(
                HttpStatus.PRECONDITION_FAILED,
                "Player count ($playerCount) must be between 5 and 10 inclusive"
        )
    }

    private fun redactPlayerInfo(players: Collection<Player>, you: Player): List<RedactedPlayer> = players
            .filter { otherPlayer -> otherPlayer.id != you.id }
            .map { otherPlayer ->
                RedactedPlayer(otherPlayer.name, allegiance(otherPlayer, you))
            }

    private fun allegiance(otherPlayer: Player, you: Player): Allegiance? {
        if (you.character == null || otherPlayer.character == null)
            return null

        if (you.character == Character.Servant)
            return null

        return otherPlayer.character!!.allegiance()
    }

    companion object {
        const val PLAYER_HEADER = "x-player"
    }
}