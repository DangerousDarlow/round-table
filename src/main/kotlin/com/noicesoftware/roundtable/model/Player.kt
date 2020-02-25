package com.noicesoftware.roundtable.model

import java.util.UUID

data class Player(
        val id: UUID,
        val name: String,
        var character: Character? = null
) {
    fun toLogStr() = "$name ($id)"
}