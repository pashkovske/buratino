package ru.pashkovske.buratino.instrument.repo

import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.time.Instant

interface InstrumentRepo {
    fun get(iid: InstrumentId): Instrument
    fun create(instrument: Instrument): Instrument
    fun update(instrument: Instrument): Instrument
    fun getLastUpdate(iid: InstrumentId): Instant?
}