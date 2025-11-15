package ru.pashkovske.buratino.price.quotation.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepo
import ru.pashkovske.buratino.price.offer.service.MarketScrapper
import ru.pashkovske.buratino.price.quotation.model.QuotationSpread

private const val DEPTH_CHECK = 5

@Service
class CurrentMarketQuotationService(
    private val marketScrapper: MarketScrapper,
    private val offerBookRepo: OfferBookRepo
) : MarketQuotationService {
    override fun getSpread(
        iid: InstrumentId
    ): QuotationSpread {
        val topSell: Quotation? = getTopOfBook(
            iid = iid,
            direction = OrderDirection.SELL
        )
        val topBuy: Quotation? = getTopOfBook(
            iid = iid,
            direction = OrderDirection.BUY
        )
        return QuotationSpread(
            bid = topBuy,
            ask = topSell
        )
    }

    override fun getTopOfBook(
        iid: InstrumentId,
        direction: OrderDirection
    ): Quotation? {
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

    private fun getTopOfBookSellPrice(offerBook: OfferBook): Quotation? {
        return offerBook.asks.keys
            .sorted()
            .firstOrNull { offerBook.asks[it]!!.alienAffiliated != null }
    }

    private fun getTopOfBookBuyPrice(offerBook: OfferBook): Quotation? {
        return offerBook.bids.keys
            .sortedDescending()
            .firstOrNull { offerBook.bids[it]!!.alienAffiliated != null }
    }
}
