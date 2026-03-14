package ru.pashkovske.buratino.assignment.limit.base.service.order

import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest

interface LimitOrderFactory<LimitA : LimitOrderAssignment> {

    fun getOrderId(assignment: LimitA): String
    fun buildLimitOrderRequest(assignment: LimitA): LimitOrderRequest
}
