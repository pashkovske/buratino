package ru.pashkovske.buratino.instrument.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId

@Service
class InstrumentServiceImpl(
    private val instrumentServiceAdapter: InstrumentServiceAdapter
) : InstrumentService {
    override fun getByTicker(ticker: String): Instrument {
        return instrumentServiceAdapter.getByTicker(ticker)
    }

    override fun get(iid: InstrumentId): Instrument {
        return instrumentServiceAdapter.get(iid)
    }
}