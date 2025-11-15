package ru.pashkovske.buratino.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.model.Spread
import ru.pashkovske.buratino.price.quotation.service.MarketQuotationService

@Service
class CurrentMarketPriceService(
    private val quotationMarketQuotationService: MarketQuotationService,
    private val instrumentService: InstrumentService
) : MarketPriceService {
    override fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Price? {
        val instrument: Instrument = instrumentService.get(iid)
        val quotationPrice: Quotation? = quotationMarketQuotationService.getTopOfBook(
            iid = iid,
            direction = direction
        )
        val moneyQuotationPrice: Quotation? = when (instrument) {
            is Future -> quotationPrice?.let { price ->
                if (price % instrument.minPriceIncrementPts != 0L) {
                    throw IllegalArgumentException("Cannot transform future points price to money price: `$price` is not multiple of min price increment `${instrument.minPriceIncrementPts}`")
                }
                val stepsInPrice: Long = price / instrument.minPriceIncrementPts
                instrument.minPriceIncrement * stepsInPrice.toInt()
            }
            else -> quotationPrice
        }
        return moneyQuotationPrice?.let {
            Price(
                quotation = it,
                currency = instrument.currency
            )
        }
    }

    override fun getOneStepOverTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Price? {
        val instrument: Instrument = instrumentService.get(iid)
        var topPrice: Price = getTopOfBook(
            iid = iid,
            direction = direction
        )!!
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (direction) {
            OrderDirection.BUY -> {
                topPrice += instrument.minPriceIncrement
            }
            OrderDirection.SELL -> {
                topPrice -= instrument.minPriceIncrement
            }
            else -> {
                throw IllegalArgumentException("Unsupported direction: $direction")
            }
        }
        return topPrice
    }

    override fun getSpread(iid: InstrumentId): Spread {
        val instrument: Instrument = instrumentService.get(iid)
        return Spread(
            quotationSpread = quotationMarketQuotationService.getSpread(iid),
            currency = instrument.currency
        )
    }
}
