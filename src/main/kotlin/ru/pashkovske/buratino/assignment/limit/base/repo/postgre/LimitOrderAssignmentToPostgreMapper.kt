package ru.pashkovske.buratino.assignment.limit.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.repo.postgre.BasicAssignmentToPostgreRowMapper
import ru.pashkovske.buratino.assignment.limit.base.model.LimitOrderAssignment
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo

abstract class LimitOrderAssignmentToPostgreMapper<
    A : LimitOrderAssignment,
    Row
    >: BasicAssignmentToPostgreRowMapper<A, Row>() {

    abstract fun mapOrderInfo(row: Row): OrderInfo
}
