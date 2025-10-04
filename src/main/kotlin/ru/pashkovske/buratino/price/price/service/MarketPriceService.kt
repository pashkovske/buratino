package ru.pashkovske.buratino.price.price.service

import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.price.model.Quotation

interface MarketPriceService {
    fun getSpreadBasisPoints(
        instrument: Instrument
    ): Long?

    fun getTopOfBook(
        instrument: Instrument,
        direction: OrderDirection
    ): Quotation?
}
