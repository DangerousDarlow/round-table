package com.noicesoftware.roundtable.model

enum class Character {
    Merlin,
    Minion,
    Servant
}

fun Character.allegiance(): Allegiance = when (this) {
    Character.Merlin -> Allegiance.Good
    Character.Minion -> Allegiance.Evil
    Character.Servant -> Allegiance.Good
}