package com.noicesoftware.roundtable

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.messageContains
import com.noicesoftware.roundtable.model.Character
import com.noicesoftware.roundtable.dealing.DeckBuilder
import org.junit.jupiter.api.Test

class DeckBuilderTests {
    private val deckBuilder = DeckBuilder()

    @Test
    fun build_five_card_deck() {
        val deck = deckBuilder.build(5)
        assertThat(deck[Character.Servant]).isEqualTo(2)
        assertThat(deck[Character.Minion]).isEqualTo(2)
        assertThat(deck[Character.Merlin]).isEqualTo(1)
    }

    @Test
    fun build_six_card_deck() {
        val deck = deckBuilder.build(6)
        assertThat(deck[Character.Servant]).isEqualTo(3)
        assertThat(deck[Character.Minion]).isEqualTo(2)
        assertThat(deck[Character.Merlin]).isEqualTo(1)
    }

    @Test
    fun cannot_build_two_card_deck() {
        assertThat { deckBuilder.build(2) }.isFailure().messageContains("between 5 and 10")
    }

    @Test
    fun cannot_build_eleven_card_deck() {
        assertThat { deckBuilder.build(11) }.isFailure().messageContains("between 5 and 10")
    }
}