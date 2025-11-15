package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.PointsPrice
import ru.pashkovske.buratino.price.model.Price

class Future(
    iid: InstrumentId,
    name: String,
    ticker: String,
    minPriceIncrement: Price,
    lot: Int,
    isTradable: Boolean,
    forQualifiedInvestorOnly: Boolean,
    currency: Currency,
    val minPriceIncrementPts: PointsPrice
) : Instrument(
    iid = iid,
    name = name,
    ticker = ticker,
    minPriceIncrement = minPriceIncrement,
    lot = lot,
    isTradable = isTradable,
    forQualifiedInvestorOnly = forQualifiedInvestorOnly,
    currency = currency,
    type = InstrumentType.FUTURE
)
