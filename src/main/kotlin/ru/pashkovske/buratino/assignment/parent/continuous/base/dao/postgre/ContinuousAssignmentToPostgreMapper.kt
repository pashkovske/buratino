package ru.pashkovske.buratino.assignment.parent.continuous.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.model.scheduling.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.model.scheduling.properties.AssignmentSchedulingProperties
import ru.pashkovske.buratino.assignment.parent.base.dao.postgre.ParentAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.parent.continuous.base.model.ContinuousAssignment

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
            AssignmentSchedulingProperties(
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
            AssignmentScheduling(
                properties = properties,
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