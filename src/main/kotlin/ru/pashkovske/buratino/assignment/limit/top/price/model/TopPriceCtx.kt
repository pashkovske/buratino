package ru.pashkovske.buratino.assignment.limit.top.price.model

import ru.pashkovske.buratino.flow.exe.context.ExeCtx
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.price.model.Price

data class TopPriceCtx(
    val oneStepOver: Boolean,
    val iid: InstrumentId,
    val direction: OrderDirection,
    var topPrice: Price? = null,
    var rawTopPrice: Price? = null,
    var oneStepOverTopPrice: Price? = null
) : ExeCtx()
