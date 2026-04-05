package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.TopPriceAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment

@Service
class TopPriceAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >() {

    override fun map(row: TopPriceAssignmentRow): TopPriceAssignment {
        val assignment = TopPriceAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            state = row.state,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            oneStepOver = row.oneStepOver
        )
        assignment.initRefreshNotifierId(row.refreshSchedulingId)
        return assignment
    }

    override fun map(assignment: TopPriceAssignment): TopPriceAssignmentRow {
        return TopPriceAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            orderDirection = assignment.direction,
            oneStepOver = assignment.oneStepOver,
            refreshSchedulingPeriod = null,
            refreshSchedulingTaskId = null,
            refreshSchedulingState = null,
            refreshSchedulingId = assignment.getRefreshNotifierId(),
            orderId = assignment.info.orderId
        )
    }
}