package ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment
import java.time.Duration

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
        val refreshPeriod: Duration? = when (assignment.refreshAssignmentSchedulingProperties) {
            null -> null
            is PeriodicAssignmentSchedulingProperties -> assignment.refreshAssignmentSchedulingProperties.period
        }
        return TopPriceAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            state = assignment.state,
            orderDirection = assignment.direction,
            oneStepOver = assignment.oneStepOver,
            refreshSchedulingPeriod = refreshPeriod,
            refreshSchedulingTaskId = assignment.getRefreshAssignmentScheduling()?.taskId,
            refreshSchedulingState = assignment.getRefreshAssignmentScheduling()?.state,
            refreshSchedulingId = assignment.getRefreshAssignmentScheduling()?.id,
            orderId = assignment.info.orderId
        )
    }
}
