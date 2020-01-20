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

    private fun host(): String = "http://localhost:$port"

    private fun createGame(player: Player): Game {
        val response = restTemplate.postForEntity("${host()}/api/game/create", HttpEntity(player), Game::class.java)
        assertThat(response.statusCode, name = "response status (${player.name})").isEqualTo(HttpStatus.OK)
        assertThat(response.body, name = "response body (${player.name})").isNotNull()
        return response.body!!
    }

    @Test
    fun five_player_game() {
        createGame(anna)
    }
}
