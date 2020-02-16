package com.noicesoftware.roundtable.dealing

import org.springframework.stereotype.Component

@Component
class DealerStrategy(val unbiasedDealer: UnbiasedDealer) {
    fun dealer(): Dealer = unbiasedDealer
}