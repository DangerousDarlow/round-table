package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
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

    private final val anna: Player = Player(UUID.fromString("7ed19a9c-b9d1-485d-8671-54980efc79ff"), name = "anna")
    private final val bill: Player = Player(UUID.fromString("96942c94-b0c7-4b9f-bf9f-35d96c014646"), name = "bill")
    private final val caia: Player = Player(UUID.fromString("e362f443-3b25-46b0-95a7-da7df590ca39"), name = "caia")
    private final val dave: Player = Player(UUID.fromString("8ac39b82-d14e-44d9-a5a9-d17667273405"), name = "dave")
    private final val emma: Player = Player(UUID.fromString("5565cbad-d72d-474d-a154-6449ef32cc79"), name = "emma")

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
        assertThat(response.body, name = "response body (${player.name})").isNotNull()
        return response.body!!
    }

    private fun joinGame(id: UUID, player: Player) {
        val response = restTemplate.postForEntity(
                "${host()}/api/game/$id/join",
                HttpEntity(player.name, player.header()),
                Void::class.java)

        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.OK)
        assertThat(response.body, name = "response body (${player.name})").isNull()
    }

    private fun getPlayers(id: UUID, player: Player): PlayersResponse {
        val response = restTemplate.exchange(
                "${host()}/api/game/$id/players",
                HttpMethod.GET,
                HttpEntity<Any>(player.header()),
                PlayersResponse::class.java)

        assertThat(response.statusCode, name = "response status").isEqualTo(HttpStatus.OK)
        assertThat(response.body, name = "response body").isNotNull()
        return response.body!!
    }

    @Test
    fun five_player_game() {
        val id = createGame(anna)

        // anna doesn't join the game twice. joining a game a player is already in has no effect.
        testPlayers.forEach { joinGame(id, it.value) }

        val players = getPlayers(id, anna)
        assertThat(players.you).isEqualTo(anna)

        // TODO others should not include the player making the api call
        assertThat(players.others).isEqualTo(testPlayers.map { RedactedPlayer(it.value.name) })
    }
}
