package ru.pashkovske.buratino.assignment.limit.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.repo.postgre.BasicAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo

abstract class LimitOrderAssignmentToPostgreMapper<
    A : LimitOrderAssignment,
    Row : LimitOrderAssignmentPostgreRow<A>
    >: BasicAssignmentToPostgreMapper<A, Row>() {

    protected fun mapOrderInfo(row: Row): OrderInfo {
        return OrderInfo(
            orderId = row.orderId,
            lastUpdate = row.lastOrderUpdate
        )
    }
}
