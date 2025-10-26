package ru.pashkovske.buratino.price.quotation.model

class PtsPrice(
    units: Long,
    nano: Int,
): Quotation(
    units = units,
    nano = nano
)
