package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Game

interface Dealer {
    fun deal(game: Game): Game
}