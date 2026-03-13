package ru.pashkovske.buratino.assignment.`super`.continuous.base.dao.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.model.AssignmentScheduling
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.`super`.base.dao.postgre.SuperAssignmentToPostgreMapper
import ru.pashkovske.buratino.assignment.`super`.continuous.base.model.ContinuousAssignment

abstract class ContinuousAssignmentToPostgreMapper<
    NestedA : Assignment,
    ContinuousA : ContinuousAssignment<NestedA>,
    ContinuousRow : ContinuousAssignmentPostgreRow<NestedA, ContinuousA>
    > : SuperAssignmentToPostgreMapper<
    NestedA,
    ContinuousA,
    ContinuousRow
    >() {

    protected fun mapContinueSchedulingProperties(row: ContinuousRow): SchedulingProperties? {
        return row.continueSchedulingPeriod?.let {
            SchedulingProperties(
                interval = it
            )
        }
    }

    private fun mapContinueSchedulingInfo(
        row: ContinuousRow,
        properties: SchedulingProperties
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
        continueSchedulingProperties: SchedulingProperties?
    ) {
        if (continueSchedulingProperties == null) {
            return
        }
        val continueAssignmentScheduling: AssignmentScheduling? = mapContinueSchedulingInfo(
            row = continuousRow,
            properties = continueSchedulingProperties
        )
        if (continueAssignmentScheduling != null) {
            continuousAssignment.initContinueScheduling(
                assignmentScheduling = continueAssignmentScheduling
            )
        }
    }
}