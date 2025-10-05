package ru.pashkovske.buratino.assignment.top.price.model

import java.time.Instant

data class TopPriceAssignmentInfo(
    var orderId: String? = null,
    var lastUpdate: Instant = Instant.now()
)