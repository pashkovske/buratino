package ru.pashkovske.buratino.price.quotation.model

open class PtsPrice(
    override val units: Long,
    override val nano: Int,
): Quotation(
    units = units,
    nano = nano
)
