package ru.pashkovske.buratino.price.price.model

import java.math.BigInteger

open class Quotation(
    open val units: Long,
    open val nano: Int
): Comparable<Quotation> {
    companion object {
        private const val MAX_NANO = 1_000_000_000
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

    operator fun rem(other: Quotation): Long {
        val dividend = toBigInt(this)
        val divisor = toBigInt(other)
        val quotient = dividend.divideAndRemainder(divisor)
        return quotient[1].toLong()
    }

    override operator fun compareTo(other: Quotation): Int {
        if (this.units != other.units) {
            return this.units.compareTo(other.units)
        }
        return this.nano.compareTo(other.nano)
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
}
