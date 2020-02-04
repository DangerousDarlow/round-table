package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.noicesoftware.roundtable.model.Player
import com.noicesoftware.roundtable.model.PlayersResponse
import com.noicesoftware.roundtable.model.RedactedPlayer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoundTableApplicationTests {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private final val anna: Player = Player(UUID.fromString("7ed19a9c-b9d1-485d-8671-54980efc79ff"), name = "anna")
    private final val bill: Player = Player(UUID.fromString("96942c94-b0c7-4b9f-bf9f-35d96c014646"), name = "bill")
    private final val caia: Player = Player(UUID.fromString("e362f443-3b25-46b0-95a7-da7df590ca39"), name = "caia")
    private final val dave: Player = Player(UUID.fromString("8ac39b82-d14e-44d9-a5a9-d17667273405"), name = "dave")
    private final val emma: Player = Player(UUID.fromString("5565cbad-d72d-474d-a154-6449ef32cc79"), name = "emma")

    private final val playerNotInGame: Player = Player(UUID.fromString("346cebd1-92f4-4f91-83ae-7a7cc544d766"), name = "fred")

    val testPlayers = mapOf(
            anna.id to anna,
            bill.id to bill,
            caia.id to caia,
            dave.id to dave,
            emma.id to emma)

    private fun host(): String = "http://localhost:$port"

    private fun Player.header(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("x-player", id.toString())
        return headers
    }

    private fun createGame(player: Player): UUID {
        val response = restTemplate.postForEntity(
                "${host()}/api/game/create",
                HttpEntity(player.name, player.header()),
                UUID::class.java)

        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.CREATED)
        return response.body!!
    }

    private fun joinGame(id: UUID, player: Player) {
        val response = restTemplate.postForEntity(
                "${host()}/api/game/$id/join",
                HttpEntity(player.name, player.header()),
                Void::class.java)

        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.OK)
    }

    private fun getPlayers(id: UUID, player: Player, status: HttpStatus): PlayersResponse? {
        val response = restTemplate.exchange(
                "${host()}/api/game/$id/players",
                HttpMethod.GET,
                HttpEntity<Any>(player.header()),
                String::class.java)

        assertThat(response.statusCode, name = "response status").isEqualTo(status)
        return if (response.statusCode.is2xxSuccessful)
            objectMapper.readValue(response.body, PlayersResponse::class.java)
        else
            null
    }

    private fun getPlayersSucceeds(id: UUID, player: Player): PlayersResponse = getPlayers(id, player, HttpStatus.OK)
            ?: throw Exception("Failed to deserialise response body")

    private fun getPlayersReturnsNotFound(id: UUID, player: Player) = getPlayers(id, player, HttpStatus.NOT_FOUND)

    @Test
    fun five_player_game() {
        val id = createGame(anna)

        // anna doesn't join the game twice. joining a game a player is already in has no effect.
        testPlayers.forEach { joinGame(id, it.value) }

        val players = getPlayersSucceeds(id, anna)
        assertThat(players.you).isEqualTo(anna)
        assertThat(players.others).isEqualTo(
                testPlayers.filter { it.value.id != anna.id }.map { RedactedPlayer(it.value.name) })

        getPlayersReturnsNotFound(id, playerNotInGame)
    }
}
