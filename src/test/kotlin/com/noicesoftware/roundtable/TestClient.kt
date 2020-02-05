package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.fasterxml.jackson.databind.ObjectMapper
import com.noicesoftware.roundtable.model.Player
import com.noicesoftware.roundtable.model.PlayersResponse
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TestClient(
        val restTemplate: TestRestTemplate,
        val objectMapper: ObjectMapper
) {
    var port: Int = 0

    private fun host(): String = "http://localhost:$port"

    fun Player.header(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("x-player", id.toString())
        return headers
    }

    fun createGame(player: Player, header: HttpHeaders = player.header()): Pair<HttpStatus, UUID?> {
        val response = restTemplate.postForEntity(
                "${host()}/api/game/create",
                HttpEntity(player.name, header),
                String::class.java)

        return if (response.statusCode.is2xxSuccessful)
            Pair(response.statusCode, objectMapper.readValue(response.body, UUID::class.java))
        else
            Pair(response.statusCode, null)
    }

    fun createGameAndReturnId(player: Player): UUID {
        val (status, id) = createGame(player)
        assertThat(status, name = "create response status").isEqualTo(HttpStatus.CREATED)
        assertThat(id, name = "create game id").isNotNull()
        return id!!
    }

    fun joinGame(id: UUID, player: Player, header: HttpHeaders = player.header()): HttpStatus {
        val response = restTemplate.postForEntity(
                "${host()}/api/game/$id/join",
                HttpEntity(player.name, header),
                Void::class.java)

        return response.statusCode
    }

    fun players(
            id: UUID,
            player: Player,
            header: HttpHeaders = player.header()
    ): Pair<HttpStatus, PlayersResponse?> {

        val response = restTemplate.exchange(
                "${host()}/api/game/$id/players",
                HttpMethod.GET,
                HttpEntity<Any>(header),
                String::class.java)

        return if (response.statusCode.is2xxSuccessful)
            Pair(response.statusCode, objectMapper.readValue(response.body, PlayersResponse::class.java))
        else
            Pair(response.statusCode, null)
    }

    fun playersSucceeds(id: UUID, player: Player): PlayersResponse {
        val (status, players) = players(id, player)
        assertThat(status, name = "players response status").isEqualTo(HttpStatus.OK)
        return players!!
    }
}