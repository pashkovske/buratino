package ru.pashkovske.buratino.price.offer.repo

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.OfferBook

interface OfferBookRepo {
    fun read(iid: InstrumentId): OfferBook?
    fun update(iid: InstrumentId, offerBook: OfferBook)
    fun delete(iid: InstrumentId)
}
