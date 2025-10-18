package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice

data class Share(
    override val iid: InstrumentId,
    override val name: String,
    override val ticker: String,
    override val lot: Int,
    override val isTradable: Boolean,
    override val forQualifiedInvestorOnly: Boolean,
    override val minPriceIncrement: MoneyPrice,
    override val currency: Currency
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
