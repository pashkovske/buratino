package ru.pashkovske.buratino.assignment.limit.spread.fraction.repo

import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo
import ru.pashkovske.buratino.assignment.limit.spread.fraction.model.FractionalSpreadAssignment
import ru.pashkovske.buratino.instrument.model.InstrumentId

object FractionalSpreadAssignmentMapper {

    fun map(row: FractionalSpreadAssignmentRow): FractionalSpreadAssignment {
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
        if (refreshSchedulingProperties != null) {
            val refreshSchedulingInfo: SchedulingInfo? = mapRefreshSchedulingInfo(
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

    fun map(assignment: FractionalSpreadAssignment): FractionalSpreadAssignmentRow {
        return FractionalSpreadAssignmentRow(
            id = assignment.id,
            instrumentId = assignment.iid.id,
            status = assignment.status,
            orderDirection = assignment.direction,
            rate = assignment.rate,
            refreshSchedulingPeriod = assignment.refreshSchedulingProperties?.interval,
            refreshSchedulingTaskId = assignment.getRefreshSchedulingInfo()?.taskId,
            refreshSchedulingStatus = assignment.getRefreshSchedulingInfo()?.status,
            orderId = assignment.info.orderId,
            lastOrderUpdate = assignment.info.lastUpdate
        )
    }

    private fun mapIid(iid: String): InstrumentId {
        return InstrumentId(iid)
    }

    private fun mapRefreshSchedulingProperties(row: FractionalSpreadAssignmentRow): SchedulingProperties? {
        return row.refreshSchedulingPeriod?.let {
            SchedulingProperties(
                interval = it
            )
        }
    }

    private fun mapOrderInfo(row: FractionalSpreadAssignmentRow): OrderInfo {
        return OrderInfo(
            orderId = row.orderId,
            lastUpdate = row.lastOrderUpdate
        )
    }

    private fun mapRefreshSchedulingInfo(
        row: FractionalSpreadAssignmentRow,
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
}
