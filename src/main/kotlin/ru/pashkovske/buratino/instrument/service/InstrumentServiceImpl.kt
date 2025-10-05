package ru.pashkovske.buratino.instrument.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.repo.InstrumentRepo
import java.time.Instant

@Service
class InstrumentServiceImpl(
    private val instrumentServiceAdapter: InstrumentServiceAdapter,
    private val instrumentRepo: InstrumentRepo
) : InstrumentService {
    override fun getByTicker(ticker: String): Instrument {
        return instrumentServiceAdapter.getByTicker(ticker)
    }

    override fun get(iid: InstrumentId): Instrument {
        val lastUpdate = instrumentRepo.getLastUpdate(iid)
        if (lastUpdate == null) {
            instrumentRepo.create(instrumentServiceAdapter.get(iid))
        }
        else if (lastUpdate.plusSeconds(5).isBefore(Instant.now())) {
            instrumentRepo.update(instrumentServiceAdapter.get(iid))
        }
        return instrumentRepo.get(iid)
    }
}