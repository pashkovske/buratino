package ru.pashkovske.buratino.assignment.service.limit.order

import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.price.model.Price
import java.util.UUID

abstract class BasicLimitOrderFactory<LimitA : LimitOrderAssignment>: LimitOrderFactory<LimitA> {

    override fun getOrderId(assignment: LimitA): String {
        return assignment.info.orderId
            ?: throw IllegalArgumentException("No order found in assignment ${assignment.id}. Probably it was not started or already canceled")
    }

    override fun buildLimitOrderRequest(assignment: LimitA): LimitOrderRequest {
        return LimitOrderRequest(
            iid = assignment.iid,
            direction = assignment.direction,
            lots = 1,
            idempotencyToken = UUID.randomUUID(),
            price = getPrice(assignment)
        )
    }

    abstract fun getPrice(assignment: LimitA): Price
}
