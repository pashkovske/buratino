package ru.pashkovske.buratino.assignment.limit.top.price.dao.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.dao.postgre.LimitOrderAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceAssignment

@Service
class TopPriceAssignmentMapper : LimitOrderAssignmentToPostgreMapper<
    TopPriceAssignment,
    TopPriceAssignmentRow
    >() {

    override fun map(row: TopPriceAssignmentRow): TopPriceAssignment {
        val refreshSchedulingProperties: SchedulingProperties? = mapRefreshSchedulingProperties(row)
        val assignment = TopPriceAssignment(
            id = row.id,
            iid = mapIid(row.instrumentId),
            status = row.status,
            refreshSchedulingProperties = refreshSchedulingProperties,
            direction = row.orderDirection,
            info = mapOrderInfo(row),
            oneStepOver = row.oneStepOver,
        )
        initRefreshSchedulingInfo(row, assignment, refreshSchedulingProperties)
        return assignment
    }

    override fun map(assignment: TopPriceAssignment): TopPriceAssignmentRow {
        return TopPriceAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            status = assignment.status,
            orderDirection = assignment.direction,
            oneStepOver = assignment.oneStepOver,
            refreshSchedulingPeriod = assignment.refreshSchedulingProperties?.interval,
            refreshSchedulingTaskId = assignment.getRefreshSchedulingInfo()?.taskId,
            refreshSchedulingStatus = assignment.getRefreshSchedulingInfo()?.status,
            orderId = assignment.info.orderId
        )
    }
}
