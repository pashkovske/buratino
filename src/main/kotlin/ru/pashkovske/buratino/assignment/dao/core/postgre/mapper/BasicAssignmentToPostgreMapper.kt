package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.AssignmentPostgreRow
import ru.pashkovske.buratino.assignment.exception.AssignmentDaoOperationInconsistencyException
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import ru.pashkovske.buratino.instrument.model.InstrumentId
import java.util.UUID

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
        val refreshSchedulingId: UUID = row.refreshSchedulingId ?: return null
        val refreshSchedulingState: SchedulingState = row.refreshSchedulingState ?: throw AssignmentDaoOperationInconsistencyException(
            message = "Refresh id is not provided but state is",
            assignmentId = row.id
        )
        return when(properties) {
            is PeriodicAssignmentSchedulingProperties -> {
                PeriodicAssignmentScheduling(
                    id = refreshSchedulingId,
                    assignmentId = row.id,
                    properties = properties,
                    taskId = row.refreshSchedulingTaskId,
                    state = refreshSchedulingState
                )
            }
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
