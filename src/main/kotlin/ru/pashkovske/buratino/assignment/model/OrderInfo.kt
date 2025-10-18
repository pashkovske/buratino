package ru.pashkovske.buratino.assignment.model

import java.time.Instant

data class OrderInfo(
    var orderId: String? = null,
    var lastUpdate: Instant = Instant.now()
)