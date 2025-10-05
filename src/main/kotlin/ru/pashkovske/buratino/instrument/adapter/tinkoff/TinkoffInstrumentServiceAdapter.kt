package ru.pashkovske.buratino.instrument.adapter.tinkoff

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.model.Share
import ru.pashkovske.buratino.instrument.adapter.InstrumentServiceAdapter
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.contract.v1.InstrumentType
import ru.tinkoff.piapi.core.InstrumentsService
import java.util.Objects
import kotlin.collections.map

@Service
class TinkoffInstrumentServiceAdapter(
    private val instrumentsService: InstrumentsService
) : InstrumentServiceAdapter {
    private val mapper = TinkoffInstrumentMapper

    override fun getByName(name: String): Instrument {
        return getFullInfo(findExactlyOne(name))
    }

    override fun findByName(name: String): List<Instrument> {
        return findInstruments(name)
            .map(::getFullInfo)
    }

    override fun getByTicker(ticker: String): Instrument {
        val instruments =  instrumentsService.findInstrumentSync(ticker)
            .filter { it.ticker == ticker }
            .filterNot(InstrumentShort::getForQualInvestorFlag)
            .filter(InstrumentShort::getApiTradeAvailableFlag)
            .map(::getFullInfo)
            .filter(Instrument::isTradable)
        check(instruments.size == 1) {
            "По тикеру `$ticker` найдено ${instruments.size} инструментов вместо 1-го:\n" +
                instruments.joinToString("\n", transform = Objects::toString)
        }

        return instruments.first()
    }

    override fun get(iid: InstrumentId): Instrument {
        val tinkoffInstrument: InstrumentShort = findExactlyOne(iid.id)
        return getFullInfo(tinkoffInstrument)
    }

    private fun getFullInfo(tinkoffInstrument: InstrumentShort): Instrument {
        val iid = InstrumentId(tinkoffInstrument.uid)
        return when (tinkoffInstrument.instrumentKind) {
            InstrumentType.INSTRUMENT_TYPE_FUTURES -> getFuture(iid)
            InstrumentType.INSTRUMENT_TYPE_SHARE -> getShare(iid)
            else -> throw IllegalStateException("Неподдерживаемый тип инструмента: ${tinkoffInstrument.instrumentKind}")
        }
    }

    override fun getTradableShares(): List<Share> {
        return instrumentsService.tradableSharesSync
            .map(mapper::map)
            .filterNot(Instrument::forQualifiedInvestorOnly)
            .filter(Instrument::isTradable)
    }

    fun getShare(iid: InstrumentId): Share {
        return mapper.map(
            instrumentsService.getShareByUidSync(iid.id)
        )
    }

    override fun getTradableFutures(): List<Future> {
        return instrumentsService.tradableFuturesSync
            .map(mapper::map)
            .filterNot(Instrument::forQualifiedInvestorOnly)
            .filter(Instrument::isTradable)
    }

    fun getFuture(iid: InstrumentId): Future {
        return mapper.map(
            instrumentsService.getFutureByUidSync(iid.id)
        )
    }

    private fun findInstruments(query: String): List<InstrumentShort> {
        return instrumentsService.findInstrumentSync(query)
            .filter(InstrumentShort::getApiTradeAvailableFlag)
    }

    private fun findExactlyOne(query: String): InstrumentShort {
        val instruments = findInstruments(query)
        check(instruments.size == 1) {
            "По запросу `$query` найдено ${instruments.size} инструментов вместо 1-го:\n" +
                instruments.joinToString("\n", transform = Objects::toString)
        }
        return instruments.first()
    }
}