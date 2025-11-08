package ru.pashkovske.buratino.price.money.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.model.MoneySpread
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
            MoneyPrice(
                quotation = it,
                currency = instrument.currency
            )
        }
    }

    override fun getOneStepOverTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice? {
        val instrument: Instrument = instrumentService.get(iid)
        var topPrice: MoneyPrice = getTopOfBook(
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

    override fun getSpread(iid: InstrumentId): MoneySpread {
        val instrument: Instrument = instrumentService.get(iid)
        return MoneySpread(
            spread = quotationMarketPriceService.getSpread(iid),
            currency = instrument.currency
        )
    }
}
