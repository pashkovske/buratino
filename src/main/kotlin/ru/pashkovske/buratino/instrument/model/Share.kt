package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice

class Share(
    iid: InstrumentId,
    name: String,
    ticker: String,
    lot: Int,
    isTradable: Boolean,
    forQualifiedInvestorOnly: Boolean,
    minPriceIncrement: MoneyPrice,
    currency: Currency
) : Instrument(
    iid = iid,
    name = name,
    ticker = ticker,
    minPriceIncrement = minPriceIncrement,
    lot = lot,
    isTradable = isTradable,
    forQualifiedInvestorOnly = forQualifiedInvestorOnly,
    currency = currency,
    type = InstrumentType.SHARE
)
