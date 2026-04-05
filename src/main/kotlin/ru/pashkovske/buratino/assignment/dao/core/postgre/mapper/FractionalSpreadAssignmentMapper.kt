package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.FractionalSpreadAssignment

@Service
class FractionalSpreadAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >() {

    override fun map(row: FractionalSpreadAssignmentRow): FractionalSpreadAssignment {
        val assignment = FractionalSpreadAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            state = row.state,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            rate = row.rate
        )
        assignment.initRefreshNotifierId(row.refreshSchedulingId)
        return assignment
    }

    override fun map(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentRow {
        return FractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            orderDirection = assignment.direction,
            rate = assignment.rate,
            refreshSchedulingPeriod = null,
            refreshSchedulingTaskId = null,
            refreshSchedulingState = null,
            refreshSchedulingId = assignment.getRefreshNotifierId(),
            orderId = assignment.info.orderId
        )
    }
}