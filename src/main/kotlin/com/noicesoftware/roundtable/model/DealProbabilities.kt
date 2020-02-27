package com.noicesoftware.roundtable.model

data class DealProbabilities(
        val character: Map<Character, Double>,
        val consecutive: Double
)