package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.price.model.Currency
import ru.pashkovske.buratino.price.price.model.MoneyPrice
import ru.pashkovske.buratino.price.price.model.PtsPrice

data class Future(
    override val iid: InstrumentId,
    override val name: String,
    override val ticker: String,
    override val minPriceIncrement: MoneyPrice,
    override val lot: Int,
    override val isTradable: Boolean,
    override val forQualifiedInvestorOnly: Boolean,
    override val currency: Currency,
    val minPriceIncrementPts: PtsPrice
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
