package ru.pashkovske.buratino.price.money.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.money.model.MoneyPrice

interface MarketMoneyPriceService {
    fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice?
}