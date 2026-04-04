package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.exception.AssignmentDaoOperationInconsistencyException
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties
import java.util.UUID

abstract class ContinuousAssignmentToPostgreMapper<
    ChildA : Assignment,
    ContinuousA : ContinuousAssignment<ChildA>,
    ContinuousRow : ContinuousAssignmentPostgreRow<ChildA, ContinuousA>
    > : ParentAssignmentToPostgreMapper<
    ChildA,
    ContinuousA,
    ContinuousRow
    >() {

    protected fun mapContinueSchedulingProperties(row: ContinuousRow): AssignmentSchedulingProperties? {
        return row.continueSchedulingPeriod?.let {
            PeriodicAssignmentSchedulingProperties(
                period = it
            )
        }
    }

    private fun mapContinueSchedulingInfo(
        row: ContinuousRow,
        properties: AssignmentSchedulingProperties
    ): AssignmentScheduling? {
        val continueSchedulingId: UUID = row.continueSchedulingId ?: return null
        val continueSchedulingState: SchedulingState = row.continueSchedulingState ?: throw AssignmentDaoOperationInconsistencyException(
            message = "Continue id is not provided but state is",
            assignmentId = row.id
        )
        return when(properties) {
            is PeriodicAssignmentSchedulingProperties -> {
                PeriodicAssignmentScheduling(
                    id = continueSchedulingId,
                    assignmentId = row.id,
                    properties = properties,
                    taskId = row.continueSchedulingTaskId,
                    state = continueSchedulingState
                )
            }
        }
    }

    protected fun initContinueSchedulingInfo(
        continuousRow: ContinuousRow,
        continuousAssignment: ContinuousA,
        continueAssignmentSchedulingProperties: AssignmentSchedulingProperties?
    ) {
        if (continueAssignmentSchedulingProperties == null) {
            return
        }
        val continueAssignmentScheduling: AssignmentScheduling? = mapContinueSchedulingInfo(
            row = continuousRow,
            properties = continueAssignmentSchedulingProperties
        )
        if (continueAssignmentScheduling != null) {
            continuousAssignment.initContinueScheduling(
                assignmentScheduling = continueAssignmentScheduling
            )
        }
    }
}