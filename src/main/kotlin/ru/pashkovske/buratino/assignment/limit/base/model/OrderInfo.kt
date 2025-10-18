package ru.pashkovske.buratino.assignment.limit.base.model

import java.time.Instant

data class OrderInfo(
    var orderId: String? = null,
    var lastUpdate: Instant = Instant.now()
)