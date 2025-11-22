package ru.pashkovske.buratino.price.offer.adapter.tinkoff.dto

import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.offer.model.OfferAffiliation
import ru.pashkovske.buratino.price.offer.model.OfferDirection

data class TinkoffOfferDto(
    val price: Price,
    val lots: Long,
    val affiliation: OfferAffiliation?,
    val direction: OfferDirection
) {
    operator fun plus(other: TinkoffOfferDto): TinkoffOfferDto {
        if (price != other.price) {
            throw IllegalArgumentException("Cannot combine TinkoffOfferDtos with different prices")
        }
        if (direction != other.direction) {
            throw IllegalArgumentException("Cannot combine TinkoffOfferDtos with different directions")
        }
        if (
            affiliation != other.affiliation
            && affiliation != null
            && other.affiliation != null
        ) {
            throw IllegalArgumentException("Cannot combine TinkoffOfferDtos with different affiliations")
        }
        val resultAffiliation = if (affiliation == null || other.affiliation == null) {
            null
        } else {
            affiliation
        }
        return TinkoffOfferDto(
            price = price,
            lots = lots + other.lots,
            affiliation = resultAffiliation,
            direction = direction
        )
    }
}
