package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.repo.postgre.LimitOrderAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment

@Service
class FractionalSpreadAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    FractionalSpreadAssignment,
    FractionalSpreadAssignmentRow
    >() {

    override fun map(row: FractionalSpreadAssignmentRow): FractionalSpreadAssignment {
        val refreshSchedulingProperties: SchedulingProperties? = mapRefreshSchedulingProperties(row)
        val assignment = FractionalSpreadAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            status = row.status,
            refreshSchedulingProperties = refreshSchedulingProperties,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            rate = row.rate,
        )
        initRefreshSchedulingInfo(row, assignment, refreshSchedulingProperties)
        return assignment
    }

    override fun map(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentRow {
        return FractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            status = assignment.status,
            orderDirection = assignment.direction,
            rate = assignment.rate,
            refreshSchedulingPeriod = assignment.refreshSchedulingProperties?.interval,
            refreshSchedulingTaskId = assignment.getRefreshSchedulingInfo()?.taskId,
            refreshSchedulingStatus = assignment.getRefreshSchedulingInfo()?.status,
            orderId = assignment.info.orderId
        )
    }
}
