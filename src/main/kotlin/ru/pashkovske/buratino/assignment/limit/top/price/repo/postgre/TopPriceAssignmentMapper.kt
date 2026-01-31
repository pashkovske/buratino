package ru.pashkovske.buratino.assignment.limit.top.price.repo.postgre

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo
import ru.pashkovske.buratino.assignment.limit.base.repo.postgre.LimitOrderAssignmentToPostgreMapper
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
        if (refreshSchedulingProperties != null) {
            val refreshSchedulingInfo: SchedulingInfo? = mapRefreshSchedulingInfoForTopPrice(
                row = row,
                properties = refreshSchedulingProperties
            )
            if (refreshSchedulingInfo != null) {
                assignment.initRefreshScheduling(
                    schedulingInfo = refreshSchedulingInfo
                )
            }
        }
        return assignment
    }

    private fun mapRefreshSchedulingInfoForTopPrice(
        row: TopPriceAssignmentRow,
        properties: SchedulingProperties
    ): SchedulingInfo? {
        return if (row.refreshSchedulingTaskId == null && row.refreshSchedulingStatus == null) {
            null
        } else if (row.refreshSchedulingTaskId != null && row.refreshSchedulingStatus != null) {
            SchedulingInfo(
                properties = properties,
                taskId = row.refreshSchedulingTaskId,
                status = row.refreshSchedulingStatus
            )
        } else {
            throw IllegalStateException("Refresh scheduling task id and status are not consistent")
        }
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
            orderId = assignment.info.orderId,
            lastOrderUpdate = assignment.info.lastUpdate
        )
    }

    override fun mapRefreshSchedulingProperties(row: TopPriceAssignmentRow): SchedulingProperties? {
        return row.refreshSchedulingPeriod?.let {
            SchedulingProperties(
                interval = it
            )
        }
    }

    override fun mapOrderInfo(row: TopPriceAssignmentRow): OrderInfo {
        return OrderInfo(
            orderId = row.orderId,
            lastUpdate = row.lastOrderUpdate
        )
    }
}
