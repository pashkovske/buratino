package ru.pashkovske.buratino.price.offer.dao

import ru.pashkovske.buratino.instrument.model.InstrumentId

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.price.offer.model.OfferBook
import java.util.concurrent.ConcurrentHashMap

@Repository
class OfferBookDaoInMemory : OfferBookDao {
    private val books: MutableMap<InstrumentId, OfferBook> = ConcurrentHashMap()

    override fun delete(iid: InstrumentId) {
        if (books.containsKey(iid)) {
            books.remove(iid)
        }
    }

    override fun read(iid: InstrumentId): OfferBook? {
        return books[iid]
    }

    override fun update(iid: InstrumentId, offerBook: OfferBook) {
        books[iid] = offerBook
    }
}