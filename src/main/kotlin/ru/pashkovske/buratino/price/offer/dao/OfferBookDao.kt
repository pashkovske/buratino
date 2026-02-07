package ru.pashkovske.buratino.price.offer.dao

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.OfferBook

interface OfferBookDao {
    fun read(iid: InstrumentId): OfferBook?
    fun update(iid: InstrumentId, offerBook: OfferBook)
    fun delete(iid: InstrumentId)
}
