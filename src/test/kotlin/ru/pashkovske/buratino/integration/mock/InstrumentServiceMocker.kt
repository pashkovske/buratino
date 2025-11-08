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
    private val iidAliases: MutableMap<String, InstrumentId> = mutableMapOf()

    fun <T: Instrument> addMock(
        stubPath: String,
        instrumentType: Class<T>
    ) {
        val instrument: Instrument = FileLoader.loadFromJson(
            path = stubPath,
            clazz = instrumentType
        )
        val alias = FileLoader.fileNameToAlias(stubPath)
        iidAliases[alias] = instrument.iid
        Mockito.`when`(mock.get(instrument.iid))
            .thenReturn(instrument)
    }

    fun getIid(alias: String): InstrumentId? {
        return iidAliases[alias]
    }
}
