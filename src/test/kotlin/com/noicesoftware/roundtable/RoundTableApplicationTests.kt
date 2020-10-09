package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isNullOrEmpty
import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.model.DealerStrategy
import com.noicesoftware.roundtable.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoundTableApplicationTests {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var client: TestClient

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
            emma.id to emma
    )

    @BeforeEach
    fun setPort() {
        client.port = port
    }

    @Test
    fun five_player_game() {
        val id = createFivePlayerGame()

        assertThat(client.deal(id, anna)).isEqualTo(HttpStatus.OK)

        // assert each player can see their details plus an appropriate
        // amount of information about the other players in the game
        testPlayers.forEach { (_, player) ->
            val players = client.playersSucceeds(id, player)

            assertThat(players.you.id).isEqualTo(player.id)
            assertThat(players.you.name).isEqualTo(player.name)
            assertThat(players.you.character).isNotNull()

            // check other players contains the names of all the other players
            assertThat(players.others.map { it.name })
                    .isEqualTo(testPlayers.filter { it.value.id != player.id }.values.map { it.name })

            // check servants can't see allegiance and other characters can
            players.others.forEach { otherPlayer ->
                if (players.you.character == Character.Servant)
                    assertThat(otherPlayer.allegiance).isNull()
                else
                    assertThat(otherPlayer.allegiance).isNotNull()
            }
        }
    }

    private fun createFivePlayerGame(strategy: DealerStrategy = DealerStrategy.Unbiased): UUID {
        val id = client.createGameAndReturnId(anna, strategy)

        // all players except anna join
        testPlayers.filter { it.value != anna }.forEach {
            assertThat(client.joinGame(id, it.value), "join response status (${it.value.name})")
                    .isEqualTo(HttpStatus.OK)
        }

        return id
    }

    val probabilityTolerance = 0.02

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    @Test
    fun five_player_game_probability_distribution_matches_proof() {
        val id = createFivePlayerGame()
        val (status, probabilities) = client.probabilities(id, anna)
        assertThat(status).isEqualTo(HttpStatus.OK)

        assertThat(probabilities).isNotNull()
        assertThat(probabilities!!.character[Character.Merlin]!!).isEqualTo(0.20, probabilityTolerance)
        assertThat(probabilities!!.character[Character.Minion]!!).isEqualTo(0.40, probabilityTolerance)
        assertThat(probabilities!!.character[Character.Servant]!!).isEqualTo(0.40, probabilityTolerance)
        assertThat(probabilities!!.consecutive).isEqualTo(0.36, probabilityTolerance)
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    @Test
    fun five_player_biased_game_probability_distribution_matches_proof() {
        val id = createFivePlayerGame(DealerStrategy.Biased)
        val (status, probabilities) = client.probabilities(id, anna)
        assertThat(status).isEqualTo(HttpStatus.OK)

        assertThat(probabilities).isNotNull()
        assertThat(probabilities!!.character[Character.Merlin]!!).isEqualTo(0.25, probabilityTolerance)
        assertThat(probabilities!!.character[Character.Minion]!!).isEqualTo(0.37, probabilityTolerance)
        assertThat(probabilities!!.character[Character.Servant]!!).isEqualTo(0.37, probabilityTolerance)
        assertThat(probabilities!!.consecutive).isEqualTo(0.18, probabilityTolerance)
    }

    @Test
    fun cannot_create_a_game_if_player_header_is_not_set() {
        val (status, _) = client.createGame(anna, header = HttpHeaders())
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun cannot_join_a_game_if_player_header_is_not_set() {
        val id = client.createGameAndReturnId(anna)
        val status = client.joinGame(id, bill, HttpHeaders())
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun cannot_join_a_game_that_does_not_exist() {
        val id = UUID.fromString("a4734cfa-b231-4571-a8ef-c19d5526525b")
        val status = client.joinGame(id, anna)
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun joining_a_game_player_is_already_in_has_no_affect() {
        val id = client.createGameAndReturnId(anna)
        assertThat(client.joinGame(id, anna), "first join response status").isEqualTo(HttpStatus.OK)
        assertThat(client.joinGame(id, anna), "second join response status").isEqualTo(HttpStatus.OK)

        val (status, players) = client.players(id, anna)
        assertThat(status).isEqualTo(HttpStatus.OK)
        assertThat(players!!.others).isNullOrEmpty()
    }

    @Test
    fun cannot_get_players_if_player_header_is_not_set() {
        val id = client.createGameAndReturnId(anna)
        val (status, _) = client.players(id, anna, HttpHeaders())
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun player_not_in_game_cannot_get_players() {
        val id = client.createGameAndReturnId(anna)
        val (status, _) = client.players(id, bill)
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun cannot_deal_if_player_header_is_not_set() {
        val id = client.createGameAndReturnId(anna)
        assertThat(client.deal(id, anna, HttpHeaders())).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun cannot_deal_if_less_then_five_players() {
        val id = client.createGameAndReturnId(anna)
        assertThat(client.deal(id, anna)).isEqualTo(HttpStatus.PRECONDITION_FAILED)
    }
}