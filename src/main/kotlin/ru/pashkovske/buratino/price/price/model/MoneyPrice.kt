package ru.pashkovske.buratino.price.price.model

data class MoneyPrice(
    override val units: Long,
    override val nano: Int,
    val currency: Currency,
) : Quotation(units, nano) {
    constructor(
        quotation: Quotation,
        currency: Currency
    ): this(
        units =quotation.units,
        nano = quotation.nano,
        currency = currency
    )
}
