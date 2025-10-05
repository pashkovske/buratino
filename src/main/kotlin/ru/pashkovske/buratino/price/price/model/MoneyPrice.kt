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
    operator fun plus(other: MoneyPrice): MoneyPrice {
        if (currency != other.currency) {
            throw IllegalArgumentException("Currencies must be the same")
        }
        val sum: Quotation = super.plus(other)
        return MoneyPrice(
            quotation = sum,
            currency = currency
        )
    }

    operator fun minus(other: MoneyPrice): MoneyPrice {
        if (currency != other.currency) {
            throw IllegalArgumentException("Currencies must be the same")
        }
        val diff: Quotation = super.minus(other)
        return MoneyPrice(
            quotation = diff,
            currency = currency
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MoneyPrice) return false
        return super.equals(other) && currency == other.currency
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }
}
