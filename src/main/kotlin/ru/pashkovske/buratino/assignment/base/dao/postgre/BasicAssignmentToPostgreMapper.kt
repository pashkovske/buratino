package ru.pashkovske.buratino.assignment.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class BasicAssignmentToPostgreMapper<
    A : Assignment,
    Row : AssignmentPostgreRow<A>
    > : AssignmentToPostgreMapper<A, Row> {

    protected fun mapIid(iid: String): InstrumentId {
        return InstrumentId(iid)
    }

    protected fun mapRefreshSchedulingProperties(row: Row): SchedulingProperties? {
        return row.refreshSchedulingPeriod?.let {
            SchedulingProperties(
                interval = it
            )
        }
    }

    private fun mapRefreshSchedulingInfo(
        row: Row,
        properties: SchedulingProperties
    ): SchedulingInfo? {
        return if (row.refreshSchedulingTaskId == null && row.refreshSchedulingState == null) {
            null
        } else if (row.refreshSchedulingTaskId != null && row.refreshSchedulingState != null) {
            SchedulingInfo(
                properties = properties,
                taskId = row.refreshSchedulingTaskId!!,
                status = row.refreshSchedulingState!!
            )
        } else {
            throw IllegalStateException("Refresh scheduling task id and status are not consistent")
        }
    }

    protected fun initRefreshSchedulingInfo(
        row: Row,
        assignment: A,
        refreshSchedulingProperties: SchedulingProperties?
    ) {
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
    }
}
