package ru.pashkovske.buratino.price.offer.service

import org.springframework.stereotype.Component
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.adapter.OfferBookAdapter
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.dao.OfferBookDao
import java.time.Duration
import java.time.Instant

private val bookTTL: Duration = Duration.ofSeconds(3L)

@Component
class MarketScrapper(
    private val offerBookAdapter: OfferBookAdapter,
    private val offerBookDao: OfferBookDao
) {

    fun updateOfferBook(
        iid: InstrumentId,
        depth: Int
    ) {
        if (!isOutdated(iid)) {
            return
        }
        val offerBook = offerBookAdapter.getOfferBook(iid, depth)
        offerBookDao.update(iid, offerBook)
    }

    fun isOutdated(iid: InstrumentId): Boolean {
        val oldOfferBook: OfferBook? = offerBookDao.read(iid)
        val lastUpdate: Instant = oldOfferBook?.ts ?: return true
        return lastUpdate < Instant.now().minus(bookTTL)
    }
}
