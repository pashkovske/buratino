package ru.pashkovske.buratino.assignment.limit.spread.fraction.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

@Service
class FractionalSpreadAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >() {

    override fun map(row: FractionalSpreadAssignmentRow): FractionalSpreadAssignment {
        val refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties? = mapRefreshSchedulingProperties(row)
        val assignment = FractionalSpreadAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            state = row.state,
            refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            rate = row.rate,
        )
        initRefreshSchedulingInfo(row, assignment, refreshAssignmentSchedulingProperties)
        return assignment
    }

    override fun map(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentRow {
        return FractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            orderDirection = assignment.direction,
            rate = assignment.rate,
            refreshSchedulingPeriod = assignment.refreshAssignmentSchedulingProperties?.period,
            refreshSchedulingTaskId = assignment.getRefreshAssignmentScheduling()?.taskId,
            refreshSchedulingState = assignment.getRefreshAssignmentScheduling()?.state,
            orderId = assignment.info.orderId
        )
    }
}
