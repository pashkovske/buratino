package ru.pashkovske.buratino.unit.price

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price

class PriceArithmeticTest {
    @Test
    fun testMulInt() {
        val mulLeft = Price(
            unit = 0L,
            nano = 812_260_000,
            currency = Currency.RUB
        )
        val mulRight = 93L

        val mulResult = mulLeft * mulRight
        val expectedMulResult = Price(
            unit = 75L,
            nano = 540_180_000,
            currency = Currency.RUB
        )

        assertEquals(expectedMulResult, mulResult)
    }

    @Test
    fun testMulDouble() {
        val mulLeft = Price(
            unit = 10_812L,
            nano = 812_260_000,
            currency = Currency.RUB
        )
        val mulRight = 0.007

        val mulResult = mulLeft * mulRight
        val expectedMulResult = Price(
            unit = 75L,
            nano = 689_685_820,
            currency = Currency.RUB
        )

        assertEquals(expectedMulResult, mulResult)
    }

    @Test
    fun testDivQuotation() {
        val divisible = Price(
            unit = 75L,
            nano = 689_685_820,
            currency = Currency.RUB
        )
        val divisor = Price(
            unit = 0L,
            nano = 812_260_000,
            currency = Currency.RUB
        )

        val quotient: Long = divisible / divisor
        val expectedQuotient = 93L

        assertEquals(expectedQuotient, quotient)
    }
}
