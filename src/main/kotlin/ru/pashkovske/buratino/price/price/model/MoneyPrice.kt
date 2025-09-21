package ru.pashkovske.buratino.price.price.model

import ru.tinkoff.piapi.contract.v1.MoneyValue

data class MoneyPrice(
    override val units: Long,
    override val nano: Int,
    val currency: Currency,
) : Quotation(units, nano)
