package ru.pashkovske.buratino.integration.mock

import org.mockito.Mockito
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.price.offer.model.OfferBook
import ru.pashkovske.buratino.price.offer.service.OfferBookService
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class OfferBookMocker(
    private val mock: OfferBookService
) {
    fun addMock(
        path: String,
        depth: Int,
        iid: InstrumentId
    ) {
        Mockito.`when`(mock.getOfferBook(iid, depth))
            .thenReturn(FileLoader.loadFromJson(path, OfferBook::class.java))
    }
}
