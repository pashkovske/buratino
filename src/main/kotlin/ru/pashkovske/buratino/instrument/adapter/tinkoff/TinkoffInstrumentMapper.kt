package ru.pashkovske.buratino.instrument.adapter.tinkoff

import ru.pashkovske.buratino.instrument.model.Future
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.instrument.model.Share
import ru.pashkovske.buratino.price.offer.adapter.tinkoff.TinkoffPriceMapper
import ru.pashkovske.buratino.price.model.Currency
import ru.tinkoff.piapi.contract.v1.SecurityTradingStatus

object TinkoffInstrumentMapper {
    private val priceMapper = TinkoffPriceMapper

    fun isTradableNow(tinkoffSecurityTradingStatus: SecurityTradingStatus): Boolean {
        return when (tinkoffSecurityTradingStatus) {
            SecurityTradingStatus.SECURITY_TRADING_STATUS_NORMAL_TRADING -> true
            SecurityTradingStatus.SECURITY_TRADING_STATUS_SESSION_OPEN -> true
            SecurityTradingStatus.SECURITY_TRADING_STATUS_DEALER_NORMAL_TRADING -> true
            else -> false
        }
    }

    fun map(tinkoffShare: ru.tinkoff.piapi.contract.v1.Share): Share {
        return Share(
            iid = InstrumentId(tinkoffShare.uid),
            name = tinkoffShare.name,
            ticker = tinkoffShare.ticker,
            minPriceIncrement = priceMapper.map(
                tinkoffQuotation = tinkoffShare.minPriceIncrement,
                currency = tinkoffShare.currency
            ),
            lot = tinkoffShare.lot,
            isTradable = isTradableNow(tinkoffShare.tradingStatus),
            currency = Currency.fromStr(tinkoffShare.currency),
            forQualifiedInvestorOnly = tinkoffShare.forQualInvestorFlag,
        )
    }

    fun map(tinkoffFuture: ru.tinkoff.piapi.contract.v1.Future): Future {
        return Future(
            iid = InstrumentId(tinkoffFuture.uid),
            name = tinkoffFuture.name,
            ticker = tinkoffFuture.ticker,
            minPriceIncrement = priceMapper.map(
                tinkoffQuotation = tinkoffFuture.minPriceIncrementAmount,
                currency = tinkoffFuture.currency
            ),
            lot = tinkoffFuture.lot,
            isTradable = isTradableNow(tinkoffFuture.tradingStatus),
            forQualifiedInvestorOnly = tinkoffFuture.forQualInvestorFlag,
            currency = Currency.fromStr(tinkoffFuture.currency),
            minPriceIncrementPts = priceMapper.mapToPointsPrice(tinkoffFuture.minPriceIncrement)
        )
    }
}