package ru.pashkovske.buratino.price.offer.service

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.repo.OfferBookRepo

@Component
class MarketScrapper(
    private val offerBookService: OfferBookService,
    private val offerBookRepo: OfferBookRepo
) {
    fun updateOfferBook(
        iid: InstrumentId,
        depth: Int
    ) {
        val offerBook = offerBookService.getOfferBook(iid, depth)
        offerBookRepo.update(iid, offerBook)
    }
}