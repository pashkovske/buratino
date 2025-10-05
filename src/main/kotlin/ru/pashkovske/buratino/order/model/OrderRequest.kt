package ru.pashkovske.buratino.order.model

import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

abstract class OrderRequest(
    open val iid: InstrumentId,
    open val direction: OrderDirection,
    open val lots: Long,
    open val idempotencyToken: UUID?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderRequest) return false
        return iid == other.iid
            && direction == other.direction
            && lots == other.lots
    }

    override fun hashCode(): Int {
        var result = lots.hashCode()
        result = 31 * result + iid.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }
}
