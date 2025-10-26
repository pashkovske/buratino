package ru.pashkovske.buratino.integration.mock

import org.mockito.Mockito
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class InstrumentServiceMocker(
    private val mock: InstrumentServiceAdapter
) {
    fun addMock(
        path: String,
        iid: InstrumentId
    ) {
        Mockito.`when`(mock.get(iid))
            .thenReturn(FileLoader.loadFromJson(path, Instrument::class.java))
    }
}
