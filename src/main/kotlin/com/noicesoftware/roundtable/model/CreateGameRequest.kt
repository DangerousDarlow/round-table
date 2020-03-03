package com.noicesoftware.roundtable.model

data class CreateGameRequest(
        val player: String,
        val strategy: DealerStrategy
)