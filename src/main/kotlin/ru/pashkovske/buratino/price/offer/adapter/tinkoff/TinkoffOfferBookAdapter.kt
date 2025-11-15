package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.price.offer.adapter.OfferBookAdapter
import ru.pashkovske.buratino.price.offer.model.Offer
import ru.pashkovske.buratino.price.offer.model.OfferAffiliation
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.model.OfferDirection
import ru.pashkovske.buratino.price.offer.model.QuotationLevelOffers
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.tinkoff.piapi.contract.v1.Order
import ru.tinkoff.piapi.core.MarketDataService
import ru.tinkoff.piapi.core.OrdersService
import java.time.Instant

@Component
class TinkoffOfferBookAdapter(
    private val tinkoffMarketDataService: MarketDataService,
    private val tinkoffOrderService: OrdersService,
    private val instrumentService: InstrumentService,
    private val account: Account
) : OfferBookAdapter {
    override fun getOfferBook(
        iid: InstrumentId,
        depth: Int
    ): OfferBook {
        val tinkoffOrderBook = tinkoffMarketDataService.getOrderBookSync(iid.id, depth)
        val tinkoffOwnedOrdes = tinkoffOrderService.getOrdersSync(account.id)
        val instrument: Instrument = instrumentService.get(iid)

        val selfOffersList: List<Offer> = tinkoffOwnedOrdes
            .filter { it.instrumentUid == iid.id }
            .map { orderState ->
                TinkoffOfferMapper.map(
                    tinkoffOrder = orderState,
                    instrument = instrument,
                    affiliation = OfferAffiliation.SELF
                )
            }

        val selfAsks: Map<Quotation, Offer> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.SELL }
        )
        val selfBids: Map<Quotation, Offer> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.BUY }
        )

        val alienAsks: Map<Quotation, Offer> = aggregateOffers(
            offers = getAlienOffers(
                tinkoffMixedOrders = tinkoffOrderBook.asksList,
                selfOffers = selfAsks,
                direction = OfferDirection.SELL
            )
        )
        val alienBids: Map<Quotation, Offer> = aggregateOffers(
            offers = getAlienOffers(
                tinkoffMixedOrders = tinkoffOrderBook.bidsList,
                selfOffers = selfBids,
                direction = OfferDirection.BUY
            )
        )

        return OfferBook(
            ts = Instant.ofEpochSecond(
                tinkoffOrderBook.orderbookTs.seconds,
                tinkoffOrderBook.orderbookTs.nanos.toLong()
            ),
            asks = combineOffers(
                selfOffers = selfAsks,
                alienOffers = alienAsks
            ),
            bids = combineOffers(
                selfOffers = selfBids,
                alienOffers = alienBids
            )
        )
    }

    private fun getAlienOffers(
        tinkoffMixedOrders: List<Order>,
        selfOffers: Map<Quotation, Offer>,
        direction: OfferDirection
    ): List<Offer> {
        return tinkoffMixedOrders
            .map { tinkoffMixedOrder ->
                val price: Quotation = TinkoffPriceMapper.map(tinkoffMixedOrder.price)
                var lots = tinkoffMixedOrder.quantity
                val selfOffer: Offer? = selfOffers[price]
                if (selfOffer != null) {
                    lots -= selfOffer.lots
                    if (lots < 0) {
                        throw IllegalStateException("More self offers than all")
                    }
                }
                Offer(
                    price = price,
                    lots = lots,
                    direction = direction,
                    affiliation = OfferAffiliation.ALIEN
                )
            }
    }

    private fun aggregateOffers(offers: List<Offer>): Map<Quotation, Offer> {
        return offers.groupBy { it.price }
            .map { it.value.reduce(Offer::plus) }
            .associateBy { it.price }
    }

    private fun combineOffers(
        selfOffers: Map<Quotation, Offer>,
        alienOffers: Map<Quotation, Offer>
    ): Map<Quotation, QuotationLevelOffers> {
        val allPrices = alienOffers.keys
        
        return allPrices.associateWith { price ->
            val selfOffer = selfOffers[price]
            var alienOffer = alienOffers[price]
            if (alienOffer?.lots == 0L) {
                alienOffer = null
            }

            QuotationLevelOffers(
                selfAffiliated = selfOffer,
                alienAffiliated = alienOffer
            )
        }
    }
}