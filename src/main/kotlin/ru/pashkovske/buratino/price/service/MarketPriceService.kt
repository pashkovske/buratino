package ru.pashkovske.buratino.price.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.model.Spread

interface MarketPriceService {
    fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Price?

    fun getOneStepOverTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Price?

    fun getSpread(
        iid: InstrumentId
    ): Spread
}
