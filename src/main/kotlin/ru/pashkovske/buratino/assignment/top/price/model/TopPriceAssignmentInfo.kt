package ru.pashkovske.buratino.assignment.top.price.model

import ru.pashkovske.buratino.order.model.Order
import java.time.Instant

data class TopPriceAssignmentInfo(
    var order: Order? = null,
    var lastUpdate: Instant = Instant.now()
)