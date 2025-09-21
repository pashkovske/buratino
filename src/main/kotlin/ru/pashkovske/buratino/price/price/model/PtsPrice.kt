package ru.pashkovske.buratino.price.price.model

open class PtsPrice(
    override val units: Long,
    override val nano: Int,
): Quotation(
    units = units,
    nano = nano
)
