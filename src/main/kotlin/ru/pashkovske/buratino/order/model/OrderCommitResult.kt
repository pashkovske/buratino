package ru.pashkovske.buratino.order.model

import ru.pashkovske.buratino.price.model.Price
import java.time.Instant

data class OrderCommitResult(
    val commission: Price,
    val time: Instant
)