package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.offer.adapter.OfferBookAdapter
import ru.pashkovske.buratino.price.offer.model.Offer
import ru.pashkovske.buratino.price.offer.model.OfferAffiliation
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.model.OfferDirection
import ru.pashkovske.buratino.price.offer.model.QuotationLevelOffers
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse
import ru.tinkoff.piapi.contract.v1.Order
import ru.tinkoff.piapi.contract.v1.OrderState
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
        val tinkoffOrderBook: GetOrderBookResponse = tinkoffMarketDataService.getOrderBookSync(iid.id, depth)
        val tinkoffOwnedOrdes: List<OrderState> = tinkoffOrderService.getOrdersSync(account.id)
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

        val selfAsks: Map<Price, Offer> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.SELL }
        )
        val selfBids: Map<Price, Offer> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.BUY }
        )

        val alienAsks: Map<Price, Offer> = aggregateOffers(
            offers = getAlienOffers(
                tinkoffMixedOrders = tinkoffOrderBook.asksList,
                selfOffers = selfAsks,
                direction = OfferDirection.SELL,
                currency = instrument.currency
            )
        )
        val alienBids: Map<Price, Offer> = aggregateOffers(
            offers = getAlienOffers(
                tinkoffMixedOrders = tinkoffOrderBook.bidsList,
                selfOffers = selfBids,
                direction = OfferDirection.BUY,
                currency = instrument.currency
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
        selfOffers: Map<Price, Offer>,
        direction: OfferDirection,
        currency: Currency
    ): List<Offer> {
        return tinkoffMixedOrders
            .map { tinkoffMixedOrder: Order ->
                val price: Price = TinkoffPriceMapper.map(
                    tinkoffQuotation = tinkoffMixedOrder.price,
                    currency = currency
                )
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

    private fun aggregateOffers(offers: List<Offer>): Map<Price, Offer> {
        return offers.groupBy { it.price }
            .map { it.value.reduce(Offer::plus) }
            .associateBy { it.price }
    }

    private fun combineOffers(
        selfOffers: Map<Price, Offer>,
        alienOffers: Map<Price, Offer>
    ): Map<Price, QuotationLevelOffers> {
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
