package ru.pashkovske.buratino.instrument.dao

import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.time.Instant

interface InstrumentDao {
    fun get(iid: InstrumentId): Instrument
    fun create(instrument: Instrument): Instrument
    fun update(instrument: Instrument): Instrument
    fun getLastUpdate(iid: InstrumentId): Instant?
}