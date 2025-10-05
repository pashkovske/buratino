package ru.pashkovske.buratino.instrument.service

import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId

interface InstrumentService {
    fun getByTicker(ticker: String): Instrument
    fun get(iid: InstrumentId): Instrument
}
