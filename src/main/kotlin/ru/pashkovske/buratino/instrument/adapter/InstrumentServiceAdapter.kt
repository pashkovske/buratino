package ru.pashkovske.buratino.instrument.adapter

import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Share

interface InstrumentServiceAdapter {
    fun getByName(name: String): Instrument
    fun findByName(name: String): List<Instrument>
    fun getByTicker(ticker: String): Instrument
    fun get(iid: InstrumentId): Instrument
    fun getTradableShares(): List<Share>
    fun getTradableFutures(): List<Future>
}