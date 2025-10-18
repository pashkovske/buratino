package ru.pashkovske.buratino.assignment.limit.top.price.model

import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

data class TopPriceAssignment(
    override val iid: InstrumentId,
    override val direction: OrderDirection,
    val oneStepOver: Boolean = false
) :  LimitedOrderAssignment(
    iid = iid,
    direction = direction
)