package ru.pashkovske.buratino.price.offer.service

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.OfferBook

interface OfferBookService {
    fun getOfferBook(
        iid: InstrumentId,
        depth: Int
    ): OfferBook
}