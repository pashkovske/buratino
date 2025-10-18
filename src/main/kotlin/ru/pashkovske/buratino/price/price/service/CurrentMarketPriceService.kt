package ru.pashkovske.buratino.price.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.price.model.Quotation
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepo
import ru.pashkovske.buratino.price.offer.service.MarketScrapper
import ru.pashkovske.buratino.price.price.model.MoneyPrice

private const val DEPTH_CHECK = 5

@Service
class CurrentMarketPriceService(
    private val marketScrapper: MarketScrapper,
    private val offerBookRepo: OfferBookRepo,
    private val instrumentService: InstrumentService
) : MarketPriceService {
/*
    override fun getSpreadBasisPoints(
        instrument: Instrument
    ): Long? {
        val step = instrument.minPriceIncrement
        val bestSellQuotation: Quotation? = getTopOfBookQuotation(
            instrument = instrument,
            direction = OrderDirection.SELL
        )
        val bestBuyQuotation: Quotation? = getTopOfBookQuotation(
            instrument = instrument,
            direction = OrderDirection.BUY
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
    }*/

    override fun getTopOfBookQuotation(
        iid: InstrumentId,
        direction: OrderDirection
    ): Quotation? {
        marketScrapper.updateOfferBook(
            iid = iid,
            depth = DEPTH_CHECK
        )
        val offerBook: OfferBook = offerBookRepo.read(iid)!!
        return when (direction) {
            OrderDirection.BUY -> getTopOfBookBuyPrice(offerBook)
            OrderDirection.SELL -> getTopOfBookSellPrice(offerBook)
            else -> throw IllegalArgumentException("Определение лучшей цены не зависимо от направления сделки не реализовано")
        }
    }

    override fun getTopOfBookMoney(
        iid: InstrumentId,
        direction: OrderDirection
    ): MoneyPrice? {
        val instrument: Instrument = instrumentService.get(iid)
        val quotationPrice: Quotation? = getTopOfBookQuotation(
            iid = iid,
            direction = direction
        )
        return if (quotationPrice == null) {
            null
        } else {
            MoneyPrice(
                quotation = quotationPrice,
                currency = instrument.currency
            )
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
