package ru.pashkovske.buratino.price.money.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.quotation.service.MarketPriceService

@Service
class CurrentMarketMoneyPriceService(
    private val quotationMarketPriceService: MarketPriceService,
    private val instrumentService: InstrumentService
) : MarketMoneyPriceService {
    override fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice? {
        val instrument: Instrument = instrumentService.get(iid)
        val quotationPrice: Quotation? = quotationMarketPriceService.getTopOfBook(
            iid = iid,
            direction = direction
        )
        return quotationPrice?.let {
            MoneyPrice(
                quotation = it,
                currency = instrument.currency
            )
        }
    }
}
