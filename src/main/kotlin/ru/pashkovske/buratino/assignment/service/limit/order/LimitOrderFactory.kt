package ru.pashkovske.buratino.assignment.service.limit.order

import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest

interface LimitOrderFactory<LimitA : LimitOrderAssignment> {

    fun getOrderId(assignment: LimitA): String
    fun buildLimitOrderRequest(assignment: LimitA): LimitOrderRequest
}
