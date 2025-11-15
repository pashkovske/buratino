package ru.pashkovske.buratino.instrument.model

import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price

open class Instrument(
    open val iid: InstrumentId,
    open val name: String,
    open val ticker: String,
    open val minPriceIncrement: Price,
    open val lot: Int,
    open val isTradable: Boolean,
    open val forQualifiedInvestorOnly: Boolean,
    open val currency: Currency,
    open val type: InstrumentType
)
