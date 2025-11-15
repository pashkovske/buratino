package ru.pashkovske.buratino.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.model.Spread
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepo
import ru.pashkovske.buratino.price.offer.service.MarketScrapper

private const val DEPTH_CHECK = 5

@Service
class CurrentMarketPriceService(
    private val marketScrapper: MarketScrapper,
    private val offerBookRepo: OfferBookRepo,
    private val instrumentService: InstrumentService
) : MarketPriceService {
    override fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Price? {
        marketScrapper.updateOfferBook(
            iid = iid,
            depth = DEPTH_CHECK
        )
        val offerBook: OfferBook = offerBookRepo.read(iid)!!
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        return when (direction) {
            OrderDirection.BUY -> getTopOfBookBuyPrice(offerBook)
            OrderDirection.SELL -> getTopOfBookSellPrice(offerBook)
            else -> throw IllegalArgumentException("Определение лучшей цены не зависимо от направления сделки не реализовано")
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
        val topSell: Price? = getTopOfBook(
            iid = iid,
            direction = OrderDirection.SELL
        )
        val topBuy: Price? = getTopOfBook(
            iid = iid,
            direction = OrderDirection.BUY
        )
        return Spread(
            bid = topBuy,
            ask = topSell,
            currency = instrument.currency
        )
    }

    private fun getTopOfBookSellPrice(offerBook: OfferBook): Price? {
        return offerBook.asks.keys
            .sorted()
            .firstOrNull { offerBook.asks[it]!!.alienAffiliated != null }
    }

    private fun getTopOfBookBuyPrice(offerBook: OfferBook): Price? {
        return offerBook.bids.keys
            .sortedDescending()
            .firstOrNull { offerBook.bids[it]!!.alienAffiliated != null }
    }
}
