package ru.pashkovske.buratino.unit.price

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.pashkovske.buratino.price.quotation.model.Quotation

class QuotationArithmeticTest {
    @Test
    fun testMulInt() {
        val mulLeft = Quotation(
            units = 0L,
            nano = 812_260_000
        )
        val mulRight = 93L

        val mulResult = mulLeft * mulRight.toInt()
        val expectedMulResult = Quotation(
            units = 75L,
            nano = 540_180_000
        )

        assertEquals(expectedMulResult, mulResult)
    }

    @Test
    fun testMulDouble() {
        val mulLeft = Quotation(
            units = 10_812L,
            nano = 812_260_000
        )
        val mulRight = 0.007

        val mulResult = mulLeft * mulRight
        val expectedMulResult = Quotation(
            units = 75L,
            nano = 689_685_820
        )

        assertEquals(expectedMulResult, mulResult)
    }

    @Test
    fun testDivQuotation() {
        val divisible = Quotation(
            units = 75L,
            nano = 689_685_820
        )
        val divisor = Quotation(
            units = 0L,
            nano = 812_260_000
        )

        val quotient: Long = divisible / divisor
        val expectedQuotient = 93L

        assertEquals(expectedQuotient, quotient)
    }
}
