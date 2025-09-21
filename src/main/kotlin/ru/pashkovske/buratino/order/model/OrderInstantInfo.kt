package ru.pashkovske.buratino.order.model

import java.time.Instant

data class OrderInstantInfo(
    val remainingLots: Long,
    val state: OrderState,
    val time: Instant
)