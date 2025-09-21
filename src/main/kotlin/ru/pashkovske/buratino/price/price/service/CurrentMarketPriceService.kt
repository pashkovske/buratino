package ru.pashkovske.buratino.price.price.service

import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.price.model.Quotation
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.repository.OfferBookRepo
import ru.pashkovske.buratino.price.offer.service.MarketScrapper

private const val DEPTH_CHECK = 5

class CurrentMarketPriceService(
    private val marketScrapper: MarketScrapper,
    private val offerBookRepo: OfferBookRepo
) : MarketPriceService {

    override fun getSpreadBasisPoints(
        instrument: Instrument,
        account: Account
    ): Long? {
        val step = instrument.minPriceIncrement
        val bestSellQuotation: Quotation? = getBestOfBook(
            instrument = instrument,
            direction = OrderDirection.SELL,
            account = account
        )
        val bestBuyQuotation: Quotation? = getBestOfBook(
            instrument = instrument,
            direction = OrderDirection.BUY,
            account = account
        )
        if (bestBuyQuotation == null || bestSellQuotation == null) {
            return null
        }
        if (bestSellQuotation % step != 0L) {
            throw IllegalArgumentException("Best sell price is not divisible by step: $instrument")
        }
        if (bestBuyQuotation % step != 0L) {
            throw IllegalArgumentException("Best buy price is not divisible by step: $instrument")
        }
        val bestSellPrice: Long = bestSellQuotation / step
        val bestBuyPrice: Long = bestBuyQuotation / step
        return ((bestSellPrice - bestBuyPrice) * 20000) / (bestSellPrice + bestBuyPrice)
    }

    override fun getBestOfBook(
        instrument: Instrument,
        direction: OrderDirection,
        account: Account
    ): Quotation? {
        val iid: InstrumentId = instrument.iid
        marketScrapper.updateOfferBook(
            iid = iid,
            depth = DEPTH_CHECK,
            account = account
        )
        val offerBook: OfferBook = offerBookRepo.read(iid)!!
        return when (direction) {
            OrderDirection.BUY -> getBestOfBookBuyPrice(offerBook)
            OrderDirection.SELL -> getBestOfBookSellPrice(offerBook)
            else -> throw IllegalArgumentException("Определение лучшей цены не зависимо от направления сделки не реализовано")
        }
    }

    private fun getBestOfBookSellPrice(offerBook: OfferBook): Quotation? {
        return offerBook.asks.keys
            .sorted()
            .firstOrNull { offerBook.asks[it]!!.alienAffiliated != null }
    }

    private fun getBestOfBookBuyPrice(offerBook: OfferBook): Quotation? {
        return offerBook.bids.keys
            .sortedDescending()
            .firstOrNull { offerBook.bids[it]!!.alienAffiliated != null }
    }
}
