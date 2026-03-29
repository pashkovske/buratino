package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.LimitOrderAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.limit.order.OrderInfo
import ru.pashkovske.buratino.assignment.model.core.LimitOrderAssignment

abstract class LimitOrderAssignmentToPostgreMapper<
    A : LimitOrderAssignment,
    Row : LimitOrderAssignmentPostgreRow<A>
    >: BasicAssignmentToPostgreMapper<A, Row>() {

    protected fun mapOrderInfo(row: Row): OrderInfo {
        return OrderInfo(
            orderId = row.orderId
        )
    }
}