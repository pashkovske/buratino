package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.price.offer.model.Offer
import ru.pashkovske.buratino.price.offer.model.OfferAffiliation
import ru.pashkovske.buratino.price.offer.model.OfferDirection
import ru.tinkoff.piapi.contract.v1.Order
import ru.tinkoff.piapi.contract.v1.OrderDirection
import ru.tinkoff.piapi.contract.v1.OrderState

object TinkoffOfferMapper {
    fun map(tinkoffDirection: OrderDirection): OfferDirection {
        return when (tinkoffDirection) {
            OrderDirection.ORDER_DIRECTION_BUY -> OfferDirection.BUY
            OrderDirection.ORDER_DIRECTION_SELL -> OfferDirection.SELL
            else -> throw IllegalArgumentException("Unsupported direction for offer: $tinkoffDirection")
        }
    }

    fun map(
        tinkoffOrder: Order,
        direction: OfferDirection,
        affiliation: OfferAffiliation
    ): Offer {
        return Offer(
            price = TinkoffPriceMapper.map(tinkoffOrder.price),
            lots = tinkoffOrder.quantity,
            direction = direction,
            affiliation = affiliation
        )
    }


    fun map(
        tinkoffOrder: OrderState,
        affiliation: OfferAffiliation
    ): Offer {
        return Offer(
            price = TinkoffPriceMapper.map(tinkoffOrder.initialOrderPrice),
            lots = tinkoffOrder.lotsRequested - tinkoffOrder.lotsExecuted,
            direction = map(tinkoffOrder.direction),
            affiliation = affiliation
        )
    }
}