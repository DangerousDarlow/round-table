package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoundTableApplicationTests {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    val anna: Player = Player(
            UUID.fromString("7ed19a9c-b9d1-485d-8671-54980efc79ff"),
            name = "anna"
    )

    val bill: Player = Player(
            UUID.fromString("96942c94-b0c7-4b9f-bf9f-35d96c014646"),
            name = "bill"
    )

    val caia: Player = Player(
            UUID.fromString("e362f443-3b25-46b0-95a7-da7df590ca39"),
            name = "caia"
    )

    val dave: Player = Player(
            UUID.fromString("8ac39b82-d14e-44d9-a5a9-d17667273405"),
            name = "dave"
    )

    val emma: Player = Player(
            UUID.fromString("5565cbad-d72d-474d-a154-6449ef32cc79"),
            name = "emma"
    )

    val players = listOf(anna, bill, caia, dave, emma)

    private fun host(): String = "http://localhost:$port"

    private fun createGame(player: Player): Game {
        val response = restTemplate.postForEntity("${host()}/api/game/create", HttpEntity(player), Game::class.java)
        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.CREATED)
        assertThat(response.body, name = "response body (${player.name})").isNotNull()
        return response.body!!
    }

    private fun joinGame(id: UUID, player: Player): Game {
        val response = restTemplate.postForEntity("${host()}/api/game/$id/join", HttpEntity(player), Game::class.java)
        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.OK)
        assertThat(response.body, name = "response body (${player.name})").isNotNull()
        return response.body!!
    }

    private fun getGame(id: UUID): Game {
        val response = restTemplate.getForEntity("${host()}/api/game/$id", Game::class.java)
        assertThat(response.statusCode, name = "response status").isEqualTo(HttpStatus.OK)
        assertThat(response.body, name = "response body").isNotNull()
        return response.body!!
    }


    @Test
    fun five_player_game() {
        // anna creates the same, all other players join
        val id = createGame(anna).id
        players.drop(1).forEach { joinGame(id, it) }

        val game = getGame(id)
        assertThat(game.id).isEqualTo(id)
        assertThat(game.players).isEqualTo(players)
    }
}
