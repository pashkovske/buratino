package ru.pashkovske.buratino.assignment.model

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

abstract class LimitedOrderAssignment(
    override val iid: InstrumentId,
    open val direction: OrderDirection,
    val info: OrderInfo = OrderInfo()
): InstrumentAssignment(
    iid = iid
)