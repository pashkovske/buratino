package ru.pashkovske.buratino.price.price.service

import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.price.model.Quotation

interface MarketPriceService {
    fun getSpreadBasisPoints(
        instrument: Instrument,
        account: Account
    ): Long?

    fun getBestOfBook(
        instrument: Instrument,
        direction: OrderDirection,
        account: Account
    ): Quotation?
}
