package ru.pashkovske.buratino.price.offer.model

import ru.pashkovske.buratino.price.model.Price
import java.time.Instant

data class OfferBook(
    val ts: Instant,
    val asks: Map<Price, QuotationLevelOffers>,
    val bids: Map<Price, QuotationLevelOffers>
)
