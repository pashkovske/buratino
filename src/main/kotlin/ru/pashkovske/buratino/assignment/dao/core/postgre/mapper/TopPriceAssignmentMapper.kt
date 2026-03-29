package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.dao.core.postgre.row.TopPriceAssignmentRow
import ru.pashkovske.buratino.assignment.model.core.TopPriceAssignment
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
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