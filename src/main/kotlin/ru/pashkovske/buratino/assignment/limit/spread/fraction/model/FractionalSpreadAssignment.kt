package ru.pashkovske.buratino.assignment.limit.spread.fraction.model

import ru.pashkovske.buratino.assignment.limit.base.model.LimitedOrderAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

data class FractionalSpreadAssignment(
    override val iid: InstrumentId,
    override val direction: OrderDirection,
    val rate: Double
): LimitedOrderAssignment(
    iid = iid,
    direction = direction
)
