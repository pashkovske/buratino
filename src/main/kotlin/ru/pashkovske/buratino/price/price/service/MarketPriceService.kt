package ru.pashkovske.buratino.price.price.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.price.model.MoneyPrice
import ru.pashkovske.buratino.price.price.model.Quotation

interface MarketPriceService {
    fun getTopOfBookQuotation(
        iid: InstrumentId,
        direction: OrderDirection
    ): Quotation?

    fun getTopOfBookMoney(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice?
}
