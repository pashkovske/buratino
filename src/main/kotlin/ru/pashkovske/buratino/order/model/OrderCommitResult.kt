package ru.pashkovske.buratino.order.model

import ru.pashkovske.buratino.price.money.model.MoneyPrice
import java.time.Instant

data class OrderCommitResult(
    val commission: MoneyPrice,
    val time: Instant
)