package ru.pashkovske.buratino.price.quotation.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.quotation.model.QuotationSpread

interface MarketQuotationService {
    fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Quotation?

    fun getSpread(
        iid: InstrumentId
    ): QuotationSpread
}
