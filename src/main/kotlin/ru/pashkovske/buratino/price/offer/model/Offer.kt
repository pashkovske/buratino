package ru.pashkovske.buratino.price.offer.model

import ru.pashkovske.buratino.price.quotation.model.Quotation

data class Offer(
    val price: Quotation,
    val lots: Long,
    val direction: OfferDirection,
    val affiliation: OfferAffiliation
) {
    operator fun plus(other: Offer): Offer {
        if (price != other.price) {
            throw IllegalArgumentException("Cannot add offers with different prices")
        }
        if (direction != other.direction) {
            throw IllegalArgumentException("Cannot add offers with different directions")
        }
        if (affiliation != other.affiliation) {
            throw IllegalArgumentException("Cannot add offers with different affiliations")
        }
        return Offer(
            price = price,
            lots = lots + other.lots,
            direction = direction,
            affiliation = affiliation
        )
    }
}