package ru.pashkovske.buratino.price.money.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.model.MoneySpread

interface MarketMoneyPriceService {
    fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice?

    fun getOneStepOverTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice?

    fun getSpread(
        iid: InstrumentId
    ): MoneySpread
}
