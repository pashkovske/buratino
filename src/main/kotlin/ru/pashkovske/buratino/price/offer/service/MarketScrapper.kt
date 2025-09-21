package ru.pashkovske.buratino.price.offer.service

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.repository.OfferBookRepo

@Component
class MarketScrapper(
    private val offerBookService: OfferBookService,
    private val offerBookRepo: OfferBookRepo
) {
    fun updateOfferBook(
        iid: InstrumentId,
        depth: Int,
        account: Account
    ) {
        val offerBook = offerBookService.getOfferBook(iid, depth, account)
        offerBookRepo.update(iid, offerBook)
    }
}