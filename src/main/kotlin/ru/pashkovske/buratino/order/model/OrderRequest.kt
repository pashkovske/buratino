package ru.pashkovske.buratino.order.model

import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class OrderRequest(
    open val iid: InstrumentId,
    open val direction: OrderDirection,
    open val lots: Long,
    open val idempotencyToken: UUID?
)
