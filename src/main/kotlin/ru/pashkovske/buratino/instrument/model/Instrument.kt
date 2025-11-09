package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.quotation.money.model.Currency
import ru.pashkovske.buratino.price.quotation.money.model.MoneyPrice

open class Instrument(
    open val iid: InstrumentId,
    open val name: String,
    open val ticker: String,
    open val minPriceIncrement: MoneyPrice,
    open val lot: Int,
    open val isTradable: Boolean,
    open val forQualifiedInvestorOnly: Boolean,
    open val currency: Currency,
    open val type: InstrumentType
)
