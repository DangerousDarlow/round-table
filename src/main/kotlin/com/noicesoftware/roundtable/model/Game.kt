package com.noicesoftware.roundtable.model

import java.time.OffsetDateTime
import java.util.UUID

data class Game(
        val id: UUID,
        val created: OffsetDateTime,
        val players: Map<UUID, Player>
) {
    fun toLogStr() = "($id)"
}