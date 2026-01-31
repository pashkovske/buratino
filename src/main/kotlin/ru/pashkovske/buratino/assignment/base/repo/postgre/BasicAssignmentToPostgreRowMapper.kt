package ru.pashkovske.buratino.assignment.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.limit.spread.fraction.repo.postgre.FractionalSpreadAssignmentRow
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class BasicAssignmentToPostgreRowMapper<A : Assignment, Row> : AssignmentToPostgreRowMapper<A, Row> {

    protected fun mapIid(iid: String): InstrumentId {
        return InstrumentId(iid)
    }

    protected abstract fun mapRefreshSchedulingProperties(row: Row): SchedulingProperties?

    protected fun mapRefreshSchedulingInfo(
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
