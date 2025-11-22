package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.offer.adapter.OfferBookAdapter
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.dto.TinkoffOfferDto
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

        val selfOffersList: List<TinkoffOfferDto> = tinkoffOwnedOrdes
            .filter { it.instrumentUid == iid.id }
            .map { externalTinkoffOrderState: OrderState ->
                TinkoffOfferMapper.map(
                    externalTinkoffOrderState = externalTinkoffOrderState,
                    affiliation = OfferAffiliation.SELF
                )
            }
        val selfAsks: Map<Price, TinkoffOfferDto> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.SELL }
        )
        val selfBids: Map<Price, TinkoffOfferDto> = aggregateOffers(
            offers = selfOffersList.filter { it.direction == OfferDirection.BUY }
        )

        val undefinedAsks: Map<Price, TinkoffOfferDto> = aggregateOffers(
            offers = tinkoffOrderBook.asksList
                .map { externalTinkoffOrder: Order ->
                    TinkoffOfferMapper.map(
                        externalTinkoffOrder = externalTinkoffOrder,
                        instrument = instrument,
                        affiliation = null,
                        direction = OfferDirection.SELL
                    )
                }
        )
        val undefinedBids: Map<Price, TinkoffOfferDto> = aggregateOffers(
            offers = tinkoffOrderBook.bidsList
                .map { externalTinkoffOrder: Order ->
                    TinkoffOfferMapper.map(
                        externalTinkoffOrder = externalTinkoffOrder,
                        instrument = instrument,
                        affiliation = null,
                        direction = OfferDirection.BUY
                    )
                }
        )

        return OfferBook(
            ts = Instant.ofEpochSecond(
                tinkoffOrderBook.orderbookTs.seconds,
                tinkoffOrderBook.orderbookTs.nanos.toLong()
            ),
            asks = combineOffers(
                selfOffers = selfAsks,
                undefinedOffers = undefinedAsks
            ),
            bids = combineOffers(
                selfOffers = selfBids,
                undefinedOffers = undefinedBids
            )
        )
    }

    private fun aggregateOffers(offers: List<TinkoffOfferDto>): Map<Price, TinkoffOfferDto> {
        return offers.groupBy { it.price }
            .map { it.value.reduce(TinkoffOfferDto::plus) }
            .associateBy { it.price }
    }

    private fun combineOffers(
        selfOffers: Map<Price, TinkoffOfferDto>,
        undefinedOffers: Map<Price, TinkoffOfferDto>
    ): Map<Price, QuotationLevelOffers> {
        val allPrices: Set<Price> = undefinedOffers.keys
        
        return allPrices.associateWith { price ->
            val selfOffer: TinkoffOfferDto? = selfOffers[price]
            val undefinedOffer: TinkoffOfferDto? = undefinedOffers[price]
            if ((undefinedOffer?.lots ?: 0L) < (selfOffer?.lots ?: 0L)) {
                throw IllegalStateException("More self offers than all")
            }
            val alienOffer: TinkoffOfferDto? = undefinedOffer?.let {
                val newAlienOffer: TinkoffOfferDto = it.copy(
                    lots = it.lots - (selfOffer?.lots ?: 0L),
                    affiliation = OfferAffiliation.ALIEN
                )
                if (newAlienOffer.lots == 0L) {
                    null
                } else {
                    newAlienOffer
                }
            }

            QuotationLevelOffers(
                selfAffiliated = TinkoffOfferMapper.map(selfOffer),
                alienAffiliated = TinkoffOfferMapper.map(alienOffer)
            )
        }
    }
}
