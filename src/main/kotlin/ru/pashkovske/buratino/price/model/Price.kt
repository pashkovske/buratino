package ru.pashkovske.buratino.price.model

import ru.pashkovske.buratino.price.quotation.model.Quotation

class Price(
    units: Long,
    nano: Int,
    val currency: Currency,
) : Quotation(units, nano) {
    constructor(
        quotation: Quotation,
        currency: Currency
    ): this(
        units = quotation.units,
        nano = quotation.nano,
        currency = currency
    )

    operator fun plus(other: Price): Price {
        if (currency != other.currency) {
            throw IllegalArgumentException("Currencies must be the same")
        }
        val sum: Quotation = super.plus(other)
        return Price(
            quotation = sum,
            currency = currency
        )
    }

    operator fun minus(other: Price): Price {
        if (currency != other.currency) {
            throw IllegalArgumentException("Currencies must be the same")
        }
        val diff: Quotation = super.minus(other)
        return Price(
            quotation = diff,
            currency = currency
        )
    }

    override operator fun times(multiplier: Int): Price {
        val result: Quotation = super.times(multiplier)
        return Price(
            quotation = result,
            currency = currency
        )
    }

    override operator fun times(multiplier: Double): Price {
        val result: Quotation = super.times(multiplier)
        return Price(
            quotation = result,
            currency = currency
        )
    }

    operator fun div(other: Price): Long {
        return super.div(other)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Price) return false
        return super.equals(other) && currency == other.currency
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }
}
