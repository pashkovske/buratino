package ru.pashkovske.buratino.integration.mock

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.Offer
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.model.OfferDirection
import ru.pashkovske.buratino.price.offer.model.QuotationLevelOffers
import ru.pashkovske.buratino.price.offer.service.OfferBookService
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.util.loader.FileLoader
import java.time.Instant

class OfferBookMock: OfferBookService {
    private val offerBooks: MutableMap<InstrumentId, OfferBook> = mutableMapOf()

    fun addMock(
        path: String,
        iid: InstrumentId
    ) {
        val offerBook: OfferBook = FileLoader.loadFromJson(path, OfferBook::class.java)
        offerBooks[iid] = offerBook
    }

    fun addOfferToMock(
        iid: InstrumentId,
        offer: Offer
    ) {
        val offerBook: OfferBook = offerBooks.computeIfAbsent(iid) {
            OfferBook(
                asks = mutableMapOf(),
                bids = mutableMapOf(),
                ts = Instant.now()
            )
        }
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (offer.direction) {
            OfferDirection.SELL ->
                offerBook.copy(asks = updateOfferBookProperty(offerBook.asks, offer))
            OfferDirection.BUY ->
                offerBook.copy(bids = updateOfferBookProperty(offerBook.bids, offer))
            else -> throw IllegalArgumentException("Unknown direction: ${offer.direction}")
        }
    }

    private fun updateOfferBookProperty(
        property: Map<Quotation, QuotationLevelOffers>,
        offer: Offer
    ): Map<Quotation, QuotationLevelOffers> {
        val quotationLevel: QuotationLevelOffers = property.getOrDefault(
            key = offer.price,
            defaultValue = QuotationLevelOffers()
        )
        val newQuotationLevel: QuotationLevelOffers = quotationLevel + offer
        val newProperty: MutableMap<Quotation, QuotationLevelOffers> = property.toMutableMap()
        newProperty[offer.price] = newQuotationLevel
        return newProperty
    }

    override fun getOfferBook(
        iid: InstrumentId,
        depth: Int
    ): OfferBook {
        val fullOfferBook = offerBooks[iid] ?: throw IllegalArgumentException("No such iid: $iid")
        return fullOfferBook.copy(
            asks = fullOfferBook.asks
                .entries
                .sortedBy(Map.Entry<Quotation, QuotationLevelOffers>::key)
                .reversed()
                .take(depth)
                .associate { it.key to it.value },
            bids = fullOfferBook.bids
                .entries
                .sortedBy(Map.Entry<Quotation, QuotationLevelOffers>::key)
                .take(depth)
                .associate { it.key to it.value }
        )
    }
}
