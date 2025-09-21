package ru.pashkovske.buratino.order.model.limit

import ru.pashkovske.buratino.instrument.model.InstrumentId
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.model.OrderRequest
import ru.pashkovske.buratino.price.price.model.MoneyPrice
import java.util.UUID

data class LimitOrderRequest(
    override val iid: InstrumentId,
    override val direction: OrderDirection,
    override val lots: Long,
    override val idempotencyToken: UUID?,
    val price: MoneyPrice
) : OrderRequest(
    iid = iid,
    direction = direction,
    lots = lots,
    idempotencyToken = idempotencyToken
)