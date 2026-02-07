package ru.pashkovske.buratino.instrument.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.dao.InstrumentDao
import java.time.Instant

private val CACHE_TTL_SECONDS = 300L

@Service
class InstrumentServiceImpl(
    private val instrumentServiceAdapter: InstrumentServiceAdapter,
    private val instrumentDao: InstrumentDao
) : InstrumentService {
    override fun getByTicker(ticker: String): Instrument {
        return instrumentServiceAdapter.getByTicker(ticker)
    }

    override fun get(iid: InstrumentId): Instrument {
        val lastUpdate = instrumentDao.getLastUpdate(iid)
        if (lastUpdate == null) {
            instrumentDao.create(instrumentServiceAdapter.get(iid))
        }
        else if (lastUpdate.plusSeconds(CACHE_TTL_SECONDS).isBefore(Instant.now())) {
            instrumentDao.update(instrumentServiceAdapter.get(iid))
        }
        return instrumentDao.get(iid)
    }
}