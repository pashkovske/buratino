package ru.pashkovske.buratino.price.offer.adapter

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.OfferBook

interface OfferBookAdapter {
    fun getOfferBook(
        iid: InstrumentId,
        depth: Int
    ): OfferBook
}