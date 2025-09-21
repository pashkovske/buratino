package ru.pashkovske.buratino.price.offer.model

import ru.pashkovske.buratino.price.price.model.Quotation
import java.time.Instant

data class OfferBook(
    val ts: Instant,
    val asks: Map<Quotation, QuotationLevelOffers>,
    val bids: Map<Quotation, QuotationLevelOffers>
)