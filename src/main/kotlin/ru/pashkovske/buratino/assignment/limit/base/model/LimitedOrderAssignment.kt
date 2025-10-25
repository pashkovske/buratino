package ru.pashkovske.buratino.assignment.limit.base.model

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

abstract class LimitedOrderAssignment(
    override val iid: InstrumentId,
    open val direction: OrderDirection,
    val info: OrderInfo = OrderInfo()
): Assignment(
    iid = iid
)