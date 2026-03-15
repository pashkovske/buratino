package ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment

@Service
class TopPriceAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >() {

    override fun map(row: TopPriceAssignmentRow): TopPriceAssignment {
        val refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties? = mapRefreshSchedulingProperties(row)
        val assignment = TopPriceAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            state = row.state,
            refreshAssignmentSchedulingProperties = refreshAssignmentSchedulingProperties,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            oneStepOver = row.oneStepOver,
        )
        initRefreshSchedulingInfo(row, assignment, refreshAssignmentSchedulingProperties)
        return assignment
    }

    override fun map(assignment: TopPriceAssignment): TopPriceAssignmentRow {
        return TopPriceAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            orderDirection = assignment.direction,
            oneStepOver = assignment.oneStepOver,
            refreshSchedulingPeriod = assignment.refreshAssignmentSchedulingProperties?.period,
            refreshSchedulingTaskId = assignment.getRefreshAssignmentScheduling()?.taskId,
            refreshSchedulingState = assignment.getRefreshAssignmentScheduling()?.state,
            orderId = assignment.info.orderId
        )
    }
}
