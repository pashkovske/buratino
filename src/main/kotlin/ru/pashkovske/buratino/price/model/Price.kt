package ru.pashkovske.buratino.price.model

import java.math.BigDecimal
import java.math.BigInteger

data class Price(
    val units: Long,
    val nano: Int,
    val currency: Currency
): Comparable<Price> {
    companion object {
        private const val MAX_NANO = 1_000_000_000

        fun fromString(value: String): Price {
            val parts: List<String> = value.split(" ")
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid price format: $value")
            }

            val currencyCode: String = parts[1]
            val currency: Currency = Currency.fromStr(currencyCode)

            val decimalParts: List<String> = parts[0].split(".")
            val unit: Long = decimalParts[0].toLong()

            val nano: Int = if (decimalParts.size > 1) {
                val nanoStr = decimalParts[1].padEnd(9, '0')
                nanoStr.take(9).toInt()
            } else {
                0
            }

            return Price(unit, nano, currency)
        }
    }

    init {
        if (nano !in 0..<MAX_NANO) {
            throw IllegalArgumentException("Nano must be in range [0, 1000000000)")
        }
    }

    override fun toString(): String {
        return "$units.$nano $currency"
            .trimEnd('0')
            .trimEnd('.')
    }

    fun makeZero(): Price {
        return copy(
            newUnit = 0L,
            newNano = 0
        )
    }

    fun copy(
        newUnit: Long,
        newNano: Int
    ): Price {
        return Price(
            units = newUnit,
            nano = newNano,
            currency = currency
        )
    }

    fun checkConsistency(other: Price) {
        if (this.currency != other.currency) {
            throw IllegalArgumentException("Currencies must be the same")
        }
    }

    operator fun plus(other: Price): Price {
        checkConsistency(other)
        return fromBigInt(
            this
                .toBigInt()
                .plus(other.toBigInt()))
    }

    operator fun minus(other: Price): Price {
        checkConsistency(other)
        return fromBigInt(
            this
                .toBigInt()
                .minus(other.toBigInt()))
    }

    operator fun times(multiplier: Long): Price {
        return fromBigInt(
            this
                .toBigInt()
                .multiply(BigInteger.valueOf(multiplier)))
    }

    operator fun times(multiplier: Double): Price {
        val roundedResult: BigDecimal = toBigInt(this)
            .toBigDecimal()
            .multiply(multiplier.toBigDecimal())
            .setScale(0)
        return fromBigInt(roundedResult.toBigInteger())
    }

    operator fun div(other: Price): Long {
        checkConsistency(other)
        val dividend = toBigInt(this)
        val divisor = toBigInt(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return quotient[0].toLong()
    }

    operator fun div(other: Long): Price {
        val dividend = toBigInt(this)
        val divisor = BigInteger.valueOf(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return fromBigInt(quotient[0])
    }

    operator fun rem(other: Price): Long {
        checkConsistency(other)
        val dividend = toBigInt(this)
        val divisor = toBigInt(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return quotient[1].toLong()
    }

    operator fun rem(other: Long): Price {
        val dividend = toBigInt(this)
        val divisor = BigInteger.valueOf(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return fromBigInt(quotient[1])
    }

    override operator fun compareTo(other: Price): Int {
        checkConsistency(other)
        if (this.units != other.units) {
            return this.units.compareTo(other.units)
        }
        return this.nano.compareTo(other.nano)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Price) return false
        return units == other.units
            && nano == other.nano
            && currency == other.currency
    }

    override fun hashCode(): Int {
        return units.hashCode()
            .xor(nano.hashCode())
            .xor(currency.hashCode())
    }

    private fun toBigInt(price: Price): BigInteger {
        return BigInteger.valueOf(price.units)
            .multiply(BigInteger.valueOf(MAX_NANO.toLong()))
            .add(BigInteger.valueOf(price.nano.toLong()))
    }

    private fun toBigInt(): BigInteger {
        return toBigInt(this)
    }

    private fun fromBigInt(bigInt: BigInteger): Price {
        val quotient = bigInt.divideAndRemainder(BigInteger.valueOf(MAX_NANO.toLong()))
        return copy(
            newUnit = quotient[0]!!.longValueExact(),
            newNano = quotient[1]!!.intValueExact()
        )
    }
}
