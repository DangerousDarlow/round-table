package com.noicesoftware.roundtable.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Player(
        val id: UUID,
        val name: String,
        var character: Character? = null
) {
    fun toLogStr() = "$name ($id)"
}