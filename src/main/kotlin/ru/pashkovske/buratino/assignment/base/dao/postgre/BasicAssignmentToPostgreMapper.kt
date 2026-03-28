package ru.pashkovske.buratino.assignment.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId

abstract class BasicAssignmentToPostgreMapper<
    A : Assignment,
    Row : AssignmentPostgreRow<A>
    > : AssignmentToPostgreMapper<A, Row> {

    protected fun mapIid(iid: String): InstrumentId {
        return InstrumentId(iid)
    }

    protected fun mapRefreshSchedulingProperties(row: Row): AssignmentSchedulingProperties? {
        return row.refreshSchedulingPeriod?.let {
            PeriodicAssignmentSchedulingProperties(
                period = it
            )
        }
    }

    private fun mapRefreshSchedulingInfo(
        row: Row,
        properties: AssignmentSchedulingProperties
    ): AssignmentScheduling? {
        return if (row.refreshSchedulingTaskId == null && row.refreshSchedulingState == null) {
            null
        } else if (row.refreshSchedulingTaskId != null && row.refreshSchedulingState != null) {
            if (row.refreshSchedulingId == null) {
                throw IllegalStateException("Refresh scheduling id is required but not provided")
            }
            PeriodicAssignmentScheduling(
                id = row.refreshSchedulingId!!,
                assignmentId = row.id,
                properties = properties as PeriodicAssignmentSchedulingProperties,
                taskId = row.refreshSchedulingTaskId!!,
                state = row.refreshSchedulingState!!
            )
        } else {
            throw IllegalStateException("Refresh scheduling task id and state are not consistent")
        }
    }

    protected fun initRefreshSchedulingInfo(
        row: Row,
        assignment: A,
        refreshAssignmentSchedulingProperties: AssignmentSchedulingProperties?
    ) {
        if (refreshAssignmentSchedulingProperties != null) {
            val refreshAssignmentScheduling: AssignmentScheduling? = mapRefreshSchedulingInfo(
                row = row,
                properties = refreshAssignmentSchedulingProperties
            )
            if (refreshAssignmentScheduling != null) {
                assignment.initRefreshScheduling(
                    assignmentScheduling = refreshAssignmentScheduling
                )
            }
        }
    }
}
