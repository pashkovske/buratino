package ru.pashkovske.buratino.instrument.model

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
    val minPriceIncrementPts: PtsPrice
) : Instrument(
    iid = iid,
    name = name,
    ticker = ticker,
    minPriceIncrement = minPriceIncrement,
    lot = lot,
    isTradable = isTradable,
    forQualifiedInvestorOnly = forQualifiedInvestorOnly,
    type = InstrumentType.FUTURE
)
