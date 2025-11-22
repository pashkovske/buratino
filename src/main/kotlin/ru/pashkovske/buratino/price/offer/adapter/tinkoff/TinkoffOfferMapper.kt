package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.Share
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.dto.TinkoffOfferDto
import ru.pashkovske.buratino.price.offer.model.Offer
import ru.pashkovske.buratino.price.offer.model.OfferAffiliation
import ru.pashkovske.buratino.price.offer.model.OfferDirection
import ru.tinkoff.piapi.contract.v1.Order
import ru.tinkoff.piapi.contract.v1.OrderDirection
import ru.tinkoff.piapi.contract.v1.OrderState
import ru.tinkoff.piapi.contract.v1.Quotation

object TinkoffOfferMapper {

    fun map(tinkoffDirection: OrderDirection): OfferDirection {
        return when (tinkoffDirection) {
            OrderDirection.ORDER_DIRECTION_BUY -> OfferDirection.BUY
            OrderDirection.ORDER_DIRECTION_SELL -> OfferDirection.SELL
            else -> throw IllegalArgumentException("Unsupported direction for offer: $tinkoffDirection")
        }
    }

    fun map(
        externalTinkoffOrderState: OrderState,
        affiliation: OfferAffiliation
    ): TinkoffOfferDto {
        return TinkoffOfferDto(
            price = TinkoffPriceMapper.map(
                tinkoffMoneyValue = externalTinkoffOrderState.initialSecurityPrice
            ),
            lots = externalTinkoffOrderState.lotsRequested - externalTinkoffOrderState.lotsExecuted,
            direction = map(externalTinkoffOrderState.direction),
            affiliation = affiliation
        )
    }

    fun map(
        externalTinkoffOrder: Order,
        instrument: Instrument,
        affiliation: OfferAffiliation?,
        direction: OfferDirection
    ): TinkoffOfferDto {
        val tinkoffRawQuotation: Quotation = Quotation.newBuilder()
            .setUnits(externalTinkoffOrder.price.units)
            .setNano(externalTinkoffOrder.price.nano)
            .build()
        val price: Price = when (instrument) {
            is Future -> TinkoffPriceMapper.convertPtsToMoney(
                pts = tinkoffRawQuotation,
                minPtsInc = instrument.minPriceIncrementPts,
                minPriceInc = instrument.minPriceIncrement
            )
            is Share -> TinkoffPriceMapper.map(
                tinkoffQuotation = tinkoffRawQuotation,
                currency = instrument.currency
            )
            else -> throw IllegalArgumentException("Unsupported instrument type: ${instrument.type}")
        }
        return TinkoffOfferDto(
            price = price,
            lots = externalTinkoffOrder.quantity,
            affiliation = affiliation,
            direction = direction
        )
    }

    fun map(tinkoffOfferDto: TinkoffOfferDto?): Offer? {
        if (tinkoffOfferDto == null) {
            return null
        }
        if (tinkoffOfferDto.affiliation == null) {
            throw IllegalArgumentException("TinkoffOfferDto affiliation is null")
        }
        return Offer(
            price = tinkoffOfferDto.price,
            lots = tinkoffOfferDto.lots,
            affiliation = tinkoffOfferDto.affiliation,
            direction = tinkoffOfferDto.direction
        )
    }
}
