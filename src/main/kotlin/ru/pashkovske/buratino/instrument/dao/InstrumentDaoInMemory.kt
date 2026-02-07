package ru.pashkovske.buratino.instrument.dao

import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Repository
class InstrumentDaoInMemory : InstrumentDao {
    private val instruments: MutableMap<InstrumentId, Instrument> = ConcurrentHashMap()
    private val updates: MutableMap<InstrumentId, Instant> = ConcurrentHashMap()

    override fun get(iid: InstrumentId): Instrument {
        if (iid !in instruments) {
            throw IllegalArgumentException("Instrument with id $iid not found")
        }
        return instruments[iid]!!
    }

    override fun create(instrument: Instrument): Instrument {
        val iid = instrument.iid
        if (iid in instruments) {
            throw IllegalArgumentException("Instrument with id $iid already exists")
        }
        instruments[iid] = instrument
        updates[iid] = Instant.now()
        return instrument
    }

    override fun update(instrument: Instrument): Instrument {
        val iid = instrument.iid
        if (iid !in instruments) {
            throw IllegalArgumentException("Instrument with id $iid not found")
        }
        instruments[iid] = instrument
        updates[iid] = Instant.now()
        return instrument
    }

    override fun getLastUpdate(iid: InstrumentId): Instant? {
        return updates[iid]
    }
}
