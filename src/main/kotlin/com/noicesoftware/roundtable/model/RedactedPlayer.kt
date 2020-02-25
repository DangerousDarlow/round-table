package com.noicesoftware.roundtable.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RedactedPlayer(
        val name: String,
        val allegiance: Allegiance?
)