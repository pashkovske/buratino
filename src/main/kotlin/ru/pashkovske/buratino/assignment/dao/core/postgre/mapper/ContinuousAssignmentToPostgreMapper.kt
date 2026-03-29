package ru.pashkovske.buratino.assignment.dao.core.postgre.mapper

import ru.pashkovske.buratino.assignment.dao.core.postgre.row.ContinuousAssignmentPostgreRow
import ru.pashkovske.buratino.assignment.model.core.Assignment
import ru.pashkovske.buratino.assignment.model.core.ContinuousAssignment
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.model.notify.properties.PeriodicAssignmentSchedulingProperties

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
        return if (row.continueSchedulingTaskId == null && row.continueSchedulingState == null) {
            null
        } else if (row.continueSchedulingTaskId != null && row.continueSchedulingState != null) {
            if (row.continueSchedulingId == null) {
                throw IllegalStateException("Continue scheduling id is required but not provided")
            }
            PeriodicAssignmentScheduling(
                id = row.continueSchedulingId!!,
                assignmentId = row.id,
                properties = properties as PeriodicAssignmentSchedulingProperties,
                taskId = row.continueSchedulingTaskId!!,
                state = row.continueSchedulingState!!
            )
        } else {
            throw IllegalStateException("Continue scheduling task id and state are not consistent")
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