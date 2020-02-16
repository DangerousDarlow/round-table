package com.noicesoftware.roundtable.dealing

import com.noicesoftware.roundtable.model.Game
import org.springframework.stereotype.Component

@Component
class UnbiasedDealer : Dealer {
    override fun deal(game: Game): Game = game
}