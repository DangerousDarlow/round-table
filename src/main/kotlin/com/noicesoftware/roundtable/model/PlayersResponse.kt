package com.noicesoftware.roundtable.model

data class PlayersResponse(
        val you: Player?,
        val others: List<RedactedPlayer>
)