package ru.pashkovske.buratino.price.quotation.model

import java.math.BigInteger

open class Quotation(
    open val units: Long,
    open val nano: Int
): Comparable<Quotation> {
    companion object {
        private const val MAX_NANO = 1_000_000_000
        val ZERO = Quotation(0, 0)
    }

    override fun toString(): String {
        return "$units.$nano"
            .trimEnd('0')
            .trimEnd('.')
    }

    operator fun plus(other: Quotation): Quotation {
        var units = this.units + other.units
        var nano = this.nano + other.nano
        if (nano >= MAX_NANO) {
            units += 1
            nano -= MAX_NANO
        }
        return Quotation(units, nano)
    }

    operator fun minus(other: Quotation): Quotation {
        var units = this.units - other.units
        var nano = this.nano - other.nano
        if (nano < 0) {
            units -= 1
            nano += MAX_NANO
        }
        return Quotation(units, nano)
    }

    operator fun times(multiplier: Int): Quotation {
        var units = this.units * multiplier
        var nano = this.nano * multiplier
        units += nano / MAX_NANO
        nano %= MAX_NANO
        return Quotation(units, nano)
    }

    operator fun div(other: Quotation): Long {
        val dividend = toBigInt(this)
        val divisor = toBigInt(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return quotient[0].toLong()
    }

    operator fun div(other: Long): Quotation {
        val dividend = toBigInt(this)
        val divisor = BigInteger.valueOf(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return fromBigInt(quotient[0])
    }

    operator fun rem(other: Quotation): Long {
        val dividend = toBigInt(this)
        val divisor = toBigInt(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return quotient[1].toLong()
    }

    operator fun rem(other: Long): Quotation {
        val dividend = toBigInt(this)
        val divisor = BigInteger.valueOf(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return fromBigInt(quotient[1])
    }

    override operator fun compareTo(other: Quotation): Int {
        if (this.units != other.units) {
            return this.units.compareTo(other.units)
        }
        return this.nano.compareTo(other.nano)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quotation) return false
        return units == other.units && nano == other.nano
    }

    private fun toBigInt(price: Quotation): BigInteger {
        return BigInteger.valueOf(price.units)
            .multiply(BigInteger.valueOf(MAX_NANO.toLong()))
            .add(BigInteger.valueOf(price.nano.toLong()))
    }

    private fun fromBigInt(bigInt: BigInteger): Quotation {
        val quotient = bigInt.divideAndRemainder(BigInteger.valueOf(MAX_NANO.toLong()))
        return Quotation(quotient[0]!!.longValueExact(), quotient[1]!!.intValueExact())
    }

    override fun hashCode(): Int {
        var result = units.hashCode()
        result = 31 * result + nano.hashCode()
        return result
    }
}
