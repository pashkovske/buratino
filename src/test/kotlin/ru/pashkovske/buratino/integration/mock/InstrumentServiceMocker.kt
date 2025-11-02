package ru.pashkovske.buratino.integration.mock

import org.mockito.Mockito
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class InstrumentServiceMocker(
    private val mock: InstrumentServiceAdapter
) {
    fun addMock(stubPath: String) {
        val instrument: Instrument = FileLoader.loadFromJson(
            path = stubPath,
            clazz = Instrument::class.java
        )
        Mockito.`when`(mock.get(instrument.iid))
            .thenReturn(instrument)
    }
}
