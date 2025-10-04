package ru.pashkovske.buratino.assignment.top.price.model

import ru.pashkovske.buratino.assignment.model.InstrumentAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection

data class TopPriceAssignment(
    override val iid: InstrumentId,
    val direction: OrderDirection,
    val info: TopPriceAssignmentInfo = TopPriceAssignmentInfo()
) : InstrumentAssignment(iid = iid)