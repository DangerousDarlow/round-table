package com.noicesoftware.roundtable.controllers

import com.noicesoftware.roundtable.model.Game
import com.noicesoftware.roundtable.model.Player
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController {

    @PostMapping("create")
    fun create(@RequestBody player: Player): Game {
        return Game(
                id = UUID.randomUUID(),
                created = OffsetDateTime.now(ZoneOffset.UTC),
                players = listOf(player)
        )
    }
}