package ru.pashkovske.buratino.price.quotation.money.model

import ru.pashkovske.buratino.price.quotation.model.Quotation

class MoneyPrice(
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

    override operator fun times(multiplier: Int): MoneyPrice {
        val result: Quotation = super.times(multiplier)
        return MoneyPrice(
            quotation = result,
            currency = currency
        )
    }

    override operator fun times(multiplier: Double): MoneyPrice {
        val result: Quotation = super.times(multiplier)
        return MoneyPrice(
            quotation = result,
            currency = currency
        )
    }

    operator fun div(other: MoneyPrice): Long {
        return super.div(other)
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
