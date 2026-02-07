package ru.pashkovske.buratino.assignment.`super`.continuous.base.repo.postgre

import ru.pashkovske.buratino.assignment.base.model.Assignment
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingInfo
import ru.pashkovske.buratino.assignment.base.scheduling.model.SchedulingProperties
import ru.pashkovske.buratino.assignment.`super`.base.repo.postgre.SuperAssignmentToPostgreMapper
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
    ): SchedulingInfo? {
        return if (row.continueSchedulingTaskId == null && row.continueSchedulingStatus == null) {
            null
        } else if (row.continueSchedulingTaskId != null && row.continueSchedulingStatus != null) {
            SchedulingInfo(
                properties = properties,
                taskId = row.continueSchedulingTaskId!!,
                status = row.continueSchedulingStatus!!
            )
        } else {
            throw IllegalStateException("Continue scheduling task id and status are not consistent")
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
        val continueSchedulingInfo: SchedulingInfo? = mapContinueSchedulingInfo(
            row = continuousRow,
            properties = continueSchedulingProperties
        )
        if (continueSchedulingInfo != null) {
            continuousAssignment.initContinueScheduling(
                schedulingInfo = continueSchedulingInfo
            )
        }
    }
}